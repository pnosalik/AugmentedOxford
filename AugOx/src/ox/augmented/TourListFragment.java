package ox.augmented;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import ox.augmented.data.TourCreator;
import ox.augmented.data.TourCreator.TourData;
import ox.augmented.model.Poi;
import ox.augmented.model.Tour;
import util.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A list fragment representing a list of Tours. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link TourDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TourListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};
	
	/** Locally stored list and hash map of available tours. */
	private ArrayList<Tour> tourList;
	private ArrayList<String> tourNameList;
	public static HashMap<String, Tour> tourMap = new HashMap<String,Tour>();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TourListFragment() {
	}
	
	/** Method to populate the local list and hash map of tours. To be called on creation, and on refreshing. */
	public void initialiseTours() {
		// create the list! 
		/* TODO: generate list from XML files. 
		 * This processing need not be done here, in which case 
		 * the list and hashmap of tours should be externally accessible/modifiable.
		 */
		
	    Field[] fields = R.raw.class.getFields();
	    Tour[]  tours  = new Tour[fields.length];
	    int k 	       = 0;
	    
	    for(int i = 0; i < tours.length; i++) { 
	    
	        try {
	        	int resourceID = fields[i].getInt(fields[i]);
	        	String resourceName = fields[i].getName();
	        	String parts[] = resourceName.split("_");
	        	String resName = parts[0];
	        	
	        	System.out.println("*********Resource name: " + resourceName + " Resource ID: " + resourceID);
	        	if(resName.equals("tour") && this.getResources().openRawResource(resourceID) != null)
	        		tours[k++] = TourCreator.parseXml(this.getResources().openRawResource(resourceID));
	    	
			} catch(IllegalAccessException e) {
	            Log.e("REFLECTION", String.format("%s threw IllegalAccessException.",
	                    fields[i].getName()));
	    	} 
		
	    }
	    
		tourList = new ArrayList<Tour>();
		tourNameList = new ArrayList<String>();
	    
		// populate the list and hash map. NOTE: currently uses tour name as key. Change later to id.
		for(int i = 0; i < k; i++) {
			tourList.add(tours[i]);
			tourNameList.add(tours[i].getName());
			tourMap.put(tours[i].getName(), tours[i]);
		}
	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// populate local list. TODO: add progress indicator, optimise.
		initialiseTours();
		
		// Create and set list adapter, using list of tour names. Style as required.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1, android.R.id.text1, tourNameList);
		setListAdapter(adapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(tourList.get(position).getName());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
