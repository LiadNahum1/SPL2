package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event {
    private Customer customer;
    private String bookName;
    public BookOrderEvent(Customer c, String name){
        this.customer = c;
        this.bookName = name;
    }

    public Customer getCustomer(){
        return this.customer;
    }

    public String getBookName(){
        return this.bookName;
    }
}
