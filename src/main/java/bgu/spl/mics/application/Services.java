package bgu.spl.mics.application;

import bgu.spl.mics.application.services.TimeService;

public class Services {
    private TimeData time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private CustomerData[] customers;

    public TimeData getTime() {
        return time;
    }

    public int getSelling() {
        return selling;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public CustomerData[] getCustomers() {
        return customers;
    }
}
