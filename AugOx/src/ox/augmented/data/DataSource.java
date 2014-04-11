package ox.augmented.data;

import android.graphics.Bitmap;
import ox.augmented.model.Poi;

/**
 * 
 * @author Ben
 * 
 * An interface for DataSources. A DataSource could be
 * facebook, FourSquare, Twitter, maybe even wikipedia,
 * and the role of this interface is to say what methods
 * must be common to each.
 * 
 * Poi anchor - the location for the DataSource, for geolocated data
 * getCurrentImage - Could be the user's icon, or the data source's logo
 * getCurrentText - returns a String that is the current data
 * next - Moves the DataSource - the next tweet, next blurb of info, etc.
 * Could do nothing if its a static source of data
 * 
 */

public interface DataSource {
	public Poi getAnchor();
	public void setAnchor(Poi newAnchor);
	public Bitmap getCurrentImage();
	public String getCurrentText();
	public void next();
	public void previous();
	public String getSearchString();
}
