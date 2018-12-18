package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private ConcurrentLinkedQueue<DeliveryVehicle> vehicles;
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> unResolved;
	private Semaphore sem;
	//thread safe singelton
	private static class SingletonHolderVehicle
	{
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private ResourcesHolder() {
		this.vehicles = new ConcurrentLinkedQueue<>();
		this.unResolved = new ConcurrentLinkedQueue<>();
}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {

		return SingletonHolderVehicle.instance;
	}

	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future <DeliveryVehicle> fu  = new Future<>();
		synchronized (unResolved) {
			if (sem.tryAcquire()) {

				fu.resolve(this.vehicles.remove());
			} else {
				unResolved.add(fu);
			}
			return fu;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
        System.out.println("release");
	    synchronized (this.unResolved) {
            if (!unResolved.isEmpty()) {
                System.out.println("other gained" + this.unResolved.size());
                unResolved.remove().resolve(vehicle);
            } else {
                System.out.println("no one asked" + this.unResolved.size());
                this.vehicles.add(vehicle);
                sem.release();
            }
        }

	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
			for (int i = 0; i < vehicles.length; i = i + 1) {
				this.vehicles.add(vehicles[i]);
			}
			sem = new Semaphore(vehicles.length);
		}

}
