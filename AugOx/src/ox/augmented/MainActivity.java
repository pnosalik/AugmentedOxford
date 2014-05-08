package ox.augmented;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import system.ArActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {
	public MainActivity mainActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// view components for this activity are now coded into fragment_main.xml
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment(), "FragmentContainingMap").commit();
		}
		
	}
	
	// method called by Start AR button, loading ArActivity with custom Setup and one of the tours
	public void startAR(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.mapView = (MapView) getSupportFragmentManager().findFragmentByTag("FragmentContainingMap").getView().findViewById(R.id.map);
		((ViewGroup) custom.mapView.getParent()).removeView(custom.mapView);
		custom.setTour(R.raw.tour_aditya);
		ArActivity.startWithSetup(MainActivity.this, custom); 
	}
	
	public void startAR2(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.mapView = (MapView) getSupportFragmentManager().findFragmentByTag("FragmentContainingMap").getView().findViewById(R.id.map);
		((ViewGroup) custom.mapView.getParent()).removeView(custom.mapView);
		custom.setTour(R.raw.tour_oxford_1);
		ArActivity.startWithSetup(MainActivity.this, custom); 
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
		private GoogleMap mMap;
		private MapView mMapView;
		
		public PlaceholderFragment() {
		
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			 mMapView = (MapView) rootView.findViewById(R.id.map);
		     mMapView.onCreate(savedInstanceState);

		     //setUpMapIfNeeded();
			
			return rootView;
		}
		
		public void onResume() {
	        super.onResume();
	        mMapView.onResume();

	        setUpMapIfNeeded();
	    }

	    private void setUpMapIfNeeded() {
	        if (mMap == null) {
	        	if (getActivity()==null) Log.d("NullPointerIn setUpMapIfNeeded","getActivity()==null");
	        	if (getActivity().findViewById(R.id.map)==null) Log.d("NullPointerIn setUpMapIfNeeded","getActivity().findViewById(R.id.map)==null");    	
	            mMap = ((MapView) getActivity().findViewById(R.id.map)).getMap();
	            mMap = ((MapView) getActivity().findViewById(R.id.map)).getMap();
	            if (mMap != null) {
	                setUpMap();
	            }
	        }
	    }

	    private void setUpMap() {
	        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	    }

	    @Override
		public void onPause() {
	        mMapView.onPause();
	        super.onPause();
	    }

	    @Override
		public void onDestroy() {
	        mMapView.onDestroy();
	        super.onDestroy();
	    }

	    @Override
	    public void onLowMemory() {
	        super.onLowMemory();
	        mMapView.onLowMemory();
	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        mMapView.onSaveInstanceState(outState);
	    }

}
}
