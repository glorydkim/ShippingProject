import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Person extends Thing {

	// Skill for the Person
	String skill;
	boolean busyFlag = false;

	/**
	 * Constructor for class Person
	 * 
	 * @param sc
	 *            Scanner object
	 */
	Person(Scanner sc) {
		// Assign name and index using parent class constructor
		super(sc);
		// Assign skill to the person
		if (sc.hasNext())
			skill = sc.next();
	}

	/**
	 * Method to implement toString() method for the class
	 */
	public String toString() {
		return "Person: " + super.toString() + "  " + skill;
	} // end toString

}
