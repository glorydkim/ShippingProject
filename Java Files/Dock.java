import java.util.Scanner;

public class Dock extends Thing {
	// Ship at the dock
	Ship ship;

	/**
	 * Dock class constructor
	 * 
	 * @param sc
	 */
	public Dock(Scanner sc) {
		// Call parent class constructor to assign index and name
		super(sc);
		// Create ship with the rest of the values for Dock
		ship = new Ship(sc);
	}

	/**
	 * Method to implement toString() method for Dock class object
	 */
	public String toString() {
		// Print Dock name and index, then ship details(at dock)
		return "\n Dock: " + super.toString() + "\n" + "   Ship : " + ship;
	} // end method toFileString

}
