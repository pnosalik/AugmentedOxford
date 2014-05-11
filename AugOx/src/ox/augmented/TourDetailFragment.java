package ox.augmented;

import java.util.List;

import ox.augmented.model.Poi;
import ox.augmented.model.Tour;
import system.ArActivity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A fragment representing a single Tour detail screen. This fragment is either
 * contained in a {@link TourListActivity} in two-pane mode (on tablets) or a
 * {@link TourDetailActivity} on handsets.
 */
public class TourDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The tour this fragment is presenting.
	 */
	private Tour mItem;
	
	private Activity currentActivity;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TourDetailFragment() {
	}
	
	public void setCurrentActivity(Activity activity) {
		this.currentActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the content specified by the tour, accessed from the hash map. 
			mItem = TourListFragment.tourMap.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tour_detail,
				container, false);

		/* Show each tour's details when it is selected.
		 * These are passed to the child Views within rootView.
		 * These are declared in fragment_tour_detail.xml
		 */
		if (mItem != null) {
			
			// text information
			((TextView) rootView.findViewById(R.id.tour_detail_name))
					.setText(mItem.getName());
			((TextView) rootView.findViewById(R.id.tour_detail_info))
					.setText(mItem.getInfo());
			((TextView) rootView.findViewById(R.id.tour_detail_heading_pois))
				.setText("Points of interest on this tour (" + mItem.getSize() + " total):");
			((TextView) rootView.findViewById(R.id.tour_detail_pois))
				.setText(listToText(mItem.getAllPois()));
			
			// button to start tour
			((Button) rootView.findViewById(R.id.tour_detail_button_start))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomARSetup custom = new CustomARSetup();
						custom.context = currentActivity;
						//custom.mapView = (MapView) getSupportFragmentManager().findFragmentByTag("FragmentContainingMap").getView().findViewById(R.id.map);
						//((ViewGroup) custom.mapView.getParent()).removeView(custom.mapView);
						custom.setTour(mItem);
						ArActivity.startWithSetup(currentActivity, custom); 
						/*
						CustomARSetup custom = new CustomARSetup();
						custom.context = currentActivity;
						custom.setTour(mItem);
						ARActivityPlusMaps.startWithSetup(currentActivity, custom); //changed to include map 
						*/
					}
				});
			
		}
		return rootView;
	}
	/* Converts list of Pois into String containing numbered Poi names. */			
	private String listToText(List<Poi> pois) {
		String result = "";
		int count = 0;
		for(Poi p : pois) {
			result+=(count +". " + p.getName() + "\n");
			count++;
		}
		return result;
	}
}
