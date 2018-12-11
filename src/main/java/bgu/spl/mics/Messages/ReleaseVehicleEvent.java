package bgu.spl.mics.Messages;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event<Boolean> {
    private DeliveryVehicle vehicle;
    public ReleaseVehicleEvent(DeliveryVehicle vehicle){
        this.vehicle = vehicle;
    }

    public DeliveryVehicle getVehicle(){
        return this.vehicle;
    }
}
