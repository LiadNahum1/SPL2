package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.BookOrderEvent;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	ConcurrentHashMap<Integer,Vector<String>> orderingOrder;
	Vector<Future<OrderReceipt>> futures;

	int timeout;
	int currentTick = 1;
	Customer cs;
	public APIService() {
		super("Change_This_Name");
	orderingOrder = new ConcurrentHashMap<>();
	}

	@Override
	protected void initialize() {
		 subscribeBroadcast(TickBroadcast.class, broadcast-> {
			this.currentTick = broadcast.getCurrentTick();
		});
		 while(this.currentTick < timeout){
		 	if(orderingOrder.containsKey(currentTick)){
				Vector<String> orders =  orderingOrder.get(currentTick);
				for(String st : orders){
					futures.add(sendEvent(new BookOrderEvent(cs,st,currentTick)));
				}
			}
		 }
		 for(Future<OrderReceipt> or : futures){
		 	 OrderReceipt completed = or.get();
		 	 if(completed!= null) {
				 cs.addRecipt(completed);

			 }
		 	 }
	}

}
