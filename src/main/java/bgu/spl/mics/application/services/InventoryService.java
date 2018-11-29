package bgu.spl.mics.application.services;

import bgu.spl.mics.CheckAvailabilityEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	public InventoryService() {
		super("Inventory");
		// TODO Implement this
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		// TODO Implement this
		System.out.println("Event Handler " + getName() + " started");

		subscribeEvent(CheckAvailabilityEvent.class, event -> {
			int price = inventory.checkAvailabiltyAndGetPrice(event.getName());

				complete(event, "Hello from " + getName());
				terminate();


		});
	}

}
