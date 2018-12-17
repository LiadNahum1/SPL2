package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.BookOrderEvent;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Messages.TerminateBroadcast;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.OrderSchedule;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private Customer customer;
	private int currentTick;
	private ConcurrentHashMap<Integer, Vector<String>> orderingBooks;
	private Vector<Future<OrderReceipt>> futures;
	private CountDownLatch c;

	public APIService(Customer customer, OrderSchedule[] orders, CountDownLatch c) {
		super("APIService " + customer.getId());
		this.c = c;
		this.orderingBooks = new ConcurrentHashMap<>();
		for (int i = 0; i < orders.length; i = i + 1) {
			if (orderingBooks.get(orders[i].getTick()) == null) {
				Vector<String> booksInTick = new Vector<>();
				booksInTick.add(orders[i].getBookTitle());
				orderingBooks.put(orders[i].getTick(), booksInTick);
			} else {
				orderingBooks.get(orders[i].getTick()).add(orders[i].getBookTitle());
			}
		}
		this.customer = customer;
		this.currentTick = 1;
		this.futures = new Vector<>();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, broadcast -> {
			this.currentTick = broadcast.getCurrentTick();
			if (orderingBooks.containsKey(currentTick)) {
				Vector<String> orders = orderingBooks.get(currentTick);
				for (String st : orders) {
					sendEvent(new BookOrderEvent(customer, st, currentTick));
				}
			}


		});
		subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
			terminate();
		});
		c.countDown();
	}
}

