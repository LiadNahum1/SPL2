package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.*;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@linkResourcesHolder}, {@linkInventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private MoneyRegister moneyReg;
	private int currentTick;

	public SellingService() {
		super("SellingService");
		moneyReg = MoneyRegister.getInstance();
		this.currentTick = 0;
	}

	@Override
	protected void initialize() {
		System.out.println("Event Handler " + getName() + " started");
        subscribeOrderBookEvent();
        subscribeTickBroadcast();
	}
    //subscribe to BookOrderEvent
    private void subscribeOrderBookEvent(){
        subscribeEvent(BookOrderEvent.class, event-> {
            //check availability and get price
            Future<Integer> futureObj = (Future<Boolean>) sendEvent(new CheckAvailabilityEvent(event.getBook()));
            //Boolean result = futureObj.get(); //blocking method until the Future is resolved
            //if (result) {
            Customer c = event.getCustomer();
            BookInventoryInfo b = event.getBook();

            int price = b.getPrice();

            //check if customer has enough money
            if (c.getAvailableCreditAmount() >= price) {
                moneyReg.chargeCreditCard(c, price); //charging customer
                //take the book
                sendEvent(new TakeBookEvent(b.getBookTitle()));
                //TODO: add event that take the book
                // OrderReceipt receipt = new OrderReceipt(event.getOrderId(), getName(), c.getId(), b.getBookTitle(), b.getPrice(), 1, issuedTick, processTick);
                //  complete(event, receipt);
            } else {
                complete(event, null);
            }
        //}
          //  else{complete(event, null);}
        });
    }
    //subscribe to TickBroadcast
    private void subscribeTickBroadcast(){
	    subscribeBroadcast(TickBroadcast.class, broadcast-> {
	        this.currentTick = broadcast.getCurrentTick();
        });
    }


}


