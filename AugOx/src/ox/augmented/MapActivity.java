package ox.augmented;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	private double[] lats;
	private double[] longs;
	private String[] names;
	private int current;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Intent intent = getIntent();
		lats = intent.getDoubleArrayExtra("LATS");
		longs = intent.getDoubleArrayExtra("LONGS");
		names = intent.getStringArrayExtra("NAMES");
		current = intent.getIntExtra("CURRENT", 0);		
		mapSetup();

	}
	
	private void mapSetup() {
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		for(int i = 0; i < names.length; i++){
			Float hue = i < current ? BitmapDescriptorFactory.HUE_BLUE :
				i > current ? BitmapDescriptorFactory.HUE_MAGENTA : 
					BitmapDescriptorFactory.HUE_GREEN;
			map.addMarker(new MarkerOptions()
	        	.position(new LatLng(lats[i], longs[i]))
	        	.title(names[i])
	        	.icon(BitmapDescriptorFactory.defaultMarker(hue)));
			
		}
	
		LatLng l = new LatLng(lats[current], longs[current]);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(l,16));
	}

	

}
