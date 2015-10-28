package reposition;

/*
 Copyright (c) 2015 Matthias Koenig

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * Plugin for reuse of available layout information in Session files.
 */

public class Reposition extends CytoscapePlugin {
    /** SBMLReader2 version */
	public static final String VERSION = "0.02";
	/** Debug and testing */
	public static final boolean debug = false;
	
	/**
	 * This constructor creates an action and adds it to the Plugins menu.
	 */
	public Reposition() {
	    // Create a new action to respond to menu activation and set in menu
	    RepositionAction action = new RepositionAction();
	    action.setPreferredMenu("Plugins");
	    Cytoscape.getDesktop().getCyMenus().addAction(action);
	}
		
	@SuppressWarnings("serial")
	public class RepositionAction extends CytoscapeAction {
		/** The constructor sets the text that should appear on the menu item.*/
	    public RepositionAction() {super("Reposition");}
	    
	    /** This method is called when the user selects the menu item.*/
	    @SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent ae) {
	    	// Load the test SBML network on which the layout information should be applied
	    	// Has to be applied prior to the positional mapping
	    	if (debug==true){
		    	String sbmlname = "/home/mkoenig/workspace/reposition/resources/examples/test_anja.sbml";
		    	Cytoscape.createNetworkFromFile(sbmlname);
		    }
	    	
		    // Test current network has nodes (no empty network allowed)
	    	// ! getCurrentNetwork() can be different from getCurrentNetworkView
	    	// TODO: Improve and get clearer
	    	// What is the difference and when can it occur
		    CyNetwork network = Cytoscape.getCurrentNetwork();
		    List<CyNode> nodes = (List<CyNode>) network.nodesList();
		    if (nodes.size() == 0){
		    	JOptionPane.showMessageDialog(null, "CurrentNetwork has no nodes !\n Network with nodes has to be loaded prior to mapping of positions to network.", "Null Node Error", JOptionPane.ERROR_MESSAGE);
		    	return;
		    }
	    	
		    //JOptionPane.showMessageDialog(null, "Select the session file ('*.cys') with the positions.", "Select session file", JOptionPane.INFORMATION_MESSAGE);
		    // Store the old session with the unpositioned network
		    // TODO: improve the handling of the temporary SBML file
		    String tmpname = "tmp.cys";
		    @SuppressWarnings("unused")
			SaveSession saveSession = new SaveSession(tmpname);
		    
			
		    // Select Cytoscape Session file with layout information
		    @SuppressWarnings("unused")
			OpenSession openSession = new OpenSession(false);
		    // TODO: Test if multiple networks are in Cytoscape session
		    network = Cytoscape.getCurrentNetwork();
		    CyNetworkView view = Cytoscape.getCurrentNetworkView();
		    
		    // Read the positional information from the selected session/network
		    // combination and store in hashmap
		    nodes = network.nodesList();
		    HashMap<String, Position> positionMap = new HashMap<String, Position>();
		    for (CyNode node : nodes){
		    	NodeView nodeView = view.getNodeView(node);
		    	positionMap.put(node.getIdentifier(), 
		    			new Position(node.getIdentifier(), 
		    							nodeView.getXPosition(),
		    							nodeView.getYPosition()));
		    }
		    // Store the current Vizmapper Layout for the new network
		    String tmpVizmapName = "tmp.props";
		    @SuppressWarnings("unused")
			ExportVizmap exportVizmap = new ExportVizmap(tmpVizmapName);
		    
		    
		    // TODO: Store node and edge attributes for reusage
		    // What happens to attributes which should not be reused (but are
		    // already available in the new network -> only overwrite attributes
		    // which have no values in the new network
		    
		    // Get the attributes of the old network
		    // nodes
		    CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		    // Store the attributes somewhere (list of hashmaps)
		    String[] nodeNames = nodeAttributes.getAttributeNames();
		    String name;
		    byte[] nodeTypes = new byte[nodeNames.length]; 
		    HashMap[] nodeStorage = new HashMap[nodeNames.length];
		    HashMap tmpMap;
		    String id;
		    
		    for (int i=0; i<nodeNames.length; ++i){
		    	name = nodeNames[i];
		    	nodeTypes[i] = nodeAttributes.getType(name);
		    	tmpMap = new HashMap();
		    	for (CyNode node : nodes){
		    		id = node.getIdentifier();
		    		tmpMap.put(id, nodeAttributes.getAttribute(id, name));
		    	}
		    	nodeStorage[i] = tmpMap;
		    }
		   
		    // edges
		    List<CyEdge> edges = network.edgesList();
		    CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		    String[] edgeNames = edgeAttributes.getAttributeNames();
		    byte[] edgeTypes = new byte[edgeNames.length]; 
		    HashMap[] edgeStorage = new HashMap[edgeNames.length];
		    
		    for (int i=0; i<edgeNames.length; ++i){
		    	name = edgeNames[i];
		    	edgeTypes[i] = edgeAttributes.getType(name);
		    	tmpMap = new HashMap();
		    	for (CyEdge edge : edges){
		    		id = edge.getIdentifier();
		    		tmpMap.put(id, edgeAttributes.getAttribute(id, name));
		    	}
		    	edgeStorage[i] = tmpMap;
		    }		   
		    
		    
		    // Apply the positional information via the node identifiers
		    // Calculate the number of nodes for which position infomration is
		    // available and for which not.
		    // TODO: Test if multiple networks are in Cytoscape session
		    // At the moment only single network per session allowed.
		    openSession = new OpenSession(tmpname, false);
		    network = Cytoscape.getCurrentNetwork();
		    view = Cytoscape.getCurrentNetworkView();
		    // apply the locations of the files
		    nodes = network.nodesList();
		    Position position;
		    for (CyNode node : nodes){
		    	id = node.getIdentifier();
		    	position = positionMap.get(id);
		    	// if no position information is available the default location
		    	// is used for the node
		    	if (position==null){
		    		position = new Position(id);
		    	}
		    	// set the position of the node
		    	NodeView nodeView = view.getNodeView(node);
		    	nodeView.setXPosition(position.getX());
		    	nodeView.setYPosition(position.getY());
		    }
		    
		    // Apply the stored attributes of the old network
		    // nodes	
		    System.out.println("# Apply Node Attributes #");
		    nodeAttributes = Cytoscape.getNodeAttributes();
		    byte type;
		    for (int i=0; i<nodeNames.length; ++i){
		    	// Set the attribute for all nodes in the current network
		    	name = nodeNames[i];
		    	type = nodeTypes[i];
		    	tmpMap = nodeStorage[i];
		    	System.out.println("Attribute name:\t" + name);
		    	
		    	for (CyNode node: nodes){
		    		id = node.getIdentifier();
		    		// if current network has no value for the id, attribute combination
		    		if (nodeAttributes.getAttribute(id, name)==null){
		    			// use the old attribute value if available
		    			if (tmpMap.get(id)!=null){
		    				switch (type){
		    					//TODO: handle the maps and list attributes
		    					case CyAttributes.TYPE_BOOLEAN:
		    						nodeAttributes.setAttribute(id, name,
		    								(Boolean) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_FLOATING:
		    						nodeAttributes.setAttribute(id, name,
		    								(Double) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_INTEGER:
		    						nodeAttributes.setAttribute(id, name,
		    								(Integer) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_STRING:
		    						nodeAttributes.setAttribute(id, name,
		    								(String) tmpMap.get(id));
		    						break;
		    				}		    				
		    			}
		    		}
		    	}
		    }
		    
		    // edges		   
		    System.out.println("# Apply Edge Attributes #");
		    edgeAttributes = Cytoscape.getEdgeAttributes();
		    for (int i=0; i<edgeNames.length; ++i){
		    	// Set the attribute for all nodes in the current network
		    	name = edgeNames[i];
		    	type = edgeTypes[i];
		    	tmpMap = edgeStorage[i];
		    	System.out.println("Attribute name:\t" + name);
		    	
		    	for (CyEdge edge: edges){
		    		id = edge.getIdentifier();
		    		// if current network has no value for the id, attribute combination
		    		if (edgeAttributes.getAttribute(id, name)==null){
		    			// use the old attribute value if available
		    			if (tmpMap.get(id)!=null){
		    				switch (type){
		    					//TODO: handle the maps and list attributes
		    					case CyAttributes.TYPE_BOOLEAN:
		    						edgeAttributes.setAttribute(id, name,
		    								(Boolean) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_FLOATING:
		    						edgeAttributes.setAttribute(id, name,
		    								(Double) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_INTEGER:
		    						edgeAttributes.setAttribute(id, name,
		    								(Integer) tmpMap.get(id));
		    						break;
		    					case CyAttributes.TYPE_STRING:
		    						edgeAttributes.setAttribute(id, name,
		    								(String) tmpMap.get(id));
		    						break;
		    				}		    				
		    			}
		    		}
		    	}	
		    }
		    
		    // Load the VizMapper from the old network
		    @SuppressWarnings("unused")
			ImportVizmap importVizmap = new ImportVizmap(tmpVizmapName);
		    //TODO: apply the old visualstyle
		    // Store the old selected vizmap value and store for reuse
		    
		    
		    
		    //Apply the visual changes
		    view.updateView();
	        view.redrawGraph(true,true);
	             
	    }
	}
}
