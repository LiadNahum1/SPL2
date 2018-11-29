package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private BookInventoryInfo book;
    public static int orderId = 0;
    public BookOrderEvent(Customer c, BookInventoryInfo book){
        this.customer = c;
        this.book= book;
        orderId = orderId + 1;

    }

    public Customer getCustomer(){
        return this.customer;
    }

    public BookInventoryInfo getBook(){
        return this.book;
    }

    public int getOrderId(){
        return orderId;
    }
}
