package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inv;
    @Before
    public void setUp() throws Exception {
        inv = Inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    //@POST: getInstance()!=null & inv == getInstance()(there is only one object)
    public void getInstance() {
        assertNotEquals(null, Inventory.getInstance());
        assertSame(inv, Inventory.getInstance());
    }

    @Test
    //@PRE: checkAvailabiltyAndGetPrice(book) = -1
    //@POST: checkAvailabiltyAndGetPrice(book) = 10
    public void load() {
         BookInventoryInfo[] books = new BookInventoryInfo[1];
         books[0] = new BookInventoryInfo("h",2,10);
         inv.load(books);
        assertEquals(10,inv.checkAvailabiltyAndGetPrice("h"));
        assertEquals(2,books[0].getAmountInInventory());
    }

    @Test
    //@PRE: none
    //@POST: checkAvailabiltyAndGetPrice(book) = -1 &
    public void take() {
        BookInventoryInfo[] books = new BookInventoryInfo[1];
        books[0] = new BookInventoryInfo("h",1,10);
        inv.load(books);
        OrderResult orderRe = inv.take("h");
        assertEquals(0,books[0].getAmountInInventory());
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN ,orderRe);
        orderRe = inv.take("d");
        assertEquals(OrderResult.NOT_IN_STOCK ,orderRe);
        orderRe = inv.take("h");
        assertEquals(OrderResult.NOT_IN_STOCK ,orderRe);
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("h"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {

    }

    @Test
    public void printInventoryToFile() {
        BookInventoryInfo[] books = new BookInventoryInfo[1];
        books[0] = new BookInventoryInfo("book2",2,10);
        inv.load(books);
        inv.printInventoryToFile("Inventory");
        try {
            FileInputStream fileInputStream = new FileInputStream("Inventory.txt");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
            Object object = objectInputStream.readObject();
            HashMap<String,Integer> booksInInv =  (HashMap<String,Integer>)object;
            assertEquals(new Integer(2), booksInInv.get("book2"));
            objectInputStream.close();
        } catch (Exception e) {

        }
    }
}