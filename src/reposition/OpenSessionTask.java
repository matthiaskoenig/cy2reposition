package reposition;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.data.readers.XGMMLException;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class OpenSessionTask implements Task {
	private String fileName;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.<br>
	 *
	 * @param fileName
	 *            Session file name
	 */
	OpenSessionTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes Task
	 *
	 * @throws
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Opening Session File.\n\nIt may take a while.\nPlease wait...");
		taskMonitor.setPercentCompleted(0);

		CytoscapeSessionReader sr;

		try {
			sr = new CytoscapeSessionReader(fileName, taskMonitor);
			sr.read();
		} catch (IOException e) {
			taskMonitor.setException(e, "Cannot open the session file: " + e.getMessage());
		} catch (JAXBException e) {
			taskMonitor.setException(e, "Cannot unmarshall document: " + e.getMessage());
        } catch (XGMMLException e) {
            taskMonitor.setException(e, "XGMML format error in network: "+e.getMessage());
        } catch (Exception e) { // catch any exception: the user should know something went wrong
            taskMonitor.setException(e, "Error while loading session " + e.getMessage());
		} finally {
			sr = null;
			Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
			System.gc();
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session file " + fileName + " successfully loaded.");

		Cytoscape.setCurrentSessionFileName(fileName);

		final File sessionFile = new File(fileName);
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session: " + sessionFile.getName()
		                                + ")");
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Failed!!!");
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
		return "Opening Session File";
	}
}