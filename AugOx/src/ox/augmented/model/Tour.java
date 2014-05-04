package ox.augmented.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		this(Arrays.asList(pois), id, name, info);
	}
	
	/** A no-argument constructor for use with different methods */
	public Tour() { }
	
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
	 * @param fileName
	 */

	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getID(){
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getInfo(){
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public int getSize(){
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void incrementIndex(){
		index++;
	}
	
	public void decrementIndex() {
		index--;
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
	
	/** Returns the pois in a list
	 * 
	 * @return pois
	 */
	public List<Poi> getAllPois(){
		return pois;
	}
	
	public void setPois(List<Poi> pois) {
		this.pois = pois;
	}
	
	public boolean hasNext(){
		return (index<pois.size());
	}
	
}
