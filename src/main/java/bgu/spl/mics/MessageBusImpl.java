package bgu.spl.mics;

import bgu.spl.mics.Messages.Broadcast;
import bgu.spl.mics.Messages.Event;
import bgu.spl.mics.Messages.Message;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService,BlockingQueue<Message>>missionsToService;
	private ConcurrentHashMap<Class<? extends Message>,BlockingQueue<MicroService>>servisesToEvents;
	private ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>>servisesToBrodcasts;
    private ConcurrentHashMap<Message,Future>futersOfEvents;
    //thread safe singelton
	private static class SingletonHolder {
		private static MessageBusImpl
				instance = new MessageBusImpl();}
	private MessageBusImpl() {
		missionsToService = new ConcurrentHashMap<>();
		servisesToEvents = new ConcurrentHashMap<>();
		servisesToBrodcasts = new ConcurrentHashMap<>();
		futersOfEvents = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized(servisesToEvents) { //TODO:check if needed
			if (servisesToEvents.containsKey(type)) { //if the event already as a servise then add this to the queueu
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
			if (servisesToBrodcasts.containsKey(type)) { //if the event already as a servise then add this to the list
				servisesToBrodcasts.get(type).add(m);
			} else //create a new queue to this events queueu
			{
				Vector<MicroService> list = new Vector<>();
				list.add(m);
				servisesToBrodcasts.put(type, list);
			}
		}
	}

	@Override //TODO:should i synchronize
	public <T> void complete(Event<T> e, T result) {
	futersOfEvents.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (servisesToBrodcasts) {
			if (servisesToBrodcasts.get(b.getClass()) != null) {
				Iterator itr = servisesToBrodcasts.get(b.getClass()).iterator();
				while (itr.hasNext()) {
					synchronized (missionsToService) {
					missionsToService.get(itr.next()).add(b);
					missionsToService.notifyAll();

					}
				}
			}
		}
	}


	//sends it to the queue of the apropriate microservies
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> fu = new Future<>();
		synchronized (futersOfEvents) {
           futersOfEvents.put(e, fu);
        }
        if(servisesToEvents.get(e.getClass())!= null) {
			MicroService execute = servisesToEvents.get(e.getClass()).poll(); //this queue will not be deleted
			servisesToEvents.get(e.getClass()).add(execute);
			synchronized (missionsToService) {
				missionsToService.get(execute).add(e); //should i synchronize this?
				missionsToService.notifyAll();
			}
			return fu;
		}
		else return null;
	}

	@Override
	public void register(MicroService m) { //TODO: should check if try to register twice?
        missionsToService.put(m,new LinkedBlockingQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
        //TODO:check if needed
		synchronized (missionsToService) {
			if (missionsToService.containsKey(m)) {
				//remove m from servises queue
				missionsToService.remove(m);
			}
		}
                //unassign M from all events
                synchronized (servisesToEvents){
					Set<Class<? extends Message>> keys = servisesToEvents.keySet();
					for(Class<? extends Message> classMsg : keys){
						if(servisesToEvents.get(classMsg).contains(m))
							servisesToEvents.get(classMsg).remove(m);
					}

                }
                //unassign M from all brodcasts
                synchronized (servisesToBrodcasts){
					Set<Class<? extends Message>> keys = servisesToBrodcasts.keySet();
					for(Class<? extends Message> classMsg : keys){
						if(servisesToBrodcasts.get(classMsg).contains(m))
							servisesToBrodcasts.get(classMsg).remove(m);
					}                }

    }

	@Override
	//return the next mission and wait for it
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (missionsToService) { //TODO: throw the exeption
			while (missionsToService.get(m).isEmpty() == true) {
				missionsToService.wait();
			}
			return missionsToService.get(m).take();
		}
	}

}
