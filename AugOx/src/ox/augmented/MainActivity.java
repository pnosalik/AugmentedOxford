package ox.augmented;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import system.ArActivity;
import system.DefaultARSetup;
import util.Vec;
import worldData.World;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
				
		/*
		Button b = new Button(this);
		b.setText("Start AR");
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CustomARSetup custom = new CustomARSetup(R.raw.tour_aditya);
				custom.context = mainActivity;
				ArActivity.startWithSetup(MainActivity.this, custom); 
				
		*/
		
				
				/*{
					
					@Override
					public void addObjectsTo(GL1Renderer renderer, World world,
							GLFactory objectFactory) {
						/*Local Data for tests
						GeoObj o1 = new GeoObj(51.364023, -1.335228);
						GeoObj o2 = new GeoObj(51.362281, -1.336687);
						GeoObj o3 = new GeoObj(51.362573, -1.331921);
						o1.setComp(objectFactory.newCube());
						o2.setComp(objectFactory.newCube());
						o3.setComp(objectFactory.newCube());
						world.add(o1);
						world.add(o2);
						world.add(o3);
						world.add(objectFactory.newSolarSystem(new Vec(10,0,0)));
					}					
					
				});
			}
		});

	}
	*/

	// method called by Start AR button, loading ArActivity with custom Setup and one of the tours
	public void startAR(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.setTour(R.raw.tour_aditya);
		ArActivity.startWithSetup(MainActivity.this, custom); 
	}
	
	public void startAR2(View view) {
		CustomARSetup custom = new CustomARSetup();
		custom.context = mainActivity;
		custom.setTour(R.raw.tour_tristan);
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
	
		public PlaceholderFragment() {
		
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}	

}
