package helpers;

import java.util.concurrent.TimeUnit;

/**
 * This class is used to keep a custom time unit. Normally {@linkplain java.util.concurrent.TimeUnit} provides a time unit 
 * which might be {@link TimeUnit#DAYS} , {@link TimeUnit#HOURS}, {@link TimeUnit#MINUTES}, {@link TimeUnit#SECONDS}, {@link TimeUnit#MILLISECONDS}, 
 * {@link TimeUnit#MICROSECONDS}, and {@link TimeUnit#NANOSECONDS}. This class makes it more generic. User can set the time unit to
 * <code>10 ms</code>  by setting the @see {@link #amount} field to <code>10</code> and @see {@link #timeUnit} field to {@link TimeUnit#MILLISECONDS}.     
 * @author <b>Emrah Cem</b>
 * @see TimeUnit
 */
public class CustomTimeUnit {

	/**
	 * Single instance of this class
	 */
	protected static CustomTimeUnit instance;
	
	/**
	 *  A {@link TimeUnit} 
	 */
	private TimeUnit timeUnit;
	
	/**
	 * multiplier of the timeUnit  
	 */
	private long amount;
	
	
	/**
	 * Constructor is private to achieve Singleton 
	 */
	private CustomTimeUnit(){}
	
	/**
	 * Gives an instance of this class (if an instance has been created before, then it is returned; otherwise new instance is returned.) 
	 * @return a Singleton instance of this class
	 */
	public static CustomTimeUnit getInstance(){
		if (instance == null){
			instance = new CustomTimeUnit();

		}
		return instance;
	}
	
	public void setUnit(long amount, TimeUnit timeUnit){
		this.amount = amount;
		this.timeUnit = timeUnit;
	}
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public long getAmount() {
		return amount;
	}
	
	public long getTimeInMilliseconds(double time) {
		return (long) (TimeUnit.MILLISECONDS.convert(getAmount(), getTimeUnit()) * time);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return amount + " ("+ timeUnit.toString()+ ")";
	}
	
}
