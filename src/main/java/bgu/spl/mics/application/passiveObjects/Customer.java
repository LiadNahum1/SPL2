package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> receipts;
	private int creditCard;
	private int availableAmountInCreditCard;
	private Integer moneyLock;
	public Customer(int id, String name, String address, int distance, int creditCard, int availableAmountInCreditCard)
	{
		this.id = id;
		this.name= name;
		this.address = address;
		this.distance = distance;
		this.receipts = new LinkedList<>();
		this.creditCard = creditCard;
		this.availableAmountInCreditCard = availableAmountInCreditCard;
		this.moneyLock = new Integer(0);
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return this.address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return this.distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return this.receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		synchronized (moneyLock) {
			return this.availableAmountInCreditCard;
		}
	}

	public void addRecipt(OrderReceipt or){receipts.add(or);}
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.creditCard;
	}

	//assumes custimer has enough money
	public void chargeCreditCard(int amount){
		synchronized (moneyLock) {
			this.availableAmountInCreditCard = this.availableAmountInCreditCard - amount;
		}

	}

	//an object to lock the availableAmountInCreditCard
	public Object getMoneyLock(){
		return this.moneyLock;
	}

	//TODO erase
	public String toString(){
		return "\nId "+ getId() + "\nname " + getName() + "\naddr " + getAddress() + "\ndist " + getDistance() + "\ncard " + getCreditNumber() + "\nmoney "+getAvailableCreditAmount()+"\n";
	}
}
