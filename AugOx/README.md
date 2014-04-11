# AugmentedOxford

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