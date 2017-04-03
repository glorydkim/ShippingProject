import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Ship extends Thing {
	// Weight of ship
	double weight = 0;
	// Length of ship
	double length = 0;
	// Width of ship
	double width = 0;
	// Draft of ship
	double draft = 0;

	// Arrival time of Ship at port
	PortTime arrivalTime = null;
	// Dock time of the ship
	PortTime dockTime = null;
	// List of Jobs assigned to the Ship
	ArrayList<Job> jobs = new ArrayList<>();

	/**
	 * Constructor for the Ship class
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public Ship(Scanner sc) {
		// Assign name and index using parent class constructor
		super(sc);
		// Assign weight of ship
		if (sc.hasNextDouble())
			weight = sc.nextDouble();
		// Assign length of ship
		if (sc.hasNextDouble())
			length = sc.nextDouble();
		// Assign width of ship
		if (sc.hasNextDouble())
			width = sc.nextDouble();
		// Assign draft of ship
		if (sc.hasNextDouble())
			draft = sc.nextDouble();
		//Set the arrival time for the Ship at Port
		Calendar calendar = Calendar.getInstance();
	    int second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR_OF_DAY) * 60*60;
		arrivalTime = new PortTime(second);
		
	} // end

	/**
	 * Implement CompareTo method for Sorting of ships
	 * 
	 * @param compareShip
	 *            Ship object to compare
	 * @param attribute
	 *            A Attribute by which ships to be sorted
	 * @return Return value indicating Sort type
	 */
	public int compareTo(Ship compareShip, int attribute) {
		// Attribute - 0 - weight, 1- length, 2-width, 3- draft, 4 -Name, 5-All
		switch (attribute) {
		// Sort by Weight
		case 0:
			if (this.weight < compareShip.weight)
				return -1;
			if (this.weight > compareShip.weight)
				return 1;
			return 0;
		// Sort By length
		case 1:
			if (this.length < compareShip.length)
				return -1;
			if (this.length > compareShip.length)
				return 1;
			return 0;
		// Sort by width
		case 2:
			if (this.width < compareShip.width)
				return -1;
			if (this.width > compareShip.width)
				return 1;
			return 0;
		// Sort by draft
		case 3:
			if (this.draft < compareShip.draft)
				return -1;
			if (this.draft > compareShip.draft)
				return 1;
			return 0;
		// Sort by name (default)
		case 4:
			return super.compareTo(compareShip);
		// Sort by all(1 by 1)
		case 5:
			// Sort by weight, if equal , proceed and sort by length
			if (this.compareTo(compareShip, 0) != 0) {
				return this.compareTo(compareShip, 0);
			}
			// Sort by length, if equal, proceed and sort by width
			else {
				if (this.compareTo(compareShip, 1) != 0) {
					return this.compareTo(compareShip, 1);
				}
				// Sort by width, if equal, proceed and sort by draft
				else {
					if (this.compareTo(compareShip, 2) != 0) {
						return this.compareTo(compareShip, 2);
					}
					// Sort by draft, if equal, proceed and sort by name
					else {
						if (this.compareTo(compareShip, 3) != 0) {
							return this.compareTo(compareShip, 3);
						}
						// Sort by name, if equal then return 0 they are equal
						else {
							if (this.compareTo(compareShip, 4) != 0) {
								return this.compareTo(compareShip, 4);
							} else {
								// Ships are equal
								return 0;
							}
						}

					}
				}
			}
		default:
			return 0;
		}
	}

	/**
	 * Method to implement toString() method for class
	 */
	public String toString() {
		// Get ship index and name
		String st = super.toString();
		// Print jobs for the Ship
		if (jobs.size() == 0)
			return st;
		for (Job mj : jobs)
			st += "\n       - " + mj;
		return st;
	} // end method toFileString

}
