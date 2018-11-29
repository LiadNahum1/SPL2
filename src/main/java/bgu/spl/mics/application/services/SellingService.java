package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

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
				//check availability
				Future<Boolean> futureObj = (Future<Boolean>)sendEvent(new CheckAvailabilityEvent(event.getBook()));
				Boolean result  = futureObj.get(); //blocking method until the Future is resolved
				synchronized(event.getCustomer()) {
					Customer c = event.getCustomer();
					BookInventoryInfo b = event.getBook();
					//check if customer has enough money
					if (c.getAvailableCreditAmount() >= b.getPrice()) {
						moneyReg.chargeCreditCard(c, b.getPrice()); //charging customer
						//TODO: add event that take the book
						OrderReceipt receipt = new OrderReceipt(event.getOrderId(), getName(), c.getId(), b.getBookTitle(), b.getPrice(), );
						complete(event, receipt);
						//int orderId, String seller,int customerId, String bookTitle,int price, int issuedTick,
						//int orderTick, int proccessTick
					}
					else{
						complete(event, null);
					}



				}
			terminate();
		});
	}

}


