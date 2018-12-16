package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
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
        Gson gson = null;
        InputClass input = null;
        try(Reader reader = new InputStreamReader(new FileInputStream(args[0]), "UTF-8")) {
            gson = new GsonBuilder().create();
            input = gson.fromJson(reader, InputClass.class);
        }
        catch(Exception e){
            System.out.println("couldnt read" + args[0]);
        }

            //create inventory
            Inventory inv = Inventory.getInstance();
            BookInventoryInfoData [] booksDada = input.getInitialInventory();
            BookInventoryInfo [] books = new BookInventoryInfo[booksDada.length];
            for (int i=0; i< books.length; i = i+1){
                books[i] = new BookInventoryInfo(booksDada[i].getBookTitle(), booksDada[i].getAmount(), booksDada[i].getPrice());
            }
            inv.load(books);

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
            try {
                doneSignal.await();
                Thread time = new Thread(timeService);
                time.start();
                v.add(time);

                for (Thread t : v) {
                    t.join();
                }
            }
            catch (Exception e){System.out.println("interupt");}
            //output files
            HashMap<Integer, Customer> customerHashMap = new HashMap<>();
            for (Customer c: customers){
                customerHashMap.put(c.getId(), c);
            }

            printAllCustomers(customerHashMap, args[1]); //HashMap<Integer,Customer>
             //TODO erase
            System.out.println("customers");
            for(Integer i:customerHashMap.keySet()){
                System.out.print(i + " " + customerHashMap.get(i).toString());
            }
            System.out.println("books");             //TODO erase
            inv.printInventoryToFile(args[2]); //HashMap<String,Integer>

            MoneyRegister moneyReg = MoneyRegister.getInstance();
            System.out.println("receipts");             //TODO erase
            moneyReg.printOrderReceipts(args[3]); //List<OrderReceipt>

          System.out.println("MoneyRegister: " + moneyReg.getTotalEarnings());             //TODO erase
            printMoneyRegister(args[4], moneyReg); //MoneyRegister

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
    public static void printMoneyRegister(String filename, MoneyRegister moneyReg) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(moneyReg);
            fileOut.close();
            System.out.printf("Serialized data is saved in " + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}