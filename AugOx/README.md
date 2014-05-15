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
		<id>1</id>
		<name>Example Tour</name>
		<info>Some info</info>
		<size>2</size>
	</meta>

	<tour>
		<poi>
			<name>Room</name>
			<latitude>51.363075</latitude>
			<longitude>-1.333717</longitude>
			<info>Some Test Data \n\n\n\n Some More Lines\n\n\n\n\n\n\n\n\n\n\n Test \n\n\n\n\n A really really really really really really really really really really really really really really really really really long sentence</info>
			<hashtags>"Tristan's Room"</hashtags>
		</poi>

		<poi>
			<name>School</name>
			<latitude>51.364023</latitude>
			<longitude>-1.335228</longitude>
			<info>My old school</info>
			<hashtags></hashtags>//Will make that POI without a TwitterSource
		</poi>
	</tour>	
</data>