package ox.augmented;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import app.akexorcist.gdaplibrary.GoogleDirection;
import app.akexorcist.gdaplibrary.GoogleDirection.OnDirectionResponseListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity 
		implements
			ConnectionCallbacks,
			OnConnectionFailedListener,
			LocationListener{
	private double[] lats;
	private double[] longs;
	private String[] names;
	private int current;
	private GoogleMap map;
	private GoogleDirection gd;
	Document mDoc;
	private LocationClient mLocationClient;
	private long lastTimeMapUpdated=0;
	
	Button buttonRefreshRoute;
	Button buttonDisplayRoute;
	Button buttonAnimateRoute;
	
	boolean displayingRoute = true;
	
	private LatLng myPosition =new LatLng(0,0);//won't actually be displayed as (0,0). Will be changed as soon as onLocationChanged is called. More efficient than setting to null and checking whether it has received data.

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
		
		buttonRefreshRoute = (Button)findViewById(R.id.refreshRoute);
        buttonRefreshRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				redrawAllOnMap();
			}
		});
        
        buttonDisplayRoute = (Button)findViewById(R.id.displayRoute);
        buttonDisplayRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				displayingRoute = !(displayingRoute);
				redrawAllOnMap();
			}
		});
        
        buttonAnimateRoute = (Button)findViewById(R.id.animateRoute);
        buttonAnimateRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (gd!=null && mDoc!=null){
					gd.animateDirection(map, gd.getDirection(mDoc), GoogleDirection.SPEED_NORMAL
							, true, false, true, false, null, false, true, null);
				}
			}
		});
		
		mapSetup();
		drawRouteOnMap();
		
		

	}
	
	private void mapSetup() {
		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
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
	
	private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	public void onLocationChanged(Location arg0) {
		long currentTime = System.currentTimeMillis();
		if (currentTime-lastTimeMapUpdated>30000){ //30 sec
			Location myLocation = mLocationClient.getLastLocation();
			myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			redrawAllOnMap();
			lastTimeMapUpdated=currentTime;	
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Do nothing	
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
	}

	@Override
	public void onDisconnected() {
		// Do Nothing	
	}
	
	protected void onResume(){
		super.onResume();
        if (map==null) mapSetup();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
	}
	
	public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
	
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getApplicationContext(),
	                    this,  // ConnectionCallbacks
	                    this); // OnConnectionFailedListener
	        }
	    }
	 
	 //possible only while no animation is running
	 private void redrawAllOnMap(){
		if (!gd.isAnimated()){
			map.clear();
			poisSetup();
			if (displayingRoute) drawRouteOnMap();
	 	}
	 }	
	

}
