import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Thing implements Comparable<Thing> {

	// Name of Thing object
	String name = null;
	// Index of Thing object
	int index = 0;
	// Parent of Thing object
	int parent = 0;

	/**
	 * Default constructor for Thing object
	 */
	public Thing() {
		// Do Nothing
	}

	/**
	 * Constructor for Thing class
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public Thing(Scanner sc) {
		// Assign name to Thing
		if (sc.hasNext())
			name = sc.next();
		// Assign index to Thing
		if (sc.hasNextInt())
			index = sc.nextInt();
		// Assign parent to Thing
		if (sc.hasNextInt())
			parent = sc.nextInt();
	}

	/**
	 * Implement method for SORTing of Things by name (default attribute)
	 */
	public int compareTo(Thing thing) {
		return name.compareTo(thing.name);
	} // end method compareTo > Comparable

	/**
	 * Method to implement toString() method for Thing class
	 */
	public String toString() {
		return this.name + "  " + this.index;
	} // default toFileString method in MyThing

}
