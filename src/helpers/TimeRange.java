package helpers;

/**
 * Represents a time range
 * @author <b>Emrah Cem</b>
 */
public class TimeRange {

	/**
	 * The lower bound of the time range
	 */
	private int lower_bound;
	
	/**
	 * The upper bound of the time range
	 */
	private int upper_bound;
	
	/**
	 * @param lower_bound the lower bound of the time range
	 * @param upper_bound the upper bound of the time range
	 */
	public TimeRange(int lower_bound, int upper_bound) {
		this.lower_bound = lower_bound;
		this.upper_bound = upper_bound;
	}
	
	
	public int getLower_bound() {
		return lower_bound;
	}
	public void setLower_bound(int lower_bound) {
		this.lower_bound = lower_bound;
	}
	public int getUpper_bound() {
		return upper_bound;
	}
	public void setUpper_bound(int upper_bound) {
		this.upper_bound = upper_bound;
	}
	
	
	
}
