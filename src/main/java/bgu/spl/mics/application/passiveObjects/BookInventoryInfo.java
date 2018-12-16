package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private String bookTitle;
	private int amountInInventory;
	private int price;
	public BookInventoryInfo (String bookTitle , int amount , int price){
		this.bookTitle = bookTitle;
		this.amountInInventory = amount;
		this.price= price;
	}
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return this.bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return this.amountInInventory;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return this.price;
	}

	/**
	 * Reduce the amount of books of this type in the inventory.
	 **/
	public void reduceAmountInInventory() {
		synchronized (this) {
			this.amountInInventory = this.amountInInventory - 1;
		}
	}

	//TODO erase
	public String toString(){
		return "\nTitle" + getBookTitle() + "\namount" + getAmountInInventory() + "\nprice" +getPrice()+"\n";
	}

	
}
