package bgu.spl.mics.Messages;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliveryEvent implements Event<DeliveryVehicle> {
    private String deliveryAddress;
    private int distance;
    public DeliveryEvent(String adrees,int distance){
        this.deliveryAddress = adrees;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }


}
