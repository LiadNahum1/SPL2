package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister {
	private Vector<OrderReceipt> orderReceipt; //treadSafe
	private int totalSum;
	private MoneyRegister(){
		this.orderReceipt = new Vector<>();
		this.totalSum = 0;
	}
	//thread safe singelton
	private static class SingletonHolderMoney {
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
			return SingletonHolderMoney.instance;

	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		synchronized (this) {
			orderReceipt.add(r);
			this.totalSum = this.totalSum + r.getPrice();//TODO:whay syncall
		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalSum;

	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	//assumes that c has enough money
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCreditCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(orderReceipt);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in " + filename);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}
