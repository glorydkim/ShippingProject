import java.util.Scanner;

public class PassengerShip extends Ship {

	// Number of Passengers for the Ship
	int numberOfPassengers = 0;
	// Number of rooms in the ship
	int numberOfRooms = 0;
	// Number of occupied rooms in the ship
	int numberOfOccupiedRooms = 0;

	/**
	 * Constructor for PassengerShip
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public PassengerShip(Scanner sc) {
		// Assign name and index using, and other default attributes
		//using parent class constructor
		super(sc);
		// Assign Number of Passengers value
		if (sc.hasNextInt())
			numberOfPassengers = sc.nextInt();
		// Assign Number of Rooms value
		if (sc.hasNextInt())
			numberOfRooms = sc.nextInt();
		// Assign Number of occupied rooms value
		if (sc.hasNextInt())
			numberOfOccupiedRooms = sc.nextInt();
	} // end random ship constructor

	/**
	 * Method to implement toString() method for the class
	 */
	public String toString() {
		// Return passenger ship index and name
		return "Passenger Ship:   " + super.toString();

	} // end method toFileString

}
