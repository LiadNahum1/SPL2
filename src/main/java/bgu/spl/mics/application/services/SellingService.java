package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private MoneyRegister moneyReg;

	public SellingService() {
		super("SellingService");
		// TODO Implement this

		moneyReg = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		System.out.println("Event Handler " + getName() + " started");

		subscribeEvent(BookOrderEvent.class, event -> {
			//synchronized (this) {
				Future<Boolean> futureObj = (Future<Boolean>)sendEvent(new CheckAvailabilityEvent(event.getBookName()));
				while (!((Future) futureObj).isDone()) {
					try {
						this.wait();
					} catch (Exception e) {
					}
				}
				complete(event, "Hello from " + getName());
				terminate();
			//}
		});
	}

}


