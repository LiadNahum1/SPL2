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

	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> missionsToService;
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> servisesToEvents;
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> servisesToBrodcasts;
	private ConcurrentHashMap<Message, Future> futersOfEvents;

	//thread safe singelton
	private static class SingletonHolder {
		private static MessageBusImpl
				instance = new MessageBusImpl();
	}

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
		synchronized (servisesToEvents) {
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
		synchronized (servisesToBrodcasts) {
			if (servisesToBrodcasts.containsKey(type)) { //if the event already as a servise then add this to the list
				servisesToBrodcasts.get(type).add(m);
			} else //create a new queue to this events queueu
			{
				BlockingQueue<MicroService> queue = new LinkedBlockingQueue<>();
				queue.add(m);
				servisesToBrodcasts.put(type, queue);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futersOfEvents.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		if (servisesToBrodcasts.get(b.getClass()) != null) {
			if (!servisesToBrodcasts.get(b.getClass()).isEmpty()) {

				for (MicroService service : servisesToBrodcasts.get(b.getClass())){
					BlockingQueue<Message> serviceQue = missionsToService.get(service);
					if (serviceQue != null) {
						synchronized (missionsToService) {
							serviceQue.add(b);
						}
					}
				}
			}
		}
	}


	//sends it to the queue of the apropriate microservies
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		Future<T> fu = new Future<>();
		futersOfEvents.put(e, fu);
		BlockingQueue<MicroService> servicesQue = servisesToEvents.get(e.getClass());
		MicroService execute;
		if (servicesQue != null) {
			synchronized (servicesQue) {
				if (!servicesQue.isEmpty()) {
					execute = servicesQue.remove();
					servicesQue.add(execute);
					synchronized (execute) {
						synchronized (missionsToService.get(execute)) {
							if (missionsToService.get(execute) != null) {
								missionsToService.get(execute).add(e);
								return fu;
							}
						}
					}


				}
			}
		}
		return null;
	}


	@Override
	public void register(MicroService m) {
		missionsToService.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for ( BlockingQueue<MicroService> eventQueue :servisesToEvents.values()){
			synchronized(eventQueue) {
				eventQueue.remove(m);
			}
		}
		for ( BlockingQueue<MicroService> bQueue : servisesToBrodcasts.values()){
			synchronized(bQueue) {
				bQueue.remove(m);
			}
		}

		//resolve all unresolved future object the the service is responsible for
		BlockingQueue<Message> toR = missionsToService.get(m);
		while(!toR.isEmpty())

		{
			futersOfEvents.get(toR.remove()).resolve(null);
		}

		missionsToService.remove(m); //removes service itself and its queue of events from hash map

	}


	@Override
	//return the next mission and wait for it
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(missionsToService.get(m) == null)
			throw new IllegalStateException();

		return missionsToService.get(m).take(); //blocking method waits until there is a message in the queue of the service

	}

}
