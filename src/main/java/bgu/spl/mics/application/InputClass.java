package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

public class InputClass {
    private BookInventoryInfoData [] initialInventory;
    private VehicleHolder[] initialResources;
    private Services services;

    public BookInventoryInfoData[] getInitialInventory() {
        return initialInventory;
    }

    public VehicleHolder[] getInitialResource() {
        return initialResources;
    }

    public Services getServices() {
        return services;
    }
}

