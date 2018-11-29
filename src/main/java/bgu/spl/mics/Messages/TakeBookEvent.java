package bgu.spl.mics.Messages;

import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements FiftyPercentDiscount.Event<OrderResult> {
    private String bookName;
    public TakeBookEvent(String bookName){
        this.bookName = bookName;
    }

    public String getBookName(){
        return this.bookName;
    }
}
