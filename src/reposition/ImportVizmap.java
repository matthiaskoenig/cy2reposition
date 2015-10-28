package reposition;

import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

public class ImportVizmap{
	private String name;
	
	/**
	 * Creates a new ImportVizmap object.
	 */
	public ImportVizmap(String name) {
		this.name = name;
		actionPerformed();
	}

	public void actionPerformed() {
		final CyFileFilter propsFilter = new CyFileFilter();
		propsFilter.addExtension("props");
		propsFilter.setDescription("Property files");

		File file;
		if (name==null){
			// Get the file name
			 file = FileUtil.getFile("Import Vizmap Property File", FileUtil.LOAD,
		                                   new CyFileFilter[] { propsFilter });
		}
		else {
			file = new File(name);
		}

		// if the name is not null, then load
		if (file != null) {
			// Create LoadNetwork Task
			LoadVizmapTask task = new LoadVizmapTask(file);

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pops open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}
}