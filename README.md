# cy2reposition: Layout reuse in Cytoscape

<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>

**cy2reposition** is an open source Cytoscape 2 Plugin for the reuse of existing Cytoscape network layouts in new Cytoscape projects.

[![Download](docs/images/icon-download.png) Download](https://github.com/matthiaskoenig/cy2reposition/releases/latest)  
**Status** : beta  
**Support & Forum** : https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker** : https://github.com/matthiaskoenig/cy2reposition/issues  

## Features
- reads positional information from given Cytoscape session files for the current network
- node and edge attribute information are reused where possible
- current visual style is also reused

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

## Install
* add `cy2reposition-vx.x.jar` to the Cytoscape Plugin folder

## Uninstall
* remove `cy2reposition-vx.x.jar` from the Cytoscape Plugin folder

## Build instructions
Clone the repository and build with `ant`
```
git clone https://github.com/matthiaskoenig/cy2reposition.git cy2reposition
cd cy2reposition
ant cy2reposition
```
Development is done in `develop` branch, documentation in the `README.md`
```
git checkout -b develop --track origin/develop
```

## Usage
 After installation the Reposition plugin is integrated into Cytoscape at startup and is available via the plugin menu.

1. Load and select the network to which the layout should be applied. For example a given SBML file of the network.  
`File -> New -> Session and load the network`  
`File -> Import -> Network (multiple file types)...`

2. Apply the layout from a given session file. Select the Reposition Plugin from the menu  
`Plugins -> Reposition`  
and select the Cytoscape Session file (*.cys) with the layout.

The positional information from the selected Cytoscape session is applied to the current network. The mapping of the layout is performed based on the xPosition and yPosition associated with the node identifiers. Nodes in the network with no positional information in the selected session file are mapped to standard positions. 

## Changelog
**v0.1.0** 
- sourceforge to github migration
- logging added
- libraries updated to Cytoscape 2.8.3

**v0.02** 
- basic release
