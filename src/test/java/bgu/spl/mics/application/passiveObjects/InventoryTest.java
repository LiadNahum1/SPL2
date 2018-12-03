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
        //@PRE: checkAvailabiltyAndGetPrice(book) == -1
        //@POST: checkAvailabiltyAndGetPrice(book) != -1
        public void load() {
            BookInventoryInfo[] books = new BookInventoryInfo[1];
            books[0] = new BookInventoryInfo("book1",2,10);
            inv.load(books);
            assertEquals(10,inv.checkAvailabiltyAndGetPrice("book1"));
        }

        @Test
        //@PRE: none
        //@POST: checkAvailabiltyAndGetPrice("book2") == -1
        public void take() {
            BookInventoryInfo[] books = new BookInventoryInfo[1];
            books[0] = new BookInventoryInfo("book2",1,10);
            inv.load(books);
            OrderResult orderRe = inv.take("book2");
            assertEquals(OrderResult.SUCCESSFULLY_TAKEN ,orderRe);
            orderRe = inv.take("book3");
            assertEquals(OrderResult.NOT_IN_STOCK ,orderRe);
            orderRe = inv.take("book2");
            assertEquals(OrderResult.NOT_IN_STOCK ,orderRe);
            assertEquals(-1,inv.checkAvailabiltyAndGetPrice("book2"));
        }
        //@PRE: none
        //@POST:checkAvailabiltyAndGetPrice("book4") != -1 &  checkAvailabiltyAndGetPrice("notExist") == -1
        @Test
        public void checkAvailabiltyAndGetPrice() {
            BookInventoryInfo[] books = new BookInventoryInfo[1];
            books[0] = new BookInventoryInfo("book4",1,14);
            inv.load(books);
            assertEquals(14,inv.checkAvailabiltyAndGetPrice("book4"));
            assertEquals(-1,inv.checkAvailabiltyAndGetPrice("notExist"));
        }

        @Test
        public void printInventoryToFile() {
            BookInventoryInfo[] books = new BookInventoryInfo[1];
            books[0] = new BookInventoryInfo("book5",2,10);
            inv.load(books);
            inv.printInventoryToFile("Inventory");
            try {
                FileInputStream fileInputStream = new FileInputStream("Inventory.txt");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                Object object = objectInputStream.readObject();
                HashMap<String,Integer> booksInInv =  (HashMap<String,Integer>)object;
                assertEquals(new Integer(2), booksInInv.get("book5"));
                objectInputStream.close();
            }
            catch (Exception e) {
            }
        }
    }

