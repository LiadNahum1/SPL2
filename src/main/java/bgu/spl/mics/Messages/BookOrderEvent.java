package bgu.spl.mics.Messages;


import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt>{
    private Customer customer;
    private String bookTitle;
    private int tick; //time that the BookOrderEvent is sent
    public BookOrderEvent(Customer c, String bookTitle, int tick){
        this.customer = c;
        this.bookTitle= bookTitle;
        this.tick = tick;

    }

    public Customer getCustomer(){
        return this.customer;
    }

    public String getBookTitle(){
        return this.bookTitle;
    }

    public int getTick(){
        return this.tick;
    }
}
