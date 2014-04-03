package ox.augmented;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ox.augmented.data.TwitterSource;

public class Tour {
	private List<Poi> pois = new ArrayList<Poi>();//All the POIs in this tour
	private int index = 0;//Current position on the tour
	private String id;//Need to implement an ID generating scheme that would work between resets and also with multiple users
	private String name;
	private String info;
	private int size; //Useful when parsing only the metadata
	
	/** Used only for the preview as there will be difference between the size field and pois.size.
	 * 
	 * @param pois
	 * @param id
	 * @param name
	 * @param info
	 * @param size
	 */
	public Tour(List<Poi> pois, String id, String name, String info, int size){
		this.pois = pois;
		this.id = id;
		this.name = name;
		this.info = info;
		this.size = size;
	}
	
	/**Creates a tour with the following parameters. If the array is null or empty creates it so that it has a single POI("Empty Tour", 0.0, 0.0, "Empty Tour")
	 * 
	 * @param pois
	 * @param id
	 * @param name
	 * @param info
	 */
	//If the array is empty it creates a tour with a single POI
	//Could be useful for debugging
	public Tour(Poi[] pois, String id, String name, String info){
		this.id = id;
		this.name = name;
		this.info = info;
		if (pois!=null && pois.length!=0){
			this.pois = Arrays.asList(pois);
		}
		else {
			this.pois.add(new Poi("Empty Tour", 0.0, 0.0, "Empty Tour"));
		}
		this.size = this.pois.size();
		//System.out.println("Tour "+id+" of size "+size+" created");
	}
	
	/**Creates a tour with the following parameters. If the ArrayList is null or empty creates it so that it has a single POI("Empty Tour", 0.0, 0.0, "Empty Tour")
	 * 
	 * @param pois
	 * @param id
	 * @param name
	 * @param info
	 */
	//Constructor for ArrayList
	public Tour(List<Poi> pois, String id, String name, String info){
		this.id = id;
		this.name = name;
		this.info = info;
		if (pois!=null && pois.size()!=0){
			this.pois = pois;
		}
		else {
			this.pois.add(new Poi("Empty Tour", 0.0, 0.0, "Empty Tour"));
		}
		this.size = this.pois.size();
		System.out.println("Tour "+id+" of size "+this.pois.size()+" created");
	}
	
	/**Creates a tour using an InputStream from an xml file.
	 * Format of the xml:
	 * <data>
	 * <meta>
	 * 		<id></id>
	 * 		<name></name>
	 * 		<info></info>
	 * 		<size></size>
	 * </meta>
	 * 
	 * <tour>
	 * 		//Create multiple POIs in the right sequence
	 * 		<poi>
	 * 			<name></name>
	 * 			<latitude></latitude>
	 * 			<longitude></longitude>
	 * 			<info></info>
	 * 			<hashtags></hashtags>
	 * 		</poi>
	 * </tour>
	 * </data>
	 * 
	 * Example:
	 * <data>
	 *  <meta>
	 *		<id>1</id>
	 *		<name>Example Tour</name>
	 *		<info>Some info</info>
	 *		<size>2</size>
	 *		</meta>
	 *
	 *	<tour>
	 *		<poi>
	 *			<name>Room</name>
	 *			<latitude>51.363075</latitude>
	 *			<longitude>-1.333717</longitude>
	 *			<info>Some Test Data \n\n\n\n Some More Lines\n\n\n\n\n\n\n\n\n\n\n Test \n\n\n\n\n A really really really really really really really really really really really really really really really really really long sentence</info>
	 *			<hashtags>"Tristan's Room"</hashtags>
	 *		</poi>
	 *
	 *		<poi>
	 *			<name>School</name>
	 *			<latitude>51.364023</latitude>
	 *			<longitude>-1.335228</longitude>
	 *			<info>My old school</info>
	 *			<hashtags></hashtags>//Will make that POI without a TwitterSource
	 *		</poi>
	 *	</tour>	
	 * </data>
	 * @param fileName
	 */
	public Tour(InputStream in_s){
		TourData tourData = new TourData();
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    //InputStream in_s = new FileInputStream(fileName);
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
			this.pois = tourData.pois;
			this.id = tourData.id;
			this.name = tourData.name;
			this.info = tourData.info;
			if (tourData.size<0 || tourData.size!=this.pois.size()) System.out.println("Wrong size field in meta tourData.size = "+tourData.size+"this.pois.size() = "+ this.pois.size());
			this.size=this.pois.size();
		}
		else System.out.println("Wasn't able to instantiate Tour");
	}
	
	private TourData parseXML(XmlPullParser parser) throws XmlPullParserException, IOException { 
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

	public String getName(){
		return name;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getID(){
		return id;
	}
	
	public String getInfo(){
		return info;
	}
	
	public int getSize(){
		return size;
	}
	
	public void incrementIndex(){
		index++;
	}
	
	/**Returns the POI at that index. If the index is invalid will return the first POI of the tour.
	 * 
	 * @param ind
	 * @return POI at that index
	 */
	//Returns the first POI if supplied an invalid index
	public Poi getPoi(int ind){
		try {
			return pois.get(ind);
		}
		catch(ArrayIndexOutOfBoundsException e){
			return pois.get(0);
		}
	}
	
	/**Returns the POI at the current index
	 * 
	 * @return
	 */
	public Poi getCurrentPoi(){
		return pois.get(index);
	}
	
	/**Moves to the next POI, incrementing the index (mod sizeOfTheTour)
	 * 
	 * @return the next POI (mod the size of the tour)
	 */
	//Increments index (mod pois.length) and returns the POI with that index
	public Poi moveToNextPoi(){
		index = (index+1)%size;
		if (index==0) System.out.println("Tour finished");
		return pois.get(index);
	}
	
	/** Returns the POI in the next position(mod size). Doesn't increment the index. 
	 * Will print out "No next POI" if it has wrapped around.
	 * 
	 * @return the POI in the next position
	 */
	public Poi getNextPoi(){
		int nextIndex = (index+1)%pois.size();
		if (nextIndex==0) System.out.println("No next POI.");
		return pois.get(nextIndex);
	}
	
	
	/** Returns the pois converted into an Array
	 *	NOT WORKING = throws a ClassCastException 
	 * @return pois as a Poi[]
	 */
	public Poi[] getAllPoisAsArray(){
		return (Poi[]) pois.toArray();
	}
	
	/** Returns the pois as an ArrayList<Poi>
	 * 
	 * @return pois
	 */
	public List<Poi> getAllPois(){
		return pois;
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
	
	public boolean hasNext(){
		return (index+1<pois.size());
	}
	
}
