package bgu.spl.mics.Messages;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import java.util.Vector;

public class FiftyPercentDiscount implements Broadcast{
    Vector<BookInventoryInfo> booksOnDiscount;
    public FiftyPercentDiscount(){
        this.booksOnDiscount = new Vector<>();

    }
    public synchronized void discount(Vector<BookInventoryInfo> books){
        this.booksOnDiscount = books;
    }

    public synchronized Vector<BookInventoryInfo> getBooksOnDiscount(){
        return this.booksOnDiscount;
    }

    /**
     * A "Marker" interface extending {@link Message}. A micro-service that sends an
     * Event message expects to receive a result of type {@code <T>} when a
     * micro-service that received the request has completed handling it.
     * When sending an event, it will be received only by a single subscriber in a
     * Round-Robin fashion.
     */
    public static interface Event<T> extends Message {

    }
}
