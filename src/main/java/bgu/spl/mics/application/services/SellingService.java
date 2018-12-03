package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.*;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Vector;

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
	private Vector<BookInventoryInfo> booksOnDiscount;

	public SellingService() {
		super("SellingService");
		// TODO Implement this
		moneyReg = MoneyRegister.getInstance();
		booksOnDiscount = new Vector<>();
	}

	@Override
	protected void initialize() {
		System.out.println("Event Handler " + getName() + " started");
        subscribeOrderBookEvent();
		subscribeFiftyPercentDiscountEvent();
	}
    //subscribe to BookOrderEvent
    private void subscribeOrderBookEvent(){
        subscribeEvent(BookOrderEvent.class, event-> {
            synchronized (this) {
                synchronized (event.getCustomer()) {
                    Future<Integer> processTickFuture = sendEvent(new CurrentTickEvet());
                    Integer processTick = processTickFuture.get();
                    //check availability
                    Future<Boolean> futureObj = (Future<Boolean>) sendEvent(new CheckAvailabilityEvent(event.getBook()));
                    Boolean result = futureObj.get(); //blocking method until the Future is resolved
                    if (result) {
                        Customer c = event.getCustomer();
                        BookInventoryInfo b = event.getBook();
                        //check if there is a discount on the book
                        int price = b.getPrice();
                        if (this.booksOnDiscount.contains(b)) {
                            price = b.getPrice() / 2; //TODO:: make it double?
                        }
                        //check if customer has enough money
                        if (c.getAvailableCreditAmount() >= price) {
                            moneyReg.chargeCreditCard(c, price); //charging customer
                            //take the book
                            sendEvent(new TakeBookEvent(b.getBookTitle()));
                            //TODO: add event that take the book
                            Future<Integer> issuedTickFuture = sendEvent(new CurrentTickEvet());
                            Integer issuedTick = issuedTickFuture.get();
                            OrderReceipt receipt = new OrderReceipt(event.getOrderId(), getName(), c.getId(), b.getBookTitle(), b.getPrice(), 1, issuedTick, processTick);
                            complete(event, receipt);
                        }
                        else {complete(event, null);}
                    }
                    else{complete(event, null);}
                }
            }
            //terminate();
        });
    }
    //subscribe to FiftyPercentDiscount Event
    private void subscribeFiftyPercentDiscountEvent(){
        subscribeBroadcast(FiftyPercentDiscount.class, event->{
            synchronized (event.getBooksOnDiscount()) {
                this.booksOnDiscount = event.getBooksOnDiscount();
            }
            //terminate();
        });
    }

}


