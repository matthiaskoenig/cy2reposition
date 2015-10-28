package reposition;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

public class OpenSession {
	public static final String SESSION_EXT = "cys";
	private String name = null; // Name of the file to be opened.
	
	public OpenSession(Boolean doPreparation){
		actionPerformed(doPreparation);
	}
	public OpenSession(String name, Boolean doPreparation){
		this.name = name;
		actionPerformed(doPreparation);
	}
	
	/**
	 * Clear current session and open the cys file.
	 */
	public void actionPerformed(Boolean doPreparation) {
		Boolean proceed = true;
		if (doPreparation==true){
			proceed = prepare();
		}
		if (proceed) {
			// Create FileFilters
			final CyFileFilter sessionFilter = new CyFileFilter();

			// Add accepted File Extensions
			sessionFilter.addExtension(SESSION_EXT);
			sessionFilter.setDescription("Cytoscape Session files");

			// Open Dialog to ask user the file name if no filename is given
			if (name==null){
				try {
					name = FileUtil.getFile("Open a Session File", FileUtil.LOAD,
				                        new CyFileFilter[] { sessionFilter }).toString();
				} catch (Exception exp) {
					// this is because the selection was canceled
					return;
				}
			}
			
			// Close all networks in the workspace.
			Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
			Cytoscape.createNewSession();
			Cytoscape.setSessionState(Cytoscape.SESSION_NEW);

			// Create Task
			final OpenSessionTask task = new OpenSessionTask(name);

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayCancelButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}
		
	/**
	 * Before loading the new session, we need to clean up current session.
	 *
	 */
	private boolean prepare() {
		final int currentNetworkCount = Cytoscape.getNetworkSet().size();

		if (currentNetworkCount != 0) {
			// Show warning
			final String warning = "Current session will be lost.\nDo you want to continue?";

			final int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), warning,
			                                                 "Caution!", JOptionPane.YES_NO_OPTION,
			                                                 JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	
}
