package helpers;

import java.util.Random;

/**
 * Custom class to keep a singleton random number generator.
 * 
 * @author <b>Emrah Cem</b>
 * @see {@link Random}
 */
public class MyRandom {

	/**
	 * Unique instance of this class
	 */
	private static MyRandom random;

	/**
	 * Random number generator
	 */
	private Random generator;

	/**
	 * Default constructor 
	 */
	protected MyRandom() {
		generator = new Random();
		generator.setSeed(1);
	}

	/**
	 * @param limit the smallest integer that is one more than the maximum number that can be generated  
	 * @return an integer between [<code>0</code> and <code>limit</code>)
	 */
	public int nextInt(int limit) {
		return generator.nextInt(limit);

	}

	/**
	 * @return a Singleton instance of this class
	 */
	public static MyRandom getInstance() {
		if (random == null) {
			random = new MyRandom();
		}

		return random;
	}

	public void setSeed(long seed) {
		generator.setSeed(seed);
	}
}
