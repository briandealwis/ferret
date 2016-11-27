package ca.ubc.cs.ferret.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import ca.ubc.cs.ferret.EclipseFuture;

public class FutureTests {

    @Test
	public void testBasicFuture() {
		EclipseFuture<Object> future = new EclipseFuture<Object>("testBasicFuture");
		assertFalse(future.isDone());
	}

    @Test
	public void testSet() {
		EclipseFuture<Object> future = new EclipseFuture<Object>("testSet");
		assertFalse(future.isDone());
		future.set("test");
		assertTrue(future.isDone());
		assertEquals("test", future.get());
	}
	
    @Test
	public void testDelayTimeout() {
		final EclipseFuture<Object> future = new EclipseFuture<Object>("testDelay");
		assertFalse(future.isDone());
		// this must be run in another thread
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					future.get(20, TimeUnit.MILLISECONDS);
					fail();
				} catch(TimeoutException e) {
					/* do nothing */
				}
				assertFalse(future.isDone());				
			}});
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("thread join failed");
		}
		assertFalse(future.isDone());
	}

    @Test
	public void testDelayAndSet() {
		final EclipseFuture<Object> future = new EclipseFuture<Object>("testDelay");
		assertFalse(future.isDone());
		// this must be run in another thread
		Thread t = new Thread(new Runnable() {
			public void run() {
				assertEquals("test",future.get());
			}});
		future.set("test");
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("thread join failed");
		}
		assertTrue(future.isDone());
		assertEquals("test",future.get());
	}


}
