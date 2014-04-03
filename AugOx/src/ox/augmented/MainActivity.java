package ox.augmented;

import com.example.augox.R;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import system.ArActivity;
import system.DefaultARSetup;
import util.Vec;
import worldData.World;
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
		Button b = new Button(this);
		b.setText("Start AR");
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CustomARSetup custom = new CustomARSetup();
				custom.context = mainActivity;
				ArActivity.startWithSetup(MainActivity.this, custom); /*{

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
					
				});*/
			}
		});
		setContentView(b);

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

	

}
