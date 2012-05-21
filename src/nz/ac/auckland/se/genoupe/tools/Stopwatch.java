package nz.ac.auckland.se.genoupe.tools;

/**
 * This is a simple stopwatch for measuring performance. It can be stopped and
 * resumed, it measures the accumulated time running. Each object is a state
 * machine with two states, running and not running. The methods start and stop
 * switch between the states.
 * 
 * 
 * @author gweb017
 * 
 */
public class Stopwatch {

	private long nanoAccumulatedTime = 0;
	private long nanoLastStartCall;
	private boolean isRunning = false;

	/**
	 * Switches from the not running state into the running state. Time
	 * measurement is hence started or resumed.
	 * 
	 */
	public void start() {
		if (isRunning)
			throw new RuntimeException("start() called while running");
		nanoLastStartCall = System.nanoTime();
		isRunning = true;
	}

	/**
	 * Switches from the running state into the not running state. The time
	 * since the last call to start is added to the accumulated time. Time
	 * measurement is hence suspended until the next call of start().
	 * 
	 */
	public void stop() {
		if (!isRunning)
			throw new RuntimeException("stop() called while not running");
		nanoAccumulatedTime += System.nanoTime() - nanoLastStartCall;
		isRunning = false;
	}

	/**
	 * Switches from any state into the not running state. The stopwatch resets,
	 * meaning the accumulatedTime is set to zero.
	 * 
	 */
	public void reset() {
		isRunning = false;
		nanoAccumulatedTime = 0;
	}

	/**
	 * Returns the accumulated time measured by the stopwatch. If called while
	 * not running, it returns the time accunulated until the last stop(). If
	 * called while running, it returns the time accumulated until now.
	 * 
	 * @return the accumulated measured time in nanoseconds
	 */
	public long nanoSeconds() {
		if (!isRunning)
			return nanoAccumulatedTime;
		else
			return nanoAccumulatedTime + System.nanoTime() - nanoLastStartCall;
	}

	public void printTime() {
		System.out.println(nanoSeconds() + " ns");
	}

}
