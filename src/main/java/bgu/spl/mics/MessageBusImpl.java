package bgu.spl.mics;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService,BlockingQueue<Message>>missionsToService;
	private ConcurrentHashMap<Class<? extends Message>,BlockingQueue<MicroService>>servisesToEvents;
	private ConcurrentHashMap<Class<? extends Message>,ConcurrentSkipListSet<MicroService>>servisesToBrodcasts;
    private ConcurrentHashMap<Message,Future>futersOfEvents;
    private static MessageBusImpl instance = null;
	//thread safe singelton
	private static class SingletonHolder {
		private static MessageBusImpl
				instance = new MessageBusImpl();}
	private MessageBusImpl() {}
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized(servisesToEvents) { //TODO:check if needed
			if (servisesToEvents.contains(type)) { //if the event already as a servise then add this to the queueu
				servisesToEvents.get(type).add(m);
			} else //create a new queue to this events queueu
			{
				BlockingQueue<MicroService> queue = new LinkedBlockingQueue<>();
				queue.add(m);
				servisesToEvents.put(type, queue);
			}
		}
		}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized(servisesToBrodcasts) { //TODO:check if needed
			if (servisesToBrodcasts.contains(type)) { //if the event already as a servise then add this to the list
				servisesToBrodcasts.get(type).add(m);
			} else //create a new queue to this events queueu
			{
				ConcurrentSkipListSet<MicroService> list = new ConcurrentSkipListSet<>();
				list.add(m);
				servisesToBrodcasts.put(type, list);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
	synchronized (futersOfEvents){
	futersOfEvents.get(e).resolve(result);
	}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (servisesToBrodcasts){
		Iterator itr = servisesToBrodcasts.get(b).iterator();
			while(itr.hasNext()){
				synchronized (missionsToService) {
					missionsToService.get(itr.next()).add(b);//TODO: I don't get why this works;
				}
			}
		}
	}


	//sends it to the queue of the apropriate microservies
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
	    synchronized (servisesToEvents) {
            MicroService execute = servisesToEvents.get(e.getClass()).poll();
            missionsToService.get(execute).add(e); //should i synchronize this?
        }
        synchronized (futersOfEvents) {
            Future<T> fu = new Future<>();
            futersOfEvents.put(e, fu);
            notifyAll();
            return fu;
        }

    }

	@Override
	public void register(MicroService m) { //TODO: should check if try to register twice?
        missionsToService.put(m,new LinkedBlockingQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
        //TODO:check if needed
            if(missionsToService.contains(m)){
                //remove m from servises queue
                synchronized(missionsToService) {
                    missionsToService.remove(m);
                }
                //unassign M from all events
                synchronized (servisesToEvents){
                    Iterator<Map.Entry<Class<? extends Message>, BlockingQueue<MicroService>>> itr = servisesToEvents.entrySet().iterator();

                    while(itr.hasNext())
                    {
                        Map.Entry<Class<? extends Message>, BlockingQueue<MicroService>> entry = itr.next();
                        if(entry.getValue().contains(m))
                            entry.getValue().remove(m);
                        itr.remove();
                    }
                }
                //unassign M from all brodcasts
                synchronized (servisesToBrodcasts){
                    Iterator<Map.Entry<Class<? extends Message>, ConcurrentSkipListSet<MicroService>>> itr = servisesToBrodcasts.entrySet().iterator();

                    while(itr.hasNext())
                    {
                        Map.Entry<Class<? extends Message>, ConcurrentSkipListSet<MicroService>> entry = itr.next();
                        if(entry.getValue().contains(m))
                            entry.getValue().remove(m);
                        itr.remove();
                    }
                }
        }
    }

	@Override
	//return the next mission and wait for it
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (missionsToService) {
			while (missionsToService.get(m).isEmpty() == true) {
				wait();
			}
			notifyAll();
			return missionsToService.get(m).take();
		}
	}

}
