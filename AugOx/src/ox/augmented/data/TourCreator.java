package ox.augmented.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ox.augmented.model.Poi;
import ox.augmented.model.Tour;

public class TourCreator {
	public static Tour parseXml(InputStream in_s) {
		Tour t = new Tour();
		
		TourData tourData = new TourData();
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			// InputStream in_s = new FileInputStream(fileName);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);
			System.out.println("Starting to parse");
			tourData = parseXML(parser);

		} catch (XmlPullParserException e) {
			System.out.println("XmlPullParserException in line "+ e.getLineNumber());
			tourData = new TourData(null, "", "Invalid Tour", "XmlPullParserEception in line "+e.getLineNumber());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException");
			tourData = new TourData(null, "", "Invalid Tour", "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			tourData = new TourData(null, "", "Invalid Tour", "IOException");
			e.printStackTrace();
		}
		if (tourData!=null){
			t.setPois(tourData.pois);
			t.setId(tourData.id);
			t.setName(tourData.name);
			t.setInfo(tourData.info);
			if (tourData.size<0 || tourData.size!=t.getAllPois().size()) System.out.println("Wrong size field in meta tourData.size = "+tourData.size+"this.pois.size() = "+ t.getAllPois().size());
			t.setSize(t.getAllPois().size());
		}
		else System.out.println("Wasn't able to instantiate Tour");
		
		return t;
	}
	
	private static TourData parseXML(XmlPullParser parser) throws XmlPullParserException, IOException { 
			  TourData tourData = null; 
			  int eventType = parser.getEventType();
			  boolean inMeta = false;
			  PoiData currentPoiData = new PoiData();
				 
			  while (eventType!=XmlPullParser.END_DOCUMENT){
				 String name = null;
				 switch (eventType){
				 	case XmlPullParser.START_DOCUMENT:
				 		tourData = new TourData();
				 		break;
				 	case XmlPullParser.START_TAG:
				 		name = parser.getName();
				 		//switch between types of tags
				 		switch(name){
				 			case "meta":
				 				inMeta = true;
				 				break;
				 			case "id":
				 				if (inMeta) {
				 					tourData.id = parser.nextText();
				 					/*String idData = parser.nextText();
				 					try {
				 						tourData.id = Integer.parseInt(idData);
				 						}
				 						catch (NumberFormatException e){
				 							throw new XmlPullParserException("Invalid String in id field");
				 						}*/
				 				}
				 				/*else	{ //Currently in a POI
				 					String data = parser.nextText();
				 					try {
				 						currentPoiData.id = Integer.parseInt(data);
				 						}
				 						catch (NumberFormatException e){
				 							throw new XmlPullParserException("Invalid String in id field");
				 						}
				 				}*/
				 				break;
				 			case "name":
				 				if (inMeta) {
				 					tourData.name = parser.nextText();
				 				}
				 				else {
				 					currentPoiData.name = parser.nextText();
				 				}
				 				break;
				 			case "info":
				 				if (inMeta) {
				 					tourData.info = parser.nextText();
				 				}
				 				else {
				 					currentPoiData.info = parser.nextText();
				 				}
				 				break;
				 			case "size":
				 				String sizeData = parser.nextText();
			 					try {
			 						tourData.size = Integer.parseInt(sizeData);
			 						}
			 						catch (NumberFormatException e){
			 							throw new XmlPullParserException("Invalid String in size field");
			 						}
			 					break;
				 			case "latitude":
				 				String latData  = parser.nextText();
				 				try {
				 					currentPoiData.latitude = Double.parseDouble(latData);
				 				}
				 				catch (NumberFormatException e){
				 					throw new XmlPullParserException("Invalid String in latitude field");
				 				}
				 				break;
				 			case "longitude":
				 				String longData  = parser.nextText();
				 				try {
				 					currentPoiData.longitude = Double.parseDouble(longData);
				 				}
				 				catch (NumberFormatException e){
				 					throw new XmlPullParserException("Invalid String in longitude field");
				 				}
				 				break;
				 			case "hashtags":
				 				/*if (inMeta){
				 					tourData.hashtags = parser.nextText();
				 				}
				 				else {*/
				 					currentPoiData.hashtags = parser.nextText();
				 				//}
				 				break;
				 			case "tour":
				 				inMeta=false;
				 				break;
				 			case "poi":
				 				currentPoiData = new PoiData();
				 				break;
				 		}
				 		break;
				 	case XmlPullParser.END_TAG:
				 		name = parser.getName();
				 		switch(name){
				 			case "poi":
				 				//Will add the poi only if the data is full
				 					Poi newPoi = currentPoiData.createPoi();
				 					if (newPoi!=null) tourData.pois.add(newPoi);
				 				break;
				 			case "tour":
				 				
				 				break;
				 			case "meta":
				 					inMeta = false;
				 				break;
				 		}
				 }
				 eventType = parser.next();
			  } // end of while
			  
			  return tourData;
			} 
	
	//Used to store the data while parsing
	public static class TourData {
		public List<Poi> pois = new ArrayList<Poi>();
		public String id = "";
		public String name = null;
		public String info = null;
		public int size = -1;
		
		public TourData(){	
		}
		
		public TourData(List<Poi> pois, String id, String name, String info){
			this.pois =pois;
			this.id = id;
			this.name = name;
			this.info = info;
		}
	}
	
	//Used to store data while parsing
	public static class PoiData{
		public String name = null;
		public double latitude = 100.0;
		public double longitude = 100.0;
		public String info = null;
		public String hashtags= null;
		
		public PoiData(){
		}
		
		//Returns null if not all fields have been initialized
		public Poi createPoi(){
			if (name!=null && latitude!=100.0 && longitude!=100.0 && info!=null && hashtags!=null) {
				if (hashtags.equalsIgnoreCase("")) {
					System.out.println("Creating POI without TwitterSource: Poi("+name+", "+latitude+", "+ longitude+", "+ ","+info+")");
					return new Poi(name, latitude,longitude,info);
				}
				else {
					System.out.println("Creating POI with TwitterSource: Poi("+name+", "+latitude+", "+ longitude+", "+info+", new TwitterSource("+hashtags+"))");
					return new Poi(name, latitude,longitude,info,new TwitterSource(hashtags));
				}
			}
			else {
				if (name==null) System.out.println("Not able to create POI - name = null");
				if (latitude==100.0) System.out.println("Not able to create POI - latitude = 100.0");
				if (longitude==100.0) System.out.println("Not able to create POI - longitude = 100.0");
				if (info==null) System.out.println("Not able to create POI - info = null");
				if (hashtags==null) System.out.println("Not able to create POI - hashtags = null");
				return null;
			}
			
		}
	}
	/** Returns a preview of the tour in the form of a Tour object with the right size, info, name, id and a list of POIs containing only the starting point of the tour
	 *  Requires an xml file. For the format take a look at the Tour(InputStream in_s). Will return null if there was a problem with the parsing.
	 * 
	 * @param in_s
	 * @return
	 */
	public static Tour getTourPreview(InputStream in_s){
		TourData tourData = null;
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    //InputStream in_s = new FileInputStream(fileName);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            tourData = parseTourXMLMeta(parser);

		} catch (XmlPullParserException e) {
			tourData = null;
			System.out.println("Invalid Tour file - XmlPullParserException in line "+e.getLineNumber());
			//tourData = new TourData(null, -1, "Invalid Tour", "XmlPullParserEception in line "+e.getLineNumber());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			tourData = null;
			System.out.println("Invalid Tour file - FileNotFoundException");
			//tourData = new TourData(null, -1, "Invalid Tour", "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			tourData = null;
			System.out.println("Invalid Tour file - IOException");
			//tourData = new TourData(null, -1, "Invalid Tour", "FileNotFoundException");
			e.printStackTrace();
		}
		if (tourData!=null){
			return new Tour(tourData.pois, tourData.id, tourData.name, tourData.info, tourData.size);	
		}
		else System.out.println("Wasn't able to instantiate Tour Preview");
		
		return null;
	}

	private static TourData parseTourXMLMeta(XmlPullParser parser) throws XmlPullParserException, IOException {
		TourData tourData = null; 
		  int eventType = parser.getEventType();
		  boolean readFirstPoi = false; //will be set to true after the first successfully read POI - when reaching a </poi> where all the data for the POI has been filled.
		  boolean inMeta = false;
		  PoiData currentPoiData = new PoiData();
		  
		  while (eventType!=XmlPullParser.END_DOCUMENT && !readFirstPoi){
			 String name = null;
			 switch (eventType){
			 	case XmlPullParser.START_DOCUMENT:
			 		tourData = new TourData();
			 		break;
			 	case XmlPullParser.START_TAG:
			 		name = parser.getName();
			 		//switch between types of tags
			 		switch(name){
			 			case "meta":
			 				inMeta = true;
			 				break;
			 			case "id":
			 				if (inMeta) {
			 					tourData.id = parser.nextText();
			 					/*String idData = parser.nextText();
			 					try {
			 						tourData.id = Integer.parseInt(idData);
			 						}
			 						catch (NumberFormatException e){
			 							throw new XmlPullParserException("Invalid String in id field");
			 						}*/
			 				}
			 				/*else	{ //Currently in a POI
			 					String data = parser.nextText();
			 					try {
			 						currentPoiData.id = Integer.parseInt(data);
			 						}
			 						catch (NumberFormatException e){
			 							throw new XmlPullParserException("Invalid String in id field");
			 						}
			 				}*/
			 				break;
			 			case "name":
			 				if (inMeta) {
			 					tourData.name = parser.nextText();
			 				}
			 				else {
			 					currentPoiData.name = parser.nextText();
			 				}
			 				break;
			 			case "info":
			 				if (inMeta) {
			 					tourData.info = parser.nextText();
			 				}
			 				else {
			 					currentPoiData.info = parser.nextText();
			 				}
			 				break;
			 			case "size":
			 				String sizeData = parser.nextText();
		 					try {
		 						tourData.size = Integer.parseInt(sizeData);
		 						}
		 						catch (NumberFormatException e){
		 							throw new XmlPullParserException("Invalid String in size field");
		 						}
		 					break;
			 			case "latitude":
			 				String latData  = parser.nextText();
			 				try {
			 					currentPoiData.latitude = Double.parseDouble(latData);
			 				}
			 				catch (NumberFormatException e){
			 					throw new XmlPullParserException("Invalid String in latitude field");
			 				}
			 				break;
			 			case "longitude":
			 				String longData  = parser.nextText();
			 				try {
			 					currentPoiData.longitude = Double.parseDouble(longData);
			 				}
			 				catch (NumberFormatException e){
			 					throw new XmlPullParserException("Invalid String in longitude field");
			 				}
			 				break;
			 			case "hashtags":
			 				/*if (inMeta){
			 					tourData.hashtags = parser.nextText();
			 				}
			 				else {*/
			 					currentPoiData.hashtags = parser.nextText();
			 				//}
			 				break;
			 			case "tour":
			 				inMeta=false;
			 				break;
			 			case "poi":
			 				currentPoiData = new PoiData();
			 				break;
			 		}
			 		break;
			 	case XmlPullParser.END_TAG:
			 		name = parser.getName();
			 		switch(name){
			 			case "poi":
			 				//Will add the poi only if the data is full
			 				if (currentPoiData!=null){
			 					Poi newPoi = currentPoiData.createPoi();
			 					if (newPoi!=null) {
			 						tourData.pois.add(newPoi);
			 						readFirstPoi = true;
			 					}
			 				}
			 				break;
			 			case "tour":
			 				
			 				break;
			 			case "meta":
			 				inMeta=false;
			 				break;
			 		}
			 }
			 eventType = parser.next();
		  } // end of while
		  
		  return tourData;
	}
}
