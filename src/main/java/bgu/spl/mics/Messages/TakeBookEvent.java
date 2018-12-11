package bgu.spl.mics.Messages;


import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements Event<OrderResult> {
    private String bookTitle;
    public TakeBookEvent(String bookTitle){
        this.bookTitle = bookTitle;
    }

    public String getBookTitle(){
        return this.bookTitle;
    }
}
