package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        try(Reader reader = new InputStreamReader(new FileInputStream("input.json"), "UTF-8")){
            Gson gson = new GsonBuilder().create();
            InputClass input = gson.fromJson(reader, InputClass.class);
            //create inventory
            Inventory inv = Inventory.getInstance();
            inv.load(input.getInitialInventory());
            //create ResourceHolder
            ResourcesHolder reHolder = ResourcesHolder.getInstance();
            reHolder.load(input.getInitialResource()[0].getVehicles());
            //initialize servises
            TimeService timeService = new TimeService(input.getServices().getTime().getSpeed(),input.getServices().getTime().getDuration());
            SellingService[] sellingServices = new SellingService[input.getServices().getSelling()];
            for (int i = 0; i<sellingServices.length; i++){
                sellingServices[i] = new SellingService(i+1);
            }
            InventoryService[] inventoryServices = new InventoryService[input.getServices().getInventoryService()];
            for (int i = 0; i<inventoryServices.length; i++){
                inventoryServices[i] = new InventoryService(i+1);
            }
            LogisticsService[] logisticsServices = new LogisticsService[input.getServices().getLogistics()];
            for (int i = 0; i<logisticsServices.length; i++){
                logisticsServices[i] = new LogisticsService(i+1);
            }
            ResourceService[] resourceServices = new ResourceService[input.getServices().getResourcesService()];
            for (int i = 0; i<resourceServices.length; i++){
                resourceServices[i] = new ResourceService(i+1);
            }
            //create customers and API services
            CustomerData [] customerData = input.getServices().getCustomers();
            Customer [] customers = new Customer[customerData.length];
            APIService [] apiServices = new APIService[customers.length];
            for (int i=0; i< customers.length; i=i+1){
                CustomerData cus = customerData[i];
                customers[i] = new Customer(cus.getId(), cus.getName(), cus.getAddress(), cus.getDistance(), cus.getCreditCard().getNumber(), cus.getCreditCard().getAmount());
                apiServices[i] = new APIService(customers[i], cus.getOrderSchedule());
            }

        }
        catch(Exception e){}
    }
}


