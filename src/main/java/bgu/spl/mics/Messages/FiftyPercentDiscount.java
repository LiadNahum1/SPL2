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

}
