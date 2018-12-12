package bgu.spl.mics.application;

public class CustomerData {
    private int id;
    private String name;
    private String address;
    private int distance;
    private CreditCardData creditCard;
    private OrderSchedule [] orderSchedule;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }

    public CreditCardData getCreditCard() {
        return creditCard;
    }

    public OrderSchedule[] getOrderSchedule() {
        return orderSchedule;
    }
}
