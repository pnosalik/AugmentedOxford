package ox.augmented;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * An activity representing a list of Tours. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link TourDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TourListFragment} and the item details (if present) is a
 * {@link TourDetailFragment}.
 * <p>
 * This activity also implements the required {@link TourListFragment.Callbacks}
 * interface to listen for item selections.
 * 
 */
public class TourListActivity extends FragmentActivity implements
		TourListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour_list);
		// Changed: DON'T show the Up button in the action bar, as this is now the main activity.
		getActionBar().setDisplayHomeAsUpEnabled(false);

		if (findViewById(R.id.tour_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((TourListFragment) getSupportFragmentManager().findFragmentById(
					R.id.tour_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.tour_list_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		/* Can insert code here to handle the refresh button,
		 * but avoid if this processing is done in TourListFragment's method,
		 * as this method overrides that of the fragments called
		 * 
		if (id == R.id.action_refresh) {
			// refresh 
			Toast.makeText(this, "Refresh selected from activity", Toast.LENGTH_SHORT)
	          .show();
			
			//copied code to load fragment
			
			if (findViewById(R.id.tour_detail_container) != null) {
				// The detail container view will be present only in the
				// large-screen layouts (res/values-large and
				// res/values-sw600dp). If this view is present, then the
				// activity should be in two-pane mode.
				mTwoPane = true;

				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				((TourListFragment) getSupportFragmentManager().findFragmentById(
						R.id.tour_list)).setActivateOnItemClick(true);
			}
			
			return true;
		}
		*/
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link TourListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(TourDetailFragment.ARG_ITEM_ID, id);
			TourDetailFragment fragment = new TourDetailFragment();
			//include bundle
			fragment.setArguments(arguments);
			//pass on identity of caller activity so that AR mode can be run from the fragment
			fragment.setCurrentActivity(this);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.tour_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, TourDetailActivity.class);
			detailIntent.putExtra(TourDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
