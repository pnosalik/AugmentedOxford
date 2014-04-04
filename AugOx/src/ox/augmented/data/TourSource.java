package ox.augmented.data;

import java.util.List;

import ox.augmented.Tour;

/**
 * 
 * @author Piotr Nosalik
 * 
 * A generalized interface for pulling tours from various sources
 *
 */
public interface TourSource {
	/**
	 * @return The list of all tours currently available
	 */
	public List<Tour> loadAll();
	
	/**
	 * @return The next available tour, or <b>null</b> if there aren't any
	 */
	public Tour loadNext();
}
