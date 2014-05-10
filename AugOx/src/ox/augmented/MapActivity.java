package ox.augmented;

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
	private Document mDoc;
	private LocationClient mLocationClient;
	
	private Button buttonRefreshRoute;
	private Button buttonDisplayRoute;
	private Button buttonAnimateRoute;
	
	private boolean displayingRoute = true;
	private LatLng myPosition =new LatLng(0,0);//won't actually be displayed as (0,0). Will be changed as soon as onLocationChanged is called. More efficient than setting to null and checking whether it has received data.
	private long lastTimeMapUpdated=0;
	
	private static final double MAX_DISTANCE = 30000;//30km . In meters. Crashes for long distances, e.g. Oxford-Bangalor
	private static final int MIN_REFRESH_PERIOD = 30000;//30s. In millisecond. Will request for new data automaticly only if MIN_REFRESH_PERIOD time has passed since the last update.

	
	
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
				lastTimeMapUpdated = System.currentTimeMillis();
			}
		});
        
        buttonDisplayRoute = (Button)findViewById(R.id.displayRoute);
        buttonDisplayRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				displayingRoute = !(displayingRoute);
				redrawAllOnMap();
				lastTimeMapUpdated = System.currentTimeMillis();
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
		LatLng destPosition = new LatLng(lats[current], longs[current]);
		if (distance(myPosition.latitude, myPosition.longitude, destPosition.latitude, destPosition.longitude)<=MAX_DISTANCE){
			gd.setLogging(true);
			gd.request(myPosition, destPosition, GoogleDirection.MODE_WALKING);
		}
	}
	
	private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	public void onLocationChanged(Location arg0) {
		long currentTime = System.currentTimeMillis();
		if (currentTime-lastTimeMapUpdated>MIN_REFRESH_PERIOD){ //30 sec
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
	
	 public double distance (double lat_a, double lng_a, double lat_b, double lng_b ) 
	 {
	     double earthRadius = 3958.75;
	     double latDiff = Math.toRadians(lat_b-lat_a);
	     double lngDiff = Math.toRadians(lng_b-lng_a);
	     double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
	     Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
	     Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
	     double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	     double distance = earthRadius * c;

	     int meterConversion = 1609;

	     return Double.valueOf(distance * meterConversion);
	 }

}
