package ox.augmented;

import geo.GeoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ox.augmented.data.TourCreator;
import ox.augmented.model.Poi;
import ox.augmented.model.Tour;
import util.Log;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
	
	/** The current activity */
	private Activity currentActivity;

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
	
	private String sortOrder = "Filename"; //activates default case
	
	private OnNavigationListener mOnNavigationListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TourListFragment() {
	}
	
	/** Method to populate the local list and hash map of tours. To be called on creation, and on refreshing. */
	public void initialiseTours() {
		// create the list! 
		/* This processing need not be done here, in which case 
		 * the list and hashmap of tours should be externally accessible/modifiable.
		 */
		
	    Field[] fields = R.raw.class.getFields();
	    Tour[]  tours  = new Tour[fields.length];
	    int k 	       = 0;
	    
	    for(int i = 0; i < tours.length; i++) { 
	    
	        try {
	        	int resourceID = fields[i].getInt(fields[i]);
	        	String resourceName = fields[i].getName();
	        	// current protocol: tour filenames begin with tour_
	        	String parts[] = resourceName.split("_");
	            if(parts[0].equals("tour"))
	       		  tours[k++] = TourCreator.parseXml(this.getResources().openRawResource(resourceID));
	    	
			} catch(IllegalAccessException e) {
	            Log.e("REFLECTION", String.format("%s threw IllegalAccessException.",
	                    fields[i].getName()));
	    	} 
		
	    }
	    
		tourList = new ArrayList<Tour>();
		//tourNameList = new ArrayList<String>();
	    
		// populate the list and hash map. NOTE: currently uses tour name as key. Change later to id.
		for(int i = 0; i < k; i++) {
			tourList.add(tours[i]);
			//tourNameList.add(tours[i].getName());
			tourMap.put(tours[i].getName(), tours[i]);
		}
		sortTours(); // must come BEFORE tourNameList is initialised
		
		initialiseTourNameList();
		
	
	}
	
	/* Initialise tourNameList based on the values in tourList in the same order.
	 * Required when refreshing and changing the sort criterion.
	 */
	private void initialiseTourNameList() {
		tourNameList = new ArrayList<String>();
		for(int i = 0; i < tourList.size(); i++) {
			tourNameList.add(tourList.get(i).getName());
		}
	}
	
	/* Method for sorting the list of Tours according to user preferences. */
	public void sortTours() { 
		/* TODO: Black magic for initializing the String "sortOrder" with the user's choice for a sorting criterion. */
		String criteria[] = getResources().getStringArray(R.array.sorting_criteria_list);
		//default, alphabetical, proximity
		if(sortOrder.equals(criteria[1]))
		{
			AlphaComparator alphaComp = new AlphaComparator();
			Collections.sort(tourList, alphaComp);
		}
		else 
		{
			if(sortOrder.equals(criteria[2]))
			{
				ProxComparator proxComp = new ProxComparator();
				Collections.sort(tourList, proxComp);
			}
		}
	}
	
	// Different comparator classes for sorting the list of tours according to user preferences .
	private class AlphaComparator implements Comparator<Tour> {

		@Override
		public int compare(Tour a, Tour b) { return a.getName().compareTo(b.getName()); }
		
	}
	
	private class ProxComparator implements Comparator<Tour> {

		@Override
		public int compare(Tour a, Tour b) {
			
			// Setting the next POIs for the Tour "a" and the Tour "b"
			Poi aPOI = a.getNextPoi();
			Location aNextLocation = new Location("aNextLocation");
			aNextLocation.setLatitude(aPOI.getLatitude());
			aNextLocation.setLongitude(aPOI.getLongitude());
			
			Poi bPOI = b.getNextPoi();
			Location bNextLocation = new Location("bNextLocation");
			bNextLocation.setLatitude(bPOI.getLatitude());
			bNextLocation.setLongitude(bPOI.getLongitude());
			
			// Calculating the distance to the closest POI for each of the tours.
			Location l = GeoUtils.getCurrentLocation(getActivity());
			
			Float aDistance = l.distanceTo(aNextLocation);
			Float bDistance = l.distanceTo(bNextLocation);
			
			if (aDistance > bDistance) 
				return 1;
			else 
				return -1;
			
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add action bar menu 
		setHasOptionsMenu(true);
		
		// create dropdown for sorting criterion
		createSortingDropdown();
	    
		// populate local list
		initialiseTours();
		
		// create and set list adapter
		resetAdapter();
	}
	
	/* Create the spinner dropdown to choose the sorting criterion for listed tours. */
	private void createSortingDropdown() {
		// create spinner adapter
		ArrayAdapter<CharSequence> mSpinnerAdapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.sorting_criteria_list, R.layout.custom_spinner_dropdown_item); //or android.R.layout.simple_spinner..
		mSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
		
		// create navigation listener
		mOnNavigationListener = new OnNavigationListener() {
			  // Get the same strings provided for the drop-down's ArrayAdapter
			  String[] strings = getResources().getStringArray(R.array.sorting_criteria_list);

			  // action when selected.
			  @Override
			  public boolean onNavigationItemSelected(int position, long itemId) {
				  // change sorting criterion and refresh as required
				  /*
				  switch (strings[position]) {
				  case "Alphabetical":
					  sortOrder = strings[position];
					  refresh();
					  break;
				  case "Proximity":
					  sortOrder = strings[position];
					  refresh();
					  break;
				  default: 
					  sortOrder = "Filename";
					  refresh();
					  break;
				  }
				  */
				  sortOrder = strings[position];
				  refresh();
				  Toast.makeText(getActivity(), "Tour sort order: " + strings[position], Toast.LENGTH_SHORT).show();
				  return true;
			  }
		};
		
		// assign to action bar
		ActionBar actionBar = getActivity().getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
	
	}
	
	/* Reset the list adapter used for the UI. Uses tourNameList. */
	private void resetAdapter() {
		// Create and set list adapter, using list of tour names. Style as required.
		/*
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActivity(),
				R.layout.tour_list_item_activated, 
				R.id.tour_list_item_text, tourNameList);
		*/
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1, 
				android.R.id.text1, tourNameList); 
		setListAdapter(adapter);
	}
	
	/* Refresh the list UI. */
	private void refresh() {
		initialiseTours();
		resetAdapter();
	}
	
	/* Pop-up class */
	public class PopUp extends Activity implements OnClickListener {
		
		LinearLayout layoutOfPopup;
		PopupWindow popupMessage;
		Button popupButton, insidePopupButton;
		TextView popupText;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			init();
			popupInit();
		}
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.action_help) {
				popupMessage.showAsDropDown(popupButton, 0, 0);
			} else {
				popupMessage.dismiss();
			}
		}
		
		public void init() {
			popupButton = (Button) findViewById(R.id.action_help);
			popupText = new TextView(this);
			insidePopupButton = new Button(this);
			layoutOfPopup = new LinearLayout(this);
			insidePopupButton.setText("Ok");
			popupText.setText("This is a Popup Window");
			popupText.setPadding(0, 0, 0, 20);
			layoutOfPopup.setOrientation(1);
			layoutOfPopup.addView(popupText);
			layoutOfPopup.addView(insidePopupButton);
		}
		
		public void popupInit() {
			popupButton.setOnClickListener(this);
			insidePopupButton.setOnClickListener(this);
			popupMessage = new PopupWindow(layoutOfPopup, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			popupMessage.setContentView(layoutOfPopup);
		}
		
	}
	
	/* Display Help pop-up screen */
	private void help() {
//		PopUp popUp = new PopUp();
		LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 

		PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.main, null, false),100,100, true);

		pw.showAtLocation(this.getActivity().findViewById(R.id.action_help), Gravity.CENTER, 0, 0);
	}
	
	/* Inflate action bar menu items. */
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // Inflate the menu items for use in the action bar
	    inflater.inflate(R.menu.tour_list_activity_actions, menu);
	}

	/* Define behaviour for action bar menu items. */
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		 
		switch (id) {

		case R.id.action_refresh:
			refresh();
			// display message
			Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_help:
			help();
			// display message
			Toast.makeText(getActivity(), "Help", Toast.LENGTH_SHORT).show();
			break;
		}
	/*	}
	        R.id.action_refresh) {
			refresh();
			// display message
			Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
		}
		if(id == R.id.action_help) {
			help();
			// display message
			Toast.makeText(getActivity(), "Help", Toast.LENGTH_SHORT).show();
		}
    */
		return getActivity().onOptionsItemSelected(item);
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
