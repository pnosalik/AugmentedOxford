package ox.augmented;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import de.rwth.ARActivityPlusMaps;

public class MainActivity extends ActionBarActivity {
	public MainActivity mainActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// view components for this activity are now coded into fragment_main.xml
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	
	// method called by Start AR button, loading ArActivity with custom Setup and one of the tours
	public void startAR(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.setTour(R.raw.tour_aditya);
		ARActivityPlusMaps.startWithSetup(MainActivity.this, custom); 
	}
	
	public void startAR2(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.setTour(R.raw.tour_tristan);
		ARActivityPlusMaps.startWithSetup(MainActivity.this, custom); 
	}

	// method called by Tour Selection button, loading tour selection screen.
	public void tourSelection(View view) {
	Intent intent = new Intent(this, TourListActivity.class);
	startActivity(intent);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 * */
	public static class PlaceholderFragment extends Fragment {
	
		public PlaceholderFragment() {
		
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}	

}
