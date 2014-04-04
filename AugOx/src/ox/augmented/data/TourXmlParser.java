package ox.augmented.data;

import java.io.File;

import ox.augmented.Tour;

public class TourXmlParser {
	public static Tour parseFile(File file) {
		/** Ignore if the input isn't an actual file (maybe throw an exception?) */
		if(!file.isFile())
			return null;
		//TODO: Read the XML file into a tour
		return null;
	}
}
