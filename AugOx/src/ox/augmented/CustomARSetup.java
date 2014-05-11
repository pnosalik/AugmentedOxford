package ox.augmented;

import geo.GeoObj;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gui.GuiSetup;

import java.util.List;
import java.util.Stack;

import ox.augmented.data.TourCreator;
import ox.augmented.model.Poi;
import ox.augmented.model.Tour;
import system.EventManager;
import system.Setup;
import util.Log;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionRotateCameraBuffered;
import actions.ActionWaitForAccuracy;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import commands.Command;
import commands.ui.CommandShowToast;

public class CustomARSetup extends Setup {
	public int activeTourID; //set by caller module
	private Tour theActiveTour;
	private Poi theCurrentPoi;
	
	private boolean minAccuracyReached; //Whether the GPS location is accurate, initially false
	private String nextPlace; //Name of the next location
	private Location nextLocation; //Poi of the next location
	private int distanceAway; //Distance to the next Poi in metres
	private GLCamera camera;
	private World world;
	private GLFactory objectFactory;
	private ActionWaitForAccuracy minAccuracyAction;
	private Action rotateGLCameraAction;
	private GuiSetup guiSetup;
	private TextView distanceInfo;
	private Stack<GeoObj> markers; //A stack of the markers corresponding to the displayed pois
	private Stack<Poi> markedPois; //A Stack of the displayed Pois
	
	private static final String LOG_TAG = "CustomARSetup";
	
	public Context context;
	//public MapView mapView;
	//public CustomARSetup(Context mainActivity){
	//	this.context = mainActivity;
	//}
	
	public CustomARSetup(){
		
	}
	
	public void setTour(int id) {
		activeTourID = id;
		theActiveTour =  TourCreator.parseXml(context.getResources().openRawResource(activeTourID));
	}
	
	public void setTour(Tour tour) {
		theActiveTour =  tour;
		theActiveTour.setIndex(0);
	}
	
	@Override
	public void _a_initFieldsIfNecessary() {
		camera = new GLCamera();
		world = new World(camera);
		markers = new Stack<GeoObj>();
		markedPois = new Stack<Poi>();
		minAccuracyReached = false;
		distanceInfo = new TextView(getActivity());
		
		//PRECONDITION: setTour(int) or setTour(Tour) has already been called before creating this Setup
		
		Tour tour = theActiveTour;
		
		//Tour tour = new Tour(context.getResources().openRawResource(activeTourID));
		List<Poi> poisTest = tour.getAllPois();
		System.out.println("TourData");
		System.out.println("ID: "+tour.getID());
		System.out.println("Name: " +tour.getName());
		System.out.println("Info: " +tour.getInfo());
		System.out.println("Size: " +tour.getSize());
		for (int i = 0; i<tour.getSize(); i++){
			System.out.println("POI " + i);
			System.out.println("Name: "+poisTest.get(i).getName());
			System.out.println("Latitude: "+poisTest.get(i).getLatitude());
			System.out.println("Longitude: "+poisTest.get(i).getLongitude());
			System.out.println("Info: "+poisTest.get(i).getInfo());
			if (poisTest.get(i).getDataSource()!=null) System.out.println("HashTags: "+poisTest.get(i).getDataSource().getSearchString());
		}
		//theActiveTour = tour;
		distanceInfo.setText("Tour loaded: " + tour.getName());
	
	}
	
	private void addNextPoi() {
		if (theActiveTour.hasNext()){
			final Poi p = theActiveTour.getCurrentPoi();
			theActiveTour.incrementIndex();			
			theCurrentPoi = p;
			updateDistanceInfo();

			final GeoObj o = new GeoObj(p.getLatitude(),p.getLongitude());
			o.setComp(objectFactory.newDiamond(Color.green())); //Green represents unvisited
			
			o.setOnClickCommand(new Command(){
				@Override
				public boolean execute() {
					String data = p.getDataSourceInfo();
					if(p.hasDataSource() && data != "") {
						displayDataSourceInfo(p.getName(), data);
					}
					else {
						displayDataSourceInfo(p.getName(),"No social media data for this location");
					}
					return true;
				}
			});
			
			o.setOnDoubleClickCommand(new Command(){
				@Override
				public boolean execute() {
					displayInfo(p.getName(),p.getInfo());
					if (theCurrentPoi==p){ //If current Poi add the next
						addNextPoi();
						o.setColor(Color.blue()); //Blue represents visited
					}
					return true;
				}
			});

			world.add(o); //Add the next marker to the world
			markers.push(o); //Push it to the stack of markers
			markedPois.push(p); //Push the Poi to the stack of displayed Pois
		}
		
	}
	
	private void updateDistanceInfo() {
		nextPlace = theCurrentPoi.getName();
		nextLocation = new Location("nextLocation");
		nextLocation.setLatitude(theCurrentPoi.getLatitude());
		nextLocation.setLongitude(theCurrentPoi.getLongitude());
	}
	
	/* Display Help dialog screen */
	private void help() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());	
		View view = View.inflate(getActivity(), R.layout.help_layout, null);
		builder.setView(view);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void skipPoi() {
		markers.peek().setColor(Color.blue());
		addNextPoi();
	}
	
	private void previousPoi() {
		if(markers.size() > 1){
			GeoObj m = markers.peek();
			//A rather crude test
			if(m.getGraphicsComponent().getColor().equals(Color.green())) {
				world.remove(markers.pop());
				theActiveTour.decrementIndex();
				markedPois.pop();
			}
			m = markers.peek();
			m.setColor(Color.green());
			theCurrentPoi = markedPois.peek();
			updateDistanceInfo();
		}
	}
	
	//Display a TextView stating a name of a location and the data source's information on it
	private void displayDataSourceInfo(String name, String info) {
		final TextView v = (TextView)View.inflate(getActivity(), R.layout.poi_layout, null);
		v.setText(name + "\n" + info);
		v.setTextColor(Color.white().toIntARGB());
		v.setBackgroundColor(Color.blueTransparent().toIntARGB());
		v.setTextSize(24f);
		v.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View arg0) {
					guiSetup.getTopView().removeAllViews();
				}
			
			});
		
		getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	guiSetup.getTopView().removeAllViews();
		 		guiSetup.addViewToTop(v);		
		    }
		});
		
	}
	
	//Display the name of a location and the defined information about it
	private void displayInfo(String name, String info) {
		/**InfoScreenSettings i = new InfoScreenSettings(getActivity());
		i.addText(name);
		i.addText(info);
		ActivityConnector.getInstance().startActivity(getActivity(),
				InfoScreen.class, i);**/
		//Using DataSourceDisplay since InfoScreen doesn't display as intended
		displayDataSourceInfo(name, info);
	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer glRenderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		this.objectFactory = objectFactory;
		glRenderer.addRenderElement(world);
		//addNextPoi();
	}

	@Override
	public void _c_addActionsToEvents(final EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		
		rotateGLCameraAction = new ActionRotateCameraBuffered(camera);
		eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(world, camera));
		
		minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 24.0f, 10) {
			@Override
			public void minAccuracyReachedFirstTime(Location l,
					ActionWaitForAccuracy a) { //Add the first Poi once there is good signal
				minAccuracyReached = true;
				addNextPoi();
				if (!eventManager.getOnLocationChangedAction().remove(a)) {
					Log.e(LOG_TAG,
							"Could not remove minAccuracyAction from the onLocationChangedAction list");
				}
			}
		};
		eventManager.addOnLocationChangedAction(minAccuracyAction);
		eventManager.addOnLocationChangedAction(new Action() {
			
			@Override
			public boolean onLocationChanged(Location location) {
				if(minAccuracyReached){
					Location l = camera.getGPSLocation();
					distanceAway = (int) l.distanceTo(nextLocation);
					if(distanceAway < 5) {
						//Add the next poi if within 5m of the current
						markers.peek().setColor(Color.blue());
						addNextPoi();
					}
					updateDistanceInfo();
					distanceInfo.setText("Next location: " + nextPlace + ", Distance: " + distanceAway +"m");
				}
				return true; //So that it is never removed from the list
			}
			
			
		});
	}
	

	
	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) {
		updater.addObjectToUpdateCycle(world);
		updater.addObjectToUpdateCycle(rotateGLCameraAction);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		this.guiSetup = guiSetup;
		guiSetup.addViewToTop(minAccuracyAction.getView());
		
		//Help Button
		guiSetup.addImangeButtonToRightView(
				R.drawable.ic_action_help,
				new Command() {

					@Override
					public boolean execute() {
						help();
						return true;
					}
				});
		
		//Next Poi Button
		guiSetup.addButtonToBottomView(new Command() {
			
			@Override
			public boolean execute() {
				if(minAccuracyReached) {
					skipPoi();
				}
				else {
					CommandShowToast.show(getActivity(), "Waiting on GPS accuracy");
				}
				return true;
			}
			
		}, "Next");
		
		//Previous Poi Button
		guiSetup.addButtonToBottomView(new Command() {
			
			@Override
			public boolean execute() {
				if(minAccuracyReached) {
					previousPoi();
				}
				else {
					CommandShowToast.show(getActivity(), "Waiting on GPS accuracy");
				}
				return true;
				
			}
			
		}, "Previous");
		
		//Show Map Button
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
					if(isOnline() && minAccuracyReached) {
						Intent intent = new Intent(getActivity(), MapActivity.class);
						Poi[] p = theActiveTour.getAllPoisAsArray();
						int n = p.length;
						double[] lats = new double[n];
						double[] longs = new double[n];
						String[] names = new String[n];
						for(int i = 0;i < n; i++) {
							lats[i] = p[i].getLatitude();
							longs[i] = p[i].getLongitude();
							names[i] = p[i].getName();
						}
						intent.putExtra("LATS", lats);
						intent.putExtra("LONGS", longs);
						intent.putExtra("NAMES", names);
						intent.putExtra("CURRENT", theActiveTour.getIndex()-1);
						
						getActivity().startActivity(intent);
						return true;
					}
					if(!isOnline()){
						Log.d("CustomARSetup.Show map", "No internet connection, not displaying map.");
						CommandShowToast.show(getActivity(), "No internet connection");
					}
					else {
						CommandShowToast.show(getActivity(), "Waiting on GPS accuracy");
					}
					return false;
			}
			
			}, "Show map");
		
		//Text stating the next location and the distance to it
		guiSetup.addViewToBottom(distanceInfo);
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	

}
