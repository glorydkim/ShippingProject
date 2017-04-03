import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SeaPort extends Thing {

	// List of Docks in the SeaPort
	ArrayList<Dock> docks;
	// List of Ships in the Que for the Port
	ArrayList<Ship> que;
	// List of All ships in the port
	ArrayList<Ship> ships;
	// List of Persons working at the Port
	ArrayList<Person> persons;

	/**
	 * Constructor for the class to initialize the SeaPort
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public SeaPort(Scanner sc) {
		// Assign name and index
		super(sc);
		// Initialize the dock list object
		docks = new ArrayList<Dock>();
		// Initialize the que list object
		que = new ArrayList<Ship>();
		// Intitialize the ships list object
		ships = new ArrayList<Ship>();
		// Initialize the persons list object
		persons = new ArrayList<Person>();
	}

	/**
	 * Method to implement toString() method for class. Prints all details for
	 * the Port
	 */
	public String toString() {
		String st = "\n\nSeaPort: " + super.toString();
		for (Dock md : docks)
			st += "\n" + md;
		st += "\n\n --- List of all ships in que: ";
		for (Ship ms : que)
			st += "\n   > " + ms;
		st += "\n\n --- List of all ships: ";
		for (Ship ms : ships)
			st += "\n   > " + ms;
		st += "\n\n --- List of all persons: ";
		for (Person mp : persons)
			st += "\n   > " + mp;
		return st;

	}

	/**
	 * Implement ComparaTo method for SORTING of ports
	 * 
	 * @param port
	 * @return
	 */
	public int compareTo(SeaPort port) {
		// Compare by name
		return name.compareTo(port.name);
	} // end method compareTo > Comparable
}
