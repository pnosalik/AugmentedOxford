/**
 * 
 */
package ox.augmented.data;

import java.util.List;

import ox.augmented.Poi;

/**
 * @author Piotr Nosalik
 * 
 * A generalized interface for pulling POIs from various sources
 *
 */
public interface PoiSource {
	
	/**
	 * @return The list of all POIs currently available
	 */
	public List<Poi> loadAll();
	
	/**
	 * @return The next available POI, or <b>null</b> if there aren't any
	 */
	public Poi loadNext();
}
