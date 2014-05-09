package ox.augmented;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import app.akexorcist.gdaplibrary.GoogleDirection;
import app.akexorcist.gdaplibrary.GoogleDirection.OnDirectionResponseListener;
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
	private GoogleDirection gd;
	Document mDoc;
	
	private LatLng myPosition = new LatLng(51.757465, -1.245925);

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
		
		gd = new GoogleDirection(this);
		gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
			public void onResponse(String status, Document doc, GoogleDirection gd) {
				mDoc = doc;
				map.addPolyline(gd.getPolyline(doc, 3, Color.RED));	
		        
			}
		});
		mapSetup();
		drawRouteOnMap();
		
		

	}
	
	private void mapSetup() {
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		poisSetup();
		LatLng l = new LatLng(lats[current], longs[current]);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(l,16));
	}
	
	private void poisSetup(){
		for(int i = 0; i < names.length; i++){
			Float hue = i < current ? BitmapDescriptorFactory.HUE_BLUE :
				i > current ? BitmapDescriptorFactory.HUE_MAGENTA : 
					BitmapDescriptorFactory.HUE_GREEN;
			map.addMarker(new MarkerOptions()
	        	.position(new LatLng(lats[i], longs[i]))
	        	.title(names[i])
	        	.icon(BitmapDescriptorFactory.defaultMarker(hue)));
			
		}
	}
	
	private void drawRouteOnMap(){
		gd.setLogging(true);
		LatLng destPosition = new LatLng(lats[current], longs[current]);
		gd.request(myPosition, destPosition, GoogleDirection.MODE_WALKING);
	}
	

}
