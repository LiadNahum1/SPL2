package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

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
private CountDownLatch countDownLatch;
    public SellingService(int num , CountDownLatch countDownLatch) {
        super("SellingService" + num);
        this.countDownLatch = countDownLatch;
        moneyReg = MoneyRegister.getInstance();
        this.currentTick = 1;
    }

    @Override
    protected void initialize() {
        System.out.println("Event Handler " + getName() + " started");
        subscribeOrderBookEvent();
        subscribeTickBroadcast();
        subscribeBroadcast(TerminateBroadcast.class , broadcast-> {
            terminate();});
    countDownLatch.countDown();
    }

    //subscribe to BookOrderEvent
    private void subscribeOrderBookEvent() {
        subscribeEvent(BookOrderEvent.class, event -> {
            //check availability and get price
            Integer bookPrice = sendEvent(new CheckAvailabilityEvent(event.getBookTitle())).get();
            //if book is available
            if (bookPrice != null && bookPrice != -1) {
                Customer customer = event.getCustomer();
                synchronized (customer.getMoneyLock()) {
                    int amountOfMoney = customer.getAvailableCreditAmount();

                    if (amountOfMoney >= bookPrice) {
                        //take the book
                        Future<OrderResult> sucessfulTaken = sendEvent(new TakeBookEvent(event.getBookTitle()));
                        //if succeed
                        OrderResult r = sucessfulTaken.get();

                        if (r!=null && r == OrderResult.SUCCESSFULLY_TAKEN) {
                            moneyReg.chargeCreditCard(customer, bookPrice); //charging customer
                            OrderReceipt receipt = new OrderReceipt(0, getName(), customer.getId(), event.getBookTitle(), bookPrice,
                                    this.currentTick, event.getTick(), this.currentTick);
                            complete(event, receipt);
                            sendEvent(new DeliveryEvent(customer.getAddress(), customer.getDistance()));

                            moneyReg.file(receipt); //saves receipt in money register
                        } else {
                            complete(event, null);
                        }
                    } else {
                        complete(event, null);
                    }

                }
            } else {
                complete(event, null);
            }
        });
    }

    //subscribe to TickBroadcast
    private void subscribeTickBroadcast() {
        subscribeBroadcast(TickBroadcast.class, broadcast -> {
            this.currentTick = broadcast.getCurrentTick();
        });
    }
}





