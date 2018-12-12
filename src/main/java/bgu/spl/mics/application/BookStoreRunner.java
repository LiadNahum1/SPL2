package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

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
            int ns = input.getServices().getSelling();
            int ni = input.getServices().getInventoryService();
            int nl=input.getServices().getLogistics();
            int nr=input.getServices().getResourcesService();
            CustomerData [] customerData = input.getServices().getCustomers();
            Customer [] customers = new Customer[customerData.length];
            int N = ns+ni+nl+nr+customerData.length;
            CountDownLatch doneSignal = new CountDownLatch(N);
            //initialize servises
            TimeService timeService = new TimeService(input.getServices().getTime().getSpeed(),input.getServices().getTime().getDuration());
            SellingService[] sellingServices = new SellingService[ns];
            for (int i = 0; i<sellingServices.length; i++){
                sellingServices[i] = new SellingService(i+1,doneSignal);
            }
            InventoryService[] inventoryServices = new InventoryService[ni];
            for (int i = 0; i<inventoryServices.length; i++){
                inventoryServices[i] = new InventoryService(i+1,doneSignal);
            }
            LogisticsService[] logisticsServices = new LogisticsService[nl];
            for (int i = 0; i<logisticsServices.length; i++){
                logisticsServices[i] = new LogisticsService(i+1,doneSignal);
            }
            ResourceService[] resourceServices = new ResourceService[nr];
            for (int i = 0; i<resourceServices.length; i++){
                resourceServices[i] = new ResourceService(i+1,doneSignal);
            }
            //create customers and API services
            APIService [] apiServices = new APIService[customers.length];
            for (int i=0; i< customers.length; i=i+1){
                CustomerData cus = customerData[i];
                customers[i] = new Customer(cus.getId(), cus.getName(), cus.getAddress(), cus.getDistance(), cus.getCreditCard().getNumber(), cus.getCreditCard().getAmount());
                apiServices[i] = new APIService(customers[i], cus.getOrderSchedule(),doneSignal);
            }

            //TODO: make sure all sevises has been initialized before the first broadcast started
            //start running all threads
            Vector<Thread> v = new Vector<>();
            for (int i = 0; i<sellingServices.length; i++){
                v.add(new Thread(sellingServices[i]));
            }
            for (int i = 0; i<inventoryServices.length; i++){
                v.add(new Thread(inventoryServices[i]));
            }
            for (int i = 0; i<logisticsServices.length; i++){
                v.add(new Thread(logisticsServices[i]));
            }
            for (int i = 0; i<resourceServices.length; i++){
                v.add(new Thread(resourceServices[i]));
            }
            for (int i=0; i< apiServices.length; i=i+1){
                v.add(new Thread(apiServices[i]));
            }
           for(Thread t : v){
                t.start();
           }
            doneSignal.await();
            v.add(new Thread(timeService));
            for(Thread t : v){
                t.join();
            }
            //output files
            HashMap<Integer, Customer> customerHashMap = new HashMap<>();
            for (Customer c: customers){
                customerHashMap.put(c.getId(), c);
            }
            printAllCustomers(customerHashMap, args[1]); //HashMap<Integer,Customer>
            inv.printInventoryToFile(args[2]); //HashMap<String,Integer>
            MoneyRegister moneyReg = MoneyRegister.getInstance();
            moneyReg.printOrderReceipts(args[3]); //List<OrderReceipt>
            moneyReg.printOrderReceipts(args[4]); //MoneyRegister
        }
        catch(Exception e){}
    }

    public static void printAllCustomers(HashMap<Integer,Customer> customers, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(customers);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}


