package bgu.spl.mics.application.services;

import bgu.spl.mics.Messages.TerminateBroadcast;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private static TimeService instance = null;
	private Timer timer;
	private int speed;
	private int duration;
	private int currentTick;

	public TimeService(int speed, int duration) {
		super("Time Service");
		this.speed = speed;
		this.duration = duration;
		this.currentTick = 0;
		//initialize timer
		this.timer = new Timer();
	}

	public static TimeService getInstance(int speed, int duration) {
		if (instance == null)
			instance = new TimeService(speed, duration);
		return instance;
	}

	@Override
	protected void initialize() {
		terminate();
		this.timer.schedule(new TimerTask() {
			@Override
			public void run() {
				currentTick = currentTick + 1;
				sendBroadcast(new TickBroadcast(currentTick));
				if (currentTick == duration) {
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
				}

			}
		}, 0, speed);
	}
}
