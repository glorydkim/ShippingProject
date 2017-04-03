
public class PortTime {

	int time = 0; // measured in seconds

	/**
	 * Constructor for the class
	 * 
	 * @param t
	 *            Time in seconds
	 */
	public PortTime(int t) {
		time = t;
	}

	/**
	 * Method to implement toString() method for the PortTime object
	 */
	public String toString() {
		return String.format("%d:%d:%d", (time / 60 / 60) % 24, (time / 60) % 60, time % 60);
	} // end method toString
}
