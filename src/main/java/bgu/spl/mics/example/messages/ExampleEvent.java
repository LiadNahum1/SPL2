package bgu.spl.mics.example.messages;

import bgu.spl.mics.Messages.FiftyPercentDiscount;

public class ExampleEvent implements FiftyPercentDiscount.Event<String> {

    private String senderName;

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}