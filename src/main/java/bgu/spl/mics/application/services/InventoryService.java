package bgu.spl.mics.application.services;

import bgu.spl.mics.Messages.CheckAvailabilityEvent;
import bgu.spl.mics.Messages.TerminateBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@linkResourcesHolder}, {@linkMoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	public InventoryService(int num) {
		super("Inventory" + num);
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println("Event Handler " + getName() + " started");

		//subscribes to CheckAvailability Event
		subscribeEvent(CheckAvailabilityEvent.class, event -> {
			int price = inventory.checkAvailabiltyAndGetPrice(event.getBookTitle());
			complete(event, price);
		});

		//subscribes to TakeBook Event
		subscribeEvent(TakeBookEvent.class, event -> {
			OrderResult orderRe =inventory.take(event.getBookTitle());
			complete(event, orderRe);
		});
		subscribeBroadcast(TerminateBroadcast.class , broadcast-> {terminate();});

	}

}
