import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

//if no job show done and read not suspended change in line 165

public class Job extends Thing implements Runnable {

	// Duration of Job
	double duration;
	// Requirement of Job (Skills) -To be used in Project 4
	ArrayList<String> requirements;
	// eg {"painter", "painter", "painter", "carpenter"};

	// HashMap for List of Ships in the port
	HashMap<Integer, Ship> shipMap;
	// HashMap for List of docks
	HashMap<Integer, Dock> dockMap;
	// HashMap for List of Ships in Que
	HashMap<Integer, Ship> queMap;
	// HashMap for list of Ports in World
	HashMap<Integer, SeaPort> portMap;
	// HashMap for the list Persons available for Job
	HashMap<Integer, Person> usingPersonMap;

	// HashMap to keep a track of Ships waiting to Dock
	static HashMap<Integer, Ship> waitingMap = new HashMap<Integer, Ship>();

	// Panel for Job GUI
	JPanel parentPanel;
	// Button for Status
	JButton jbGo = new JButton("Suspend");
	// Button for Job cancel
	JButton jbKill = new JButton("Cancel");
	// Progress bar on Job GUI
	JProgressBar pm;

	// Label for Ship status
	JLabel shipStatus = new JLabel("", SwingConstants.CENTER);
	// Lavel for Dock time
	JLabel dockTime = new JLabel("", SwingConstants.CENTER);
	// Label for job status
	JLabel statusFlag = new JLabel("", SwingConstants.CENTER);

	// Label for Resources available
	JLabel resAvailable = new JLabel("", SwingConstants.CENTER);

	// Label for resources acquired and outstanding
	JLabel resStatus = new JLabel("", SwingConstants.CENTER);

	// Flags to be used in the program
	boolean goFlag = true, noKillFlag = true;
	// Flag to indicate if a job is cancelled
	boolean cancelFlag = false;

	// Status for each job - initial - SUSPENDED
	Status status = Status.SUSPENDED;

	/**
	 * Constants for Job Status
	 */
	enum Status {
		RUNNING, SUSPENDED, WAITING, DONE
	};

	/**
	 * Constructor for class Job
	 * 
	 * @param sc
	 *            Scanner object
	 */
	public Job(Scanner sc, JPanel jobPanel, HashMap<Integer, Ship> shipMap, HashMap<Integer, Dock> dockMap,
			HashMap<Integer, Ship> queMap, HashMap<Integer, SeaPort> PortMap, HashMap<Integer, Person> personMap) {
		super(sc);

		// Assigning the HashMap references to the class variables
		this.dockMap = dockMap;
		this.shipMap = shipMap;
		this.queMap = queMap;
		this.portMap = PortMap;

		// Initializing the map
		usingPersonMap = new HashMap<Integer, Person>();

		// Assign duration of job
		if (sc.hasNextDouble())
			duration = sc.nextDouble();

		// Create list of requirements
		requirements = new ArrayList<>();

		// Add requirements to the list
		while (sc.hasNext())
			requirements.add(sc.next());

		parentPanel = jobPanel;

		// Add job name to the panel
		parentPanel.add(new JLabel("Job: " + this.name, SwingConstants.CENTER));
		// Add ship name to the panel
		parentPanel.add(new JLabel("Ship: " + this.parent, SwingConstants.CENTER));
		// Add Arrival time to Panel
		parentPanel
				.add(new JLabel("Port Arrival Time: " + shipMap.get(this.parent).arrivalTime, SwingConstants.CENTER));
		// Add Dock time to Panel
		parentPanel.add(dockTime);

		// If ship for which job is assigned, is already docked, then get the
		// docktime for the ship
		if (dockMap.get(shipMap.get(this.parent).parent) != null)
			dockTime.setText("Dock Time: " + shipMap.get(this.parent).dockTime.toString());

		// Create and add Progress bar to the Panel
		pm = new JProgressBar();
		pm.setStringPainted(true);
		parentPanel.add(pm);

		// Add buttons for Suspend and cancel to Panel
		parentPanel.add(jbGo);
		parentPanel.add(jbKill);
		// Add Ship status on the GUI (Docked or Left the port, or in the Que)
		parentPanel.add(shipStatus);
		parentPanel.add(statusFlag);

		parentPanel.add(resAvailable);
		parentPanel.add(resStatus);

		// Button Action Listener implementation
		jbGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call method to perform the required operation
				toggleGoFlag();
			}
		});

		// Button Action Listner implementation
		jbKill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call method to kill the job
				setKillFlag();
			}
		});

		// Get the ship for which job is assigned
		Ship s = shipMap.get(this.parent);

		// Get the port for the ship
		SeaPort p;
		p = portMap.get(s.parent);
		// If ship is docked, then get Ship's port using dock's parent
		if (p == null)
			p = portMap.get(dockMap.get(s.parent).parent);

		// Check if the job requires resources that are not available at port or
		// it does not require any resources
		String statusText = checkResourcesExistence(p);
		// If statusText is not empty, then means job has to be cancelled
		// because:
		// 1. No Job requirements provided OR
		// 2. Required resources not available at PORT
		if (!statusText.isEmpty()) {
			// Cancel the job
			this.cancelFlag = true;
			// Show suspended status
			showStatus(Status.SUSPENDED);
			// Set the reason for cancellation
			statusFlag.setText(statusText);
			// Set color of panel as RED
			parentPanel.setBackground(Color.red);
		}

		// If the job is not cancelled OR if job is cancelled and ship is docked
		// (in this case, we have to remove the ship from
		// dock and put another ship from waiting in the dock, then start the
		// Thread
		if (this.cancelFlag == false || dockMap.get(s.parent) != null)
			new Thread(this).start();
	}

	/**
	 * Toggle the goFlag variable
	 */
	public void toggleGoFlag() {
		goFlag = !goFlag;
	} // end method toggleRunFlag

	/**
	 * Set the Kill flag and panel color appropriately
	 */
	public void setKillFlag() {
		noKillFlag = false;
		parentPanel.setBackground(Color.red);
	} // end setKillFlag

	/**
	 * Implement toString() method for the Job class
	 */
	public String toString() {
		return "Job: " + super.toString() + ", Duration: " + duration + " sec " + "\n";
	}

	/**
	 * Method to change panel color based on Job status and set the text for the
	 * jbGo button
	 * 
	 * @param st
	 *            Job status
	 */
	void showStatus(Status st) {
		status = st;
		switch (status) {
		case RUNNING:
			parentPanel.setBackground(Color.green);
			jbGo.setText("Running");
			break;
		case SUSPENDED:
			parentPanel.setBackground(Color.yellow);
			jbGo.setText("Suspended");
			break;
		case WAITING:
			parentPanel.setBackground(Color.orange);
			jbGo.setText("Waiting turn");
			break;
		case DONE:
			parentPanel.setBackground(Color.red);
			jbGo.setText("Done");
			break;
		} // end switch on status
	} // end showStatus

	/**
	 * Method to check if Resources required by a Job are available at Port or
	 * not
	 * 
	 * @param port
	 *            Port at which ship is assigned
	 * @return Status text, if Resources exist it will be empty , else
	 *         appropriate cancellation message
	 */
	public String checkResourcesExistence(SeaPort port) {

		// If job does not have any requirements, return cancellation message
		if (this.requirements.size() == 0) {
			System.out.println("\njob:" + this.index + "," + "Req count: " + 0 + "Exist count: " + 0);
			return "JOB CANCELLED! No Job requirements provided!";
		}

		// Get the count of requirements
		int reqCount = this.requirements.size();
		// Counter for skills that exist at port
		int existCount = 0;

		// Iterate through the requirements for the job, and
		// check if port has those skills. If any of the skill is not available,
		// return appropriate message and cancel the job
		for (int i = 0; i < reqCount; i++) {
			String req = requirements.get(i);

			for (int j = 0; j < port.persons.size(); j++) {
				Person p = port.persons.get(j);
				if (p.skill.equals(req)) {
					existCount++;
					break;
				}
			}

		}

		// If all skills required exists, return empty string
		if (reqCount <= existCount)
			return "";
		else
			// If any of the required skills is not available at port, return
			// appropriate message for job cancellation
			return "JOB CANCELLED! Requirements does not exist at Port!";

	}

	/**
	 * Implement RUN method for the Thread Jobs will run for the ships docked
	 * currently, then the program will pick up ships from the que one by one as
	 * soon as a dock gets free
	 */
	@Override
	public void run() {

		// Get the ship for which job is assigned
		Ship s = shipMap.get(this.parent);

		SeaPort p;
		p = portMap.get(s.parent);
		if (p == null)
			p = portMap.get(dockMap.get(s.parent).parent);

		// Get the current time to calculate start and stop time for the job
		long time = System.currentTimeMillis();
		long startTime = time;
		// Calculate stop time in milliseconds using start time and duration of
		// job
		long stopTime = time + 1000 * (long) duration;
		double durationT = stopTime - time;

		// Check if the ship is in Que or docked
		if (queMap.get(s.index) != null) { // If the ship is waiting in Que, add
											// the object to the waitingMap
			waitingMap.put(s.index, s);
		}

		// Synchronize on Port to avoid race condition
		synchronized (p) {
			// Check if ship is docked or not, if yes, then check if all
			// resources
			// are available, if yes then proceed, else make
			// the ship wait (show status accordingly on GUI)
			int resFlag = 0;
			// Check if the job was cancelled due to unavailability of skills at
			// Port
			// or no requirements provided
			// If job is not cancelled, check for resources
			if (this.cancelFlag == false)
				resFlag = getResources(s, requirements, p);

			/**
			 * resFlag - 1 Ship is still waiting to be docked in the que , 0 -
			 * Ship is docked and has all the skills available, start the job, 2
			 * - Ship is docked, but all skills not available, job is waiting
			 **/
			while (resFlag == 1 || resFlag == 2) {
				/// Set the color and jbGo button text accordingly to indicate
				/// Waiting status
				showStatus(Status.WAITING);
				// Check if job is waiting for ship to be docked - resFlag = 1
				// OR
				// if job is waiting for resources - resFlag 2 , set the status
				// appropriately
				if (resFlag == 1)
					shipStatus.setText("Ship in Que...");
				else
					shipStatus.setText("Waiting for resources");
				try {
					// Call thread wait method to wait till notified i.e. any of
					// the dock is free or any of the resources gets available
					p.wait();
				} catch (InterruptedException e) {
				} // end try/catch block
					// Check for docking of ship and resource availability again
				resFlag = getResources(s, requirements, p);
			} // end while waiting for dock to be free or resources to be
				// available
		} // end sychronized on portMap

		// Set the text of the label for docktime
		dockTime.setText("Dock Time: " + s.dockTime.toString());

		// Keep executing the job for the duration provided job is not killed by
		// user
		while (time < stopTime && noKillFlag) {
			// Set label accordingly
			shipStatus.setText("Ship at Dock, Jobs running!");

			// Call sleep method for the Thread
			try {
				// This case is only when ship is originally Docked, but its
				// job(s)
				// were cancelled, so we mock it for just a minute second and
				// remove
				// the ship from dock eventually
				if (this.cancelFlag) {
					Thread.sleep(1);
				} else
					Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			// If goFlag is set, status is RUNNING
			if (goFlag) {
				showStatus(Status.RUNNING);
				time += 100;
				// Show progress percentage on the GUI
				if (!this.cancelFlag) {
					pm.setValue((int) (((time - startTime) / durationT) * 100));
				}
			} else {
				// If job is suspended/cancelled by user, show Suspended status
				showStatus(Status.SUSPENDED);
			} // end if stepping
		} // end running

		// Job is completed at this point
		if (!this.cancelFlag) {
			// Set the progress bar value to 100
			pm.setValue(100);
		}

		// Show status as SUSPENDED, if job was cancelled
		if (this.cancelFlag == true) {
			showStatus(Status.SUSPENDED);
			this.setKillFlag();
		}

		// Show status as DONE, if the job was not cancelled and completed
		else {
			showStatus(Status.DONE);
		}

		// If the job was for a waiting ship that was served after dock got free
		// remove it from the waiting Map
		if (waitingMap.get(s.index) != null)
			waitingMap.remove(s.index);

		if (this.cancelFlag == false)
			shipStatus.setText("Job completed, Waiting for other jobs to finish");

		// Called when a job on a ship is over, and/OR ship lefts the dock,
		// Assign the dock to another ship from the waiting list, and add
		// resources back
		// to available pool, notify the waiting threads using notifyAll()
		synchronized (p) {
			// If job is not cancelled, release the resources back to pool
			if (this.cancelFlag == false)
				releaseResources(p);

			boolean doneFlag = true;

			// Checking if all jobs are suspended
			for (int i = 0; i < s.jobs.size(); i++) {
				if (s.jobs.get(i).cancelFlag == true) {
					if (s.jobs.get(i).status != Status.SUSPENDED) {
						doneFlag = false;
						break;
					}
				}
			}

			// Checking if all jobs for the ship are done
			for (int i = 0; i < s.jobs.size(); i++) {
				if (s.jobs.get(i).cancelFlag == false) {
					if (s.jobs.get(i).status != Status.DONE) {
						doneFlag = false;
						break;
					}
				}
			}

			// If all the jobs on a ship at dock are done, then proceed to dock
			// another ship from the waiting que
			if (doneFlag) {

				// Set the status accordingly
				if (this.cancelFlag == false)
					shipStatus.setText("All Jobs completed, Ship left Dock");
				else
					shipStatus.setText("All Jobs cancelled, Ship left Dock");

				// Check if any ship is still in Que/waiting
				if (waitingMap.size() > 0) {
					// Get the first ship in the waiting que
					boolean sflag = false;
					Dock d = dockMap.get(s.parent);
					int portIndex = d.parent;
					Ship upShip = null;
					Map.Entry<Integer, Ship> entry = null;

					for (Map.Entry<Integer, Ship> entry1 : waitingMap.entrySet()) {
						upShip = entry1.getValue();
						if (upShip.parent == portIndex) {
							// Assign parent of ship as Dock to be assigned to
							// it (Dock index got free from previous ship)
							upShip.parent = s.parent;
							entry = entry1;
							break;
						}
					}

					if (entry != null) {
						// set the dock time for the ship
						Calendar calendar = Calendar.getInstance();
						int second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60
								+ calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;
						upShip.dockTime = new PortTime(second);
						waitingMap.remove(entry.getKey());
						// Update the ship in the waiting HashMap with the
						// new
						// parent as DockIndex instead of existing
						// Port index (for waiting ships)
						waitingMap.put(upShip.index, upShip);
					}
				}
			}
			// Notify all the waiting threads to check if its
			// ship is docked or not, and if resources are available now,
			// if docked and all resources available, proceed with
			// executing the jobs, else WAIT.
			p.notifyAll();
		}

	}

	/**
	 * Release all the resources acquired by job when it finishes
	 * 
	 * @param sp
	 *            Port at which Ship is there
	 */
	public synchronized void releaseResources(SeaPort sp) {

		// Iterate through Map with acquired resources by Job
		for (Map.Entry<Integer, Person> entry2 : usingPersonMap.entrySet()) {

			// Add the resources back to the Global skills availability map sp
			sp.persons.add(entry2.getValue());
		}
	}

	/**
	 * Method to check if Job can be started i.e. it has all the required skills
	 * available and is docked
	 * 
	 * @param s
	 *            Ship the job has been assigned to
	 * @param requirement
	 *            List of requirements by Job
	 * @param sp
	 *            SeaPort at which Ship is parked
	 * @return 1 - Ship is still waiting to be docked in the que , 0 - Ship is
	 *         docked and has all the skills available, start the job , 2 - Ship
	 *         is docked, but all skills not available, job is waiting
	 */
	public synchronized int getResources(Ship s, ArrayList<String> requirement, SeaPort sp) {
		// Number of requirements for the job
		int reqCount = requirement.size();

		// Available number of persons with the required skills
		int availCount = 0;

		// Check if ship is docked or still waiting in Que
		// If ship is waiting
		if (dockMap.get(s.parent) == null) {
			// Available Resources, is 0
			resAvailable.setText("Available Resources: " + availCount);
			// Resources Acquired 0, and outstanding = total requirement count
			resStatus.setText("Resources Acquired: 0" + ", Resources Outstanding: " + reqCount);
			// Return 1 to indicate Ship is not docked and still in Que
			return 1;
		}

		// If Ship is Docked, check for availability of Skills at Port

		// Iterate through the requirements for the job, and
		// check if port has skills available. If any of the skill is not
		// available,
		// then the job will be in waiting state
		for (int i = 0; i < reqCount; i++) {
			String req = requirements.get(i);
			for (int j = 0; j < sp.persons.size(); j++) {
				Person p = sp.persons.get(j);
				if (p.skill.equals(req)) {
					usingPersonMap.put(p.index, p);
					// Increment the available resources count
					availCount++;
					break;
				}
			}

		}

		// Set the Available Resources status on the Job Panel
		resAvailable.setText("Available Resources: " + availCount);

		// Check if this available count is >= Requirement for the job
		// IF yes, then Start the job and return 0
		if (reqCount <= availCount) {
			// Removing skills from the availability pool
			for (Map.Entry<Integer, Person> entry5 : usingPersonMap.entrySet()) {
				sp.persons.remove(entry5.getValue());
			}
			// Set the resources status on the job panel
			resStatus.setText("Resources Acquired: " + reqCount + ", Resources Outstanding: 0 ");
			return 0;
		}
		// IF no, then job keeps waiting, and the skills acquired are added back
		// to available pool
		else {

			// Resources status on job panel update
			resStatus.setText(
					"Resources Acquired: " + availCount + ", Resources Outstanding: " + (reqCount - availCount));

			for (Map.Entry<Integer, Person> entry2 : usingPersonMap.entrySet()) {
				// usingPersonMap.remove(entry2.getKey());
				sp.persons.add(entry2.getValue());
			}

			usingPersonMap.clear();
			return 2;
		}
	}
}
