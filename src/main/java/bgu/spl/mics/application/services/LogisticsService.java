package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.AcquireVehicleEvent;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Messages.ReleaseVehicleEvent;
import bgu.spl.mics.Messages.TerminateBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
private CountDownLatch countDownLatch;
	public LogisticsService(int num , CountDownLatch countDownLatch) {
		super("LogisticsService" + num);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
			subscribeEvent(DeliveryEvent.class, event -> {
				Future<DeliveryVehicle> vehicleFuture = sendEvent(new AcquireVehicleEvent());

				DeliveryVehicle vehicle = vehicleFuture.get();
				if(vehicle!= null) {
					vehicle.deliver(event.getDeliveryAddress(), event.getDistance());
					sendEvent(new ReleaseVehicleEvent(vehicle));
				}
			});
		subscribeBroadcast(TerminateBroadcast.class , broadcast-> {
			terminate();});
		countDownLatch.countDown();
	}

		
	}


