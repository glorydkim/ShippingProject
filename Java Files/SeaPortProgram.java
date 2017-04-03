import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;

public class SeaPortProgram extends JFrame {

	// Object of world class to read file and create data structures
	World world;
	// Text area displaying the text in TEXT tab
	JTextArea jtextArea;
	// Text Area displaying the search result in SEARCH tab
	JTextArea searchResult;
	// ScrollPane for TEXT and TREE tab
	JScrollPane jsp, jspTree;
	// Parent panel for JOB tab (populated dynamically)
	JPanel jobPanel;

	/**
	 * Constructor for JFrame constructor to create components
	 */
	SeaPortProgram() {

		/**
		 * JFRAME initialization and creation
		 */
		// Set title for JFrame
		setTitle("Sea Port");
		// Set size for JFrame
		setSize(1200, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set layout for JFrame
		setLayout(new BorderLayout());

		// Create buttons for Read and Display
		JButton jbRead = new JButton("Read");
		JButton jbDisplay = new JButton("Display");

		// Create Panel for Buttons at the top
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(jbRead);
		buttonPanel.add(jbDisplay);

		// Implement action listener for read button
		jbRead.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventHandlerReadButton();
			}
		});

		// Implement action listener for display button
		jbDisplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventHandlerDisplayButton();
			}
		});

		/**
		 * GUI Element JTEXTAREA for TEXT TAB (Display of read data)
		 */
		// Creating text area for Text TAB in the GUI
		jtextArea = new JTextArea("No Data read yet!");
		// Scroll pane for the text area
		jsp = new JScrollPane(jtextArea);

		/**
		 * PANEL FOR SEARCH TAB
		 */
		JPanel searchPanelParent = new JPanel(new BorderLayout());
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
		JTextField searchText = new JTextField("                               ");
		searchText.setSize(100, 20);
		// Labels for search attribute
		String labels[] = { "Name", "Index", "Skills" };
		JComboBox comboBox1 = new JComboBox(labels);
		JButton searchButton = new JButton("Search");
		searchResult = new JTextArea("");
		// Add textfield for search text
		searchPanel.add(searchText);
		// Add combo box with search attributes
		searchPanel.add(comboBox1);
		// Adding search button
		searchPanel.add(searchButton);
		// Adding the search panel to search parent panel
		searchPanelParent.add(searchPanel, BorderLayout.NORTH);
		// Implement SEARCH button handler
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Call search event handler with search text, and selected
				// attribute
				eventHandlerSearchButton(searchText.getText(),
						(String) comboBox1.getItemAt(comboBox1.getSelectedIndex()));
			}
		});

		// Panel for Results from SEARCH operation
		JPanel searchResultPanel = new JPanel();
		searchResultPanel.add(searchResult);
		// Adding scrollpane to the panel
		JScrollPane jsp2 = new JScrollPane(searchResultPanel);
		searchPanelParent.add(jsp2, BorderLayout.CENTER);

		/**
		 * PANEL FOR SORT TAB
		 */
		JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
		JLabel selectFieldLabel = new JLabel("Select Field to Sort By: ");
		// Creating combo box with options to sort by
		String fields[] = { "Ships in Port's Que", "Ships in Port", "Person", "Dock", "Job" };
		JComboBox fieldText = new JComboBox(fields);
		fieldText.setSelectedIndex(2);
		JLabel selectAttributeLabel = new JLabel("Select attribute to Sort by: ");
		// Create combo box with default Sorting attribute i.e. name
		JComboBox attrText = new JComboBox();
		attrText.addItem("Name");

		// Implement combo box change listener for Sort Field selection
		fieldText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get the selected Field to sort by
				String item = (String) fieldText.getSelectedItem();
				if (item.equals("Ships in Port's Que") || item.equals("Ships in Port")) {
					// If Ships sort is selected, then Sorting can be done by
					// various
					// attributes as mentioned below. Default option is Name for
					// other
					// fields
					attrText.removeAllItems();
					// Attribute - 1 - weight, 2- length, 3-width, 4- draft, 5
					// -Name
					String[] arr = { "Weight", "Length", "Width", "Draft", "Name", "All (in sequence)" };
					for (int i = 0; i < arr.length; i++) {
						attrText.addItem(arr[i]);
					}
				} else {
					// Sort by name only for other fields
					attrText.removeAllItems();
					attrText.addItem("Name");
				}
			}
		});

		// Add Buttong for SORT
		JButton sortButton = new JButton("Sort Data");
		// Implement Buttong listener for SORT button
		sortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Call event handler method with Selected field to sort and
				// attribute
				// by which sorting need to be done
				eventHandlerSortButton((String) fieldText.getSelectedItem(), attrText.getSelectedIndex());
			}
		});

		// Add GUI elements to the SORT panel
		sortPanel.add(selectFieldLabel);
		sortPanel.add(fieldText);
		sortPanel.add(selectAttributeLabel);
		sortPanel.add(attrText);
		sortPanel.add(sortButton);

		/**
		 * PANEL FOR JOB TAB
		 */
		// This will be populated dynamically when jobs are created, will be
		// passed
		// as a parameter to the World class
		jobPanel = new JPanel(new GridLayout(20,20));		
	    JScrollPane scrollPane = new JScrollPane(jobPanel,   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    /**
		 * GUI element JTREE for TREE TAB
		 */

		// Creating Tree for TREE tab in the GUI
		jspTree = new JScrollPane(new JLabel("No data read yet"));

		/**
		 * TABS for all the Panels/GUI elements for program features
		 */
		JTabbedPane tabbedPane = new JTabbedPane();
		// Add all the tabs to the tabbedPane
		tabbedPane.addTab("Text", null, jsp, "Display all the text");
		tabbedPane.addTab("Search", null, searchPanelParent, "Search data");
		tabbedPane.addTab("Sort", null, sortPanel, "Sort Data");
		tabbedPane.addTab("Tree", null, jspTree, "Tree display");
		tabbedPane.addTab("Jobs", null, scrollPane, "Jobs");

		// Add button panel to the JFRAME
		add(buttonPanel, BorderLayout.NORTH);
		// Add TabbedPane to the JFRAME
		add(tabbedPane, BorderLayout.CENTER);
		// make jframe visible
		setVisible(true);
	}

	/**
	 * Method to handle SORT feature of the program
	 * 
	 * @param field
	 *            Field to SORT the data by
	 * @param attribute
	 *            Attribute for Field to SORT by
	 */
	public void eventHandlerSortButton(String field, int attribute) {
		// Check if data is uploaded yet, if yes, SORT else return
		if (world != null) {
			// Call method to sort the data
			world.sortData(field, attribute);
			// Refresh Display data - emulate display button handler
			eventHandlerDisplayButton();

		}
	}

	/**
	 * Method to handle SEARCH button click/selection
	 * 
	 * @param searchText
	 *            Data text to search for
	 * @param selType
	 *            Type of data to search for (name, index,skills)
	 */
	public void eventHandlerSearchButton(String searchText, String selType) {
		// Set search result to searchResult text area
		searchText = searchText.trim();
		// Check if data is uploaded yet, if yes, SEARCH, else return message
		// no data uploaded
		if (world != null) {
			// If no search data exists, display message to user
			if (searchText.equals("")) {
				searchResult.setText("Please enter target value!");
			} else {
				// Call method to search the data
				searchResult.setText(world.searchData(searchText, selType));
			}
		} else
			searchResult.setText("No data uploaded yet");
	}

	/**
	 * Method to upload and read the input file
	 */
	public void eventHandlerReadButton() {
		jobPanel.removeAll();
		// Create object of World class to upload data
		// Pass jobPanel i.e. panel for jobs that is populated dynamically
		world = new World(jobPanel);
	}

	/**
	 * Method to display file contents as TEXT in TEXT tab and TREE in TREE tab
	 */
	public void eventHandlerDisplayButton() {
		// Check if data is uploaded, if yes, display that in the tabs
		if (world != null) {
			// Creating a Tree with the data using below method
			JTree tree = new JTree(world.createNodes("World"));
			// Add TREE to TREE TAB
			jspTree.setViewportView(tree);
			// Display uploaded text in the TEXT TAB
			jtextArea.setText(world.getAllData());
		} else {
			// If no data read, display the same to user
			jtextArea.setText("No data uploaded yet");
		}
	}

	/**
	 * Main method of the program
	 * 
	 * @param args
	 *            Argument for the program
	 */
	public static void main(String args[]) {
		// Create object of the class to start the program
		SeaPortProgram newObj = new SeaPortProgram();

	}

}
