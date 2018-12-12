package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Vector;
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
	private Vector<DeliveryVehicle> vehicles;
	private Semaphore sem;
	//thread safe singelton
	private static class SingletonHolderVehicle
	{
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private ResourcesHolder() {
		this.vehicles = new Vector<>();
}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {

		return ResourcesHolder.SingletonHolderVehicle.instance;
	}

	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		try{
		sem.acquire();
		}
		catch (Exception e){}
	 Future <DeliveryVehicle> fu  = new Future<>();
	 fu.resolve(this.vehicles.remove(0));
	 return fu;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		this.vehicles.add(vehicle);
		sem.release();
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		synchronized (this.vehicles) {
			for (int i = 0; i < vehicles.length; i = i + 1) {
				this.vehicles.add(vehicles[i]);
			}
			sem = new Semaphore(this.vehicles.size());
		}
	}

}
