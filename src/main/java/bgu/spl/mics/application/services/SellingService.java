package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.*;
import bgu.spl.mics.application.passiveObjects.*;

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
            Integer bookPrice = sendEvent(new CheckAvailabilityEvent(event.getBookTitle())).get();
            //if book is available
            if(bookPrice != -1) {
                Customer customer = event.getCustomer();
                synchronized (customer.getMoneyLock()){ //TODO
                    int amountOfMoney = customer.getAvailableCreditAmount();
                    if(amountOfMoney >= bookPrice){
                        moneyReg.chargeCreditCard(customer, bookPrice); //charging customer
                        //take the book
                        Future<OrderResult> sucessfulTaken = sendEvent(new TakeBookEvent(event.getBookTitle()));
                        //if succeed
                        if(sucessfulTaken.get() == OrderResult.SUCCESSFULLY_TAKEN)
                        {
                            OrderReceipt receipt = new OrderReceipt(0, getName(), customer.getId(), event.getBookTitle(), bookPrice,
                                    this.currentTick, event.getTick(), this.currentTick);
                            complete(event, receipt);
                            moneyReg.file(receipt); //saves receipt in money register
                        }
                        else{complete(event, null);}
                    }
                    else{complete(event, null);}

                }
            }
            else{complete(event, null);}
        });
    }
    //subscribe to TickBroadcast
    private void subscribeTickBroadcast(){
	    subscribeBroadcast(TickBroadcast.class, broadcast-> {
	        this.currentTick = broadcast.getCurrentTick();
        });
    }


}


