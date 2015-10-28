package reposition;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

public class ExportVizmap {
	private String name = null;
	
	public ExportVizmap(String name) {
		this.name = name;
		actionPerformed();
	}
	
	public ExportVizmap() {
		actionPerformed();
	}

	/**
	 * Get file name and execute the saving task<br>
	 */
	public void actionPerformed() {
		if (name==null){
			try {
				name = FileUtil.getFile("Export Vizmap property file", FileUtil.SAVE,
			                        new CyFileFilter[] {  }).toString();
			} catch (Exception exp) {
				// this is because the selection was canceled
				return;
			}
		}

		if (!name.endsWith(".props"))
			name = name + ".props";

		// Create Task
		ExportVizmapTask task = new ExportVizmapTask(name);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
	
	
}
