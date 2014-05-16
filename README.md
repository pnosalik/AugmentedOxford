# AugmentedOxford

# App usage

## Tour Selection

Tap on a tour name to display information about the tour such as a description of the tour and a list of POIs.
Click on "Start Tour" to start the augmented reality, displaying POI makers one by one at each location.
Click on "Demo Tour" to start a tour in augmented reality using nearby locations for the markers instead of GPS coordinates of the POIs.
Click the ? button to display help dialogue for selecting tours.
Click the drop-down menu to sort the tours by filename, proximity or tour name.
Click the refresh button to refresh the list of tours.
Click the about button to display information about the project and libraries used.


## Augmented Reality

Each marker is coloured green if unvisited, and is made blue once visited (markers do not seem to display properly on our tablet devices though this may be a DroidAR issue).
Single tap a POI marker to display any twitter data obtained from the POI's hashtags if available.
Double tap a POI marker to display the tour-defined information on the POI and mark it as visited, adding the next marker to the world.
The information screen can be removed by tapping it once.
Click the ? button to display help dialogue for using the Augmented Reality interface.
Click the "Next" button to display the tour-defined information on the current POI, mark it as visited and add the next marker to the world.
Click the "Previous" button to remove the current POI marker and set the previous marker to unvisited.
Click the "Show Map" button to display Google Maps with markers overlaid for each POI (blue for visited, green for current, red for unvisited).

## Google Maps

Zoom the map by pressing the + and - buttons.
Zoom to your current location by pressing the gps button above the zoom buttons.
Refresh the display by pressing the refresh button.
Toggle between displaying the route (if available) with the D button.
Display an animation of the route by pressing the A button.
Tap on a marker to display the name of the associated POI.


## Tour XML format
<data>
	<meta>
		<id></id>
			<name></name>
			<info></info>
			<size></size>
	</meta>

	<tour>
		<poi>
			<name></name>
			<latitude></latitude>
			<longitude></longitude>
			<info></info>
			<hashtags></hashtags>
		</poi>
		<poi>
			<name></name>
			<latitude></latitude>
			<longitude></longitude>
			<info></info>
			<hashtags></hashtags>
		</poi>
	</tour>
</data>

### Example:
<data>
	<meta>
		<id>OXF1</id>
		<name>Oxford Science Quarter</name>
		<info>Info for Science Quarter Tour in Oxford</info>
		<size>5</size>
	</meta>
	
	<tour>
	    <poi>
			<name>Wolfson Building</name>
			<latitude>51.759834</latitude>
			<longitude>-1.258468</longitude>
			<info>Department of Computer Science, formerly Computing Laboratory.</info>
			<hashtags>"ComputerScience"</hashtags>
		</poi>
		<poi>
			<name>Tony Hoare Room</name>
			<latitude>51.758855</latitude>
			<longitude>-1.256543</longitude>
			<info>Seminar room named after Sir CAR Hoare, former head of the CS dept and inventor of the quicksort.</info>
			<hashtags>"ComputerScience"</hashtags>
		</poi>
		<poi>
			<name>Natural History Museum</name>
			<latitude>51.758594</latitude>
			<longitude>-1.255974</longitude>
			<info>The Oxford University Museum of Natural History houses the University's scientific collections of zoological, entomological and geological specimens. The Museum itself is a Grade 1 listed building, renowned for its spectacular neo-Gothic architecture. Among its most famous features are the Oxfordshire dinosaurs, the dodo, and the swifts in the tower. (Source: oum.ox.ac.uk)</info>
			<hashtags>morethanadodo</hashtags>
		</poi>
		<poi>
			<name>Pitt Rivers Museum</name>
			<latitude>51.758860</latitude>
			<longitude>-1.255194</longitude>
			<info>Pitt Rivers Museum</info>
			<hashtags></hashtags>
		</poi>
		<poi>
			<name>Robert Hooke Building</name>
			<latitude>51.758964</latitude>
			<longitude>-1.256401</longitude>
			<info>Part of the CS dept, named after scientist and microbiology pioneer Robert Hooke.</info>
			<hashtags>""Hooke"</hashtags>
		</poi>		
	</tour>
</data>