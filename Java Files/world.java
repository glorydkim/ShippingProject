import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

public class World extends Thing {

	// Data structure HashMap for storing Ports
	HashMap<Integer, SeaPort> portsMap;
	// Data structure HashMap for storing Persons working at a Port
	HashMap<Integer, Person> personMap;
	// Data structure HashMap for storing Docks in a port
	HashMap<Integer, Dock> dockMap;
	// Data structure HashMap for storing Jobs required at the Port
	HashMap<Integer, Job> jobMap;
	// Data structure HashMap for storing Ships and ships waiting in que
	HashMap<Integer, Ship> shipMap, queMap;

	// Parent panel for Job TAB
	JPanel jobPanel;
	PortTime time;

	/**
	 * Constructor for the class
	 * 
	 * @param sc
	 *            Scanner object
	 */
	World(Scanner sc) {
		// Call parent class constructor
		super(sc);
	}

	/**
	 * Constructor for class
	 * 
	 * @param jPanel
	 *            Panel object for JOB Tab (populated dynamically)
	 */
	World(JPanel jPanel) {
		jobPanel = jPanel;
		// Initializing HashMap data structures to be populated on file upload
		portsMap = new HashMap<Integer, SeaPort>();
		dockMap = new HashMap<Integer, Dock>();
		jobMap = new HashMap<Integer, Job>();
		shipMap = new HashMap<Integer, Ship>();
		queMap = new HashMap<Integer, Ship>();
		personMap = new HashMap<Integer, Person>();
		// Select File using the method
		File file = fileChooser();
		// Read file contents from the file object
		readFileContents(file);
	}

	/**
	 * Method to select a file from the user's system
	 * 
	 * @return Return file object with the name and path
	 */
	private File fileChooser() {

		// selecting the txt file gui
		JFileChooser jFileChooser = new JFileChooser();

		// only accept text files
		FileNameExtensionFilter textFilter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		jFileChooser.setFileFilter(textFilter);

		int result = jFileChooser.showOpenDialog(null);
		File selectedFile = jFileChooser.getSelectedFile();

		// if file select print text function
		if (result == JFileChooser.APPROVE_OPTION) {
			return selectedFile;
		} else {
			System.out.println("Error selecting file");
			return null;
		}
	}

	/**
	 * Method to read the file contents and process each line populating
	 * appropriate data structures for Port,Dock,Ship,Job,Person
	 * 
	 * @param selectedFile
	 *            File object with name and path
	 */
	private void readFileContents(File selectedFile) {
		String textFile = "";
		try {

			// start file reader and buffered reader to read txt file that was
			// selected
			FileReader fileReader = new FileReader(selectedFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();

			// While line exists, read and process it
			while (line != null) {
				textFile += line;
				// Process the line appropriately, and add to data structure if
				// required
				processLine(line);
				// Read next line from the file
				line = bufferedReader.readLine();
			}

			// close bufferedReader and fileReader
			bufferedReader.close();
			fileReader.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		System.out.println(textFile);
	}

	/**
	 * Method to process each line from the file and call appropriate method to
	 * take action
	 * 
	 * @param line
	 *            Line text read from file
	 */
	public void processLine(String line) {
		System.out.println("Processing >" + line + "<");
		// Create scanner object for the line for processing
		Scanner sc = new Scanner(line);
		// If line is empty, dont process and return
		if (!sc.hasNext())
			return;
		// Switch on the line first word - Port, Dock, Ship,Person, etc.
		switch (sc.next()) {
		case "port":
			// Initialize the port i.e. create port object and add the port to
			// the data structures
			addPort(sc);
			break;
		case "dock":
			// Initialize the dock .e. create dock object and add the dock to
			// the data structures
			addDock(sc);
			break;
		case "cship":
			// Initialize the Cargo ship i.e. create Cargo ship object and add
			// the ship to the data structures
			addCShip(sc);
			break;
		case "pship":
			// Initialize the Passenger ship i.e. create Passenger ship object
			// and add the ship to the data structures
			addPShip(sc);
			break;
		case "person":
			// Initialize the Person i.e. create Person object and add the
			// Person to the data structures
			addPerson(sc);
			break;
		case "job":
			// Initialize the Job i.e. create Job object and add the job to the
			// data structures and its
			// Panel of the Job tab
			addJob(sc);
		default:
			// Do nothing;
			break;
		}
	}

	/**
	 * Method to create a new port
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public void addPort(Scanner sc) {
		// Create new SeaPort object
		SeaPort newPort = new SeaPort(sc);
		// Add to the port HashMap
		portsMap.put(newPort.index, newPort);
	}

	/**
	 * Method to create a new Dock in the port
	 * 
	 * @param sc
	 *            Scanner Object to read values
	 */
	public void addDock(Scanner sc) {
		// Create new Dock object
		Dock d = new Dock(sc);
		// Add Dock to the Dock HashMap
		dockMap.put(d.index, d);
		// Add Dock to its parent Port DockList
		portsMap.get(d.parent).docks.add(d);
	}

	/**
	 * Method to create Person working at a Port
	 * 
	 * @param sc
	 *            Scanner Object to read values
	 */
	public void addPerson(Scanner sc) {
		// Create new Person
		Person newPerson = new Person(sc);
		// Add Person to the Person HashMap
		personMap.put(newPerson.index, newPerson);
		// Add Person to the Port's PersonList
		portsMap.get(newPerson.parent).persons.add(newPerson);
	}

	/**
	 * Method to create a new Job at the Port
	 * 
	 * @param sc
	 *            Scanner Object to read values
	 */
	public void addJob(Scanner sc) {
		// Creating panel for each job
		JPanel newJobPanel = new JPanel(new GridLayout(11, 1));
		newJobPanel.setBorder(BorderFactory.createLineBorder(Color.black,2));
		// Create the Job object and start/pause the job(if job is for ship in
		// que)
		Job j = new Job(sc, newJobPanel, shipMap, dockMap, queMap,portsMap,personMap);
		// Adding job panel to parent panel
		jobPanel.add(newJobPanel);
		// Adding Job to the job HashMap
		jobMap.put(j.index, j);
		// Add job to the assigned Ship JobList
		shipMap.get(j.parent).jobs.add(j);
	}

	/**
	 * Method to create new PassengerShip object
	 * 
	 * @param sc
	 *            Scanner Object to read values
	 */
	public void addPShip(Scanner sc) {
		// Create new Passenger ship object
		PassengerShip newShip = new PassengerShip(sc);
		// Add ship to the Ship HashMap
		shipMap.put(newShip.index, newShip);
		// Check if Ship is waiting at Port or is at a Dock
		// If ship is at Port, and waiting to Dock
		if (portsMap.get(newShip.parent) != null) {
			// Add Ship to the Port's ShipList
			portsMap.get(newShip.parent).ships.add(newShip);
			// Add Ship to the Port's Que (waiting list)
			portsMap.get(newShip.parent).que.add(newShip);
			// Add ship to the Que HashMap
			queMap.put(newShip.index, newShip);
		} else {
			// If ship is docked
			if (dockMap.get(newShip.parent) != null) {
				// Get the Dock object for the dock at which the ship is docked
				// at
				Dock d = dockMap.get(newShip.parent);
				// Add the Ship to the Port's ShipList
				portsMap.get(d.parent).ships.add(newShip);
				// Add the Ship to the Dock's ship object
				dockMap.get(newShip.parent).ship = newShip;
				// Assign the dock time to the ship
				Calendar calendar = Calendar.getInstance();
			    int second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR_OF_DAY) * 60*60;
				newShip.dockTime = new PortTime(second);
			}
		}
	}

	/**
	 * Method to create new CargoShip object
	 * 
	 * @param sc
	 *            Scanner Object to read values
	 */
	public void addCShip(Scanner sc) {
		// Create new Cargo ship object
		CargoShip newShip = new CargoShip(sc);
		// Add ship to the Ship HashMap
		shipMap.put(newShip.index, newShip);
		// Check if Ship is waiting at Port or is at a Dock
		// If ship is at Port, and waiting to Dock
		if (portsMap.get(newShip.parent) != null) {
			// Add Ship to the Port's ShipList
			portsMap.get(newShip.parent).ships.add(newShip);
			// Add Ship to the Port's Que (waiting list)
			portsMap.get(newShip.parent).que.add(newShip);
			// Add ship to the Que HashMap
			queMap.put(newShip.index, newShip);

		} else {
			// If ship is docked
			if (dockMap.get(newShip.parent) != null) {
				// Get the Dock object for the dock at which the ship is docked
				// at
				Dock d = dockMap.get(newShip.parent);
				// Add the Ship to the Port's ShipList
				portsMap.get(d.parent).ships.add(newShip);
				// Add the Ship to the Dock's ship object
				dockMap.get(newShip.parent).ship = newShip;
				// Assign the dock time to the ship
				Calendar calendar = Calendar.getInstance();
			    int second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR_OF_DAY) * 60*60;
				newShip.dockTime = new PortTime(second);
			}

		}
	}

	/**
	 * Method to SORT The data structures as per the criteria selected by the
	 * user
	 * 
	 * @param field
	 *            Field to sort the data by
	 * @param attribute
	 *            Field attribute to the sort the data by
	 */
	public void sortData(String field, int attribute) {

		// Sort data for each SeaPort
		for (Map.Entry<Integer, SeaPort> entry : portsMap.entrySet()) {
			SeaPort p = (SeaPort) entry.getValue();

			// Switch case for Field type
			switch (field) {
			// IF Ships in Que to be sorted
			case "Ships in Port's Que":
				// Sort the Ships in the Port's queList
				Collections.sort(p.que, new Comparator<Ship>() {
					@Override
					public int compare(Ship arg0, Ship arg1) {
						return arg0.compareTo(arg1, attribute);
					}
				});
				break;
			// If all the ships in the Port to be sorted
			case "Ships in Port":
				// Sort the ships in the Port's ShipList
				Collections.sort(p.ships);
				// Sort the ships in the Ship HashMap
				shipMap = new LinkedHashMap<Integer, Ship>();
				for (int i = 0; i < p.ships.size(); i++) {
					shipMap.put(p.ships.get(i).index, p.ships.get(i));
				}
				break;
			// If all the Persons at the Port to be sorted
			case "Person":
				// Sort the Persons in the Port's PersonList
				Collections.sort(p.persons);

				// Sort the Persons in the Person HashMap
				personMap = new LinkedHashMap<Integer, Person>();
				for (int i = 0; i < p.persons.size(); i++) {
					personMap.put(p.persons.get(i).index, p.persons.get(i));
				}
				break;
			// If all the docks to be sorted
			case "Dock":
				// Sort the Docks in the Port's DockList
				Collections.sort(p.docks);
				// Sort the Dock HashMap
				dockMap = new LinkedHashMap<Integer, Dock>();
				for (int i = 0; i < p.docks.size(); i++) {
					dockMap.put(p.docks.get(i).index, p.docks.get(i));
				}
				break;
			// If all the jobs to be sorted
			case "Job":
				// Sort the jobs by name for all the ships
				for (int i = 0; i < p.ships.size(); i++) {
					Collections.sort(p.ships.get(i).jobs);
				}

				break;
			default:
				break;
			}
		}
	}

	/**
	 * Method to return the display text for the TEXT tab of the GUI
	 * 
	 * @return
	 */
	public String getAllData() {
		String dataStr = ">>>>> The world:\n\n";

		// Read data from the Port HashMap and print each Port's data
		for (Map.Entry<Integer, SeaPort> entry : portsMap.entrySet()) {
			dataStr = dataStr + entry.getValue();
		}
		// Return the final data
		return dataStr;
	}

	/**
	 * Method to perform SEARCH operation on the data
	 * 
	 * @param searchText
	 *            Search Text
	 * @param selType
	 *            Type of data i.e. Name, Index or Skills
	 * @return Return the corresponding object from the search
	 */
	public String searchData(String searchText, String selType) {
		/**
		 * SEARCH BY Name
		 */
		if (selType.equals("Name")) {

			// Check if name matches to any of the Port's name
			for (Map.Entry<Integer, SeaPort> entry : portsMap.entrySet()) {
				SeaPort p = (SeaPort) entry.getValue();
				if (p.name.equals(searchText)) {
					// Return found Port as a String to be printed in the result
					return p.toString();
				}
			}

			// Check if name matches to any of the Dock's name
			for (Map.Entry<Integer, Dock> entry2 : dockMap.entrySet()) {
				Dock d = (Dock) entry2.getValue();
				if (d.name.equals(searchText)) {
					// Return found Dock as a String to be printed in the result
					return d.toString();
				}
			}

			// Check if name matches to any of the Ships' name
			for (Map.Entry<Integer, Ship> entry3 : shipMap.entrySet()) {
				Ship s = (Ship) entry3.getValue();
				if (s.name.equals(searchText)) {
					// Return found Ship as a String to be printed in the result
					return s.toString();
				}
			}

			// Check if name matches to any of the Persons' name
			for (Map.Entry<Integer, Person> entry4 : personMap.entrySet()) {
				Person pr = (Person) entry4.getValue();
				if (pr.name.equals(searchText)) {
					// Return found Person as a String to be printed in the
					// result
					return pr.toString();
				}
			}

			// Check if name matches to any of the Jobs' name
			for (Map.Entry<Integer, Job> entry6 : jobMap.entrySet()) {
				Job j = (Job) entry6.getValue();
				if (j.name.equals(searchText)) {
					// Return found Job as a String to be printed in the result
					return j.toString();
				}
			}
		}

		/**
		 * SEARCH By Index
		 */
		else if (selType.equals("Index")) {
			// Convert text containing integer value to Integer value
			int searchIndex = Integer.parseInt(searchText);

			// Check if index matches a Port's index
			if (portsMap.get(searchIndex) != null) {
				// If found, return the Port to print as search result
				return portsMap.get(searchIndex).toString();
			}

			// Check if index matches a Dock's index
			if (dockMap.get(searchIndex) != null) {
				// If found, return the Dock to print as search result
				return dockMap.get(searchIndex).toString();
			}

			// Check if index matches a Ship's index
			if (shipMap.get(searchIndex) != null) {
				// If found, return the Ship to print as search result
				return shipMap.get(searchIndex).toString();
			}

			// Check if index matches a Person's index
			if (personMap.get(searchIndex) != null) {
				// If found, return the Person to print as search result
				return personMap.get(searchIndex).toString();
			}

			// Check if index matches a Job's index
			if (jobMap.get(searchIndex) != null) {
				// If found, return the Job to print as search result
				return jobMap.get(searchIndex).toString();
			}
		}
		// search by skills
		else {
			// To be implemented in Project 4
		}

		// If no data found, return result appropriate string result as below
		return "No Data found with the search Criteria!!!";

	}

	/**
	 * Method to create a TREE for the Port data
	 * 
	 * @param title
	 *            Title of the Tree
	 * @return Return the TreeNode object having all the data in a tree
	 *         structure(nested tree nodes)
	 */
	public DefaultMutableTreeNode createNodes(String title) {
		// Top Tree Node for the data, initializing and creating it
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(title);

		// Tree Node for Ports (will be added to top Node)
		DefaultMutableTreeNode spn;

		// Tree Head Node for the Docks (will be added to spn Node)
		DefaultMutableTreeNode dockHead;
		// Tree Node for Docks (will be added to the dockHead Node)
		DefaultMutableTreeNode dn;
		// Tree Node for Dock's ship (will be added to dn Node)
		DefaultMutableTreeNode dns;

		// Tree Head Node for Ships in the Que (will be added to the spn Node)
		DefaultMutableTreeNode queHead;
		// Tree Node for the ships in Que (will be added to the queHead Node)
		DefaultMutableTreeNode qn;

		// Tree Head Node for Ships in Port (will be added to the spn Node)
		DefaultMutableTreeNode shipHead;
		// Tree Node for the ships in Port (will be added to the shipHead Node)
		DefaultMutableTreeNode sn;

		// Tree Head Node for Persons in Port (will be added to the spn Node)
		DefaultMutableTreeNode personHead;
		// Tree Node for the Persons in Port (will be added to the personHead
		// Node)
		DefaultMutableTreeNode pn;

		// TreeNode for Job Node for a ship (will be added to the Ship Node
		// i.e.qn,sn)
		DefaultMutableTreeNode jn;

		// Add Seaport to Tree structure
		for (Map.Entry<Integer, SeaPort> entry : portsMap.entrySet()) {
			/**
			 * ADD SeaPort to the TREE Structure
			 */
			SeaPort p = (SeaPort) entry.getValue();
			// Creating TreeNode for the SeaPort with the data
			spn = new DefaultMutableTreeNode("Sea Port: " + p.name + " " + p.index);
			// Add the Port to the TOP TreeNode
			top.add(spn);

			/**
			 * ADD Docks to the TREE structure
			 */
			// Initialize DockHead TreeNode
			dockHead = new DefaultMutableTreeNode("Docks: ");
			// Add the Head Node to the Parent TreeNode i.e. spn node
			spn.add(dockHead);

			// Loop through all the Docks in the Port and add node for each one
			for (Map.Entry<Integer, Dock> entry2 : dockMap.entrySet()) {
				Dock d = (Dock) entry2.getValue();
				// Assign Dock to its parent Node i.e. Port
				if (d.parent == p.index) {
					// Create new Dock node
					dn = new DefaultMutableTreeNode("Dock: " + d.name + " " + d.index);
					// Create new Ship node under Dock
					dns = new DefaultMutableTreeNode(d.ship);
					// Add the ship node to the Dock's
					dn.add(dns);
					// Add the Dock node to the DockHead node
					dockHead.add(dn);
				}
			}

			/**
			 * ADD Ships in Que to the TREE structure
			 */
			// Initialize the QueHead TreeNode
			queHead = new DefaultMutableTreeNode("List of Ships in Que: ");
			// Add the node to its parent node i.e. Port TreeNode
			spn.add(queHead);

			// Retrieve the list of ships in the Que at Port
			ArrayList<Ship> que = p.que;

			// Loop through the list and add node for each ship to the Que Head
			for (int i = 0; i < que.size(); i++) {
				Ship s = que.get(i);
				// Create new Ship in Que node
				qn = new DefaultMutableTreeNode(s.getClass().getSimpleName() + ": " + s.name + " " + s.index);

				// Loop through the Jobs for the Ship
				for (int j = 0; j < s.jobs.size(); j++) {
					// Create job node
					jn = new DefaultMutableTreeNode("Job: " + s.jobs.get(j).name + " " + s.jobs.get(j).index);
					// Add to the ship's node
					qn.add(jn);
				}

				// Add ship node to the Que Head
				queHead.add(qn);
			}

			/**
			 * ADD all Ships at the PORT/DOCK to the TREE structure
			 */
			// Initialize the Ship Head tree node
			shipHead = new DefaultMutableTreeNode("List of Ships: ");
			// Add it to the parent node i.e. Ports
			spn.add(shipHead);

			// Add all Ships to Tree
			for (Map.Entry<Integer, Ship> entry4 : shipMap.entrySet()) {
				Ship s = (Ship) entry4.getValue();
				sn = new DefaultMutableTreeNode(s.getClass().getSimpleName() + ": " + s.name + " " + s.index);
				// Loop through the Jobs for the Ship
				for (int j = 0; j < s.jobs.size(); j++) {
					// Create job node
					jn = new DefaultMutableTreeNode("Job: " + s.jobs.get(j).name + " " + s.jobs.get(j).index);
					// Add to the ship's node
					sn.add(jn);
				}

				shipHead.add(sn);
			}

			/**
			 * ADD all Persons at the Port to the TREE structure
			 */
			// Initialize the PersonHead tree node
			personHead = new DefaultMutableTreeNode("List of Persons: ");
			// Add it the parent node i.e Ports
			spn.add(personHead);

			// Add Person to Tree
			for (Map.Entry<Integer, Person> entry5 : personMap.entrySet()) {
				Person pr = (Person) entry5.getValue();
				// Create a new Person Node
				pn = new DefaultMutableTreeNode(pr);
				// Add it to the Person Head node
				personHead.add(pn);
			}

		}
		// Return the Parent (top most) Tree node containing all the
		// hierarchical nodes
		return top;
	}

}
