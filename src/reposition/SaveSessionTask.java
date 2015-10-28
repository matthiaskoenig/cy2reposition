package reposition;

import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

/**
 * Save Session Task.<br>
 * Call the Session Writer to save the following:<br>
 * <ul>
 * <li>Networks with metadata</li>
 * <li>All attributes (for nodes, edges, and network)</li>
 * <li>Visual Styles</li>
 * <li>Cytoscape Properties</li>
 * </ul>
 *
 * @author kono
 *
 */
class SaveSessionTask implements Task {
	private String fileName;
	private TaskMonitor taskMonitor;
	CytoscapeSessionWriter sw;

	/**
	 * Constructor.<br>
	 *
	 * @param fileName
	 *            Absolute path to the Session file.
	 */
	SaveSessionTask(String fileName) {
		this.fileName = fileName;
		// Create session writer object
		sw = new CytoscapeSessionWriter(fileName);
	}

	/**
	 * Execute task.<br>
	 */
	public void run() {
		taskMonitor.setStatus("Saving Cytoscape Session.\n\nIt may take a while.  Please wait...");
		taskMonitor.setPercentCompleted(-1);

		try {
			sw.writeSessionToDisk();
		} catch (Exception e) {
			taskMonitor.setException(e, "Could not write session to the file: " + fileName);
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session successfully saved to:  " + fileName);

		// Show the session Name as the window title.
		File shortName = new File(fileName);
		Cytoscape.setCurrentSessionFileName(fileName);
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session: " + shortName.getName() + ")");
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return Task Title.
	 */
	public String getTitle() {
		return "Saving Cytoscape Session";
	}
} // End of SaveSessionTask