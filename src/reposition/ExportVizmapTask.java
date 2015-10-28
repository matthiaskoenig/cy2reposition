package reposition;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class ExportVizmapTask implements Task{
	private String fileName;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 */
	ExportVizmapTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes Task
	 */
	public void run() {
		taskMonitor.setStatus("Saving Visual Styles...");
		taskMonitor.setPercentCompleted(-1);

		Cytoscape.firePropertyChange(Cytoscape.SAVE_VIZMAP_PROPS, null, fileName);

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Vizmaps successfully saved to:  " + fileName);
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
		return new String("Saving Vizmap");
	}
}
