package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<OrderReceipt> future;
    @Before
    public void setUp() throws Exception {
        future = new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    //@PRE: none
    //@POST: get()!=null
    public void get() {
        future.resolve(new OrderReceipt(1, "seller", 1, "h", 30, 4,1, 2));
        assertNotNull(future.get());

    }

    @Test
    //@PRE: @param!=null
    //@POST: get() != null
    public void resolve() {
        future.resolve(new OrderReceipt(1, "seller", 1, "h", 30, 4,1, 2));
        assertNotNull(future.get(10, TimeUnit.SECONDS));
    }

    @Test
    //@PRE: none
    //@POST: none
    public void isDone() {
     assertFalse(future.isDone());
     future.resolve(new OrderReceipt(1, "seller", 1, "h", 30, 4,1, 2));
     assertTrue(future.isDone());
    }

    @Test
    //@PRE: none
    //@POST: none
    public void get1() {
        future.resolve(new OrderReceipt(1, "seller", 1, "h", 30, 4,1, 2));
        assertNotNull(future.get(10, TimeUnit.SECONDS));

    }
}