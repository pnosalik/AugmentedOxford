package ox.augmented;

import geo.GeoGraph;
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
import util.IO;
import util.Log;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionRotateCameraBuffered;
import actions.ActionWaitForAccuracy;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.MapActivity;

import commands.Command;

public class CustomARSetup extends Setup {
	public int activeTourID; //set by caller module
	private Tour theActiveTour;
	private Poi theCurrentPoi;
	
	private String nextPlace;
	private Location nextLocation;
	private int distanceAway;
	private GLCamera camera;
	private World world;
	private GeoGraph visitedPins;
	private GeoGraph unvisitedPins;
	private GLFactory objectFactory;
	private ActionWaitForAccuracy minAccuracyAction;
	private Action rotateGLCameraAction;
	private GuiSetup guiSetup;
	private TextView distanceInfo;
	private Stack<GeoObj> markers;
	private Stack<Poi> markedPois;
	
	private static final String LOG_TAG = "CustomARSetup";
	
	public Context context;
	public MapView mapView;
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
	}
	
	@Override
	public void _a_initFieldsIfNecessary() {
		camera = new GLCamera();
		world = new World(camera);
		markers = new Stack<GeoObj>();
		markedPois = new Stack<Poi>();
		visitedPins = new GeoGraph(false);
		unvisitedPins = new GeoGraph(false);
		world.add(visitedPins);
		world.add(unvisitedPins);
	
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
			o.setComp(objectFactory.newDiamond(Color.green()));
			o.setOnClickCommand(new Command(){
				@Override
				public boolean execute() {
					displayInfo(p.getName(),p.getInfo());;
					return true;
				}
			});
			
			o.setOnDoubleClickCommand(new Command(){
				@Override
				public boolean execute() {
					if (theCurrentPoi==p){
						addNextPoi();
						o.setColor(Color.blue());
					}
					return true;
				}
			});
			/*if(!markers.isEmpty()) {
				GeoObj m = markers.peek();
				unvisitedPins.remove(m);
				visitedPins.add(m);
			}*/
			unvisitedPins.add(o);
			markers.push(o);
			markedPois.push(p);
		}
		
	}
	
	private void updateDistanceInfo() {
		nextPlace = theCurrentPoi.getName();
		nextLocation = new Location("nextLocation");
		nextLocation.setLatitude(theCurrentPoi.getLatitude());
		nextLocation.setLongitude(theCurrentPoi.getLongitude());
	}
	
	private void skipPoi() {
		markers.peek().setColor(Color.blue());
		addNextPoi();
	}
	
	private void previousPoi() {
		if(markers.size() > 1){
			unvisitedPins.remove(markers.pop());
			markedPois.pop();
			GeoObj m = markers.peek();
			//visitedPins.remove(m);
			//unvisitedPins.add(m);
			m.setColor(Color.green());
			theCurrentPoi = markedPois.peek();
			updateDistanceInfo();
			theActiveTour.decrementIndex();
		}
	}
	
	private void displayInfo(String name, String info) {
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
		
		/**Intent intent = new Intent(getActivity(), InfoScreen.class);
		intent.putExtra("NAME",name);
		intent.putExtra("INFO", info);
		getActivity().startActivity(intent);*/
	}

	@Override
	public void _b_addWorldsToRenderer(GL1Renderer glRenderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		this.objectFactory = objectFactory;
		glRenderer.addRenderElement(world);
		addNextPoi();
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
					ActionWaitForAccuracy a) {
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
				Location l = camera.getGPSLocation();
				distanceAway = (int) l.distanceTo(nextLocation);	
				updateDistanceInfo();
				/*distanceInfo.setText(
				"Current location: Lat: " + l.getLatitude() + " Long: " + l.getLongitude() +
				"Next location: " + nextPlace + " Lat: " + nextLocation.getLatitude() + " long: " + nextLocation.getLongitude() +
				" Distance: " + distance +" m");*/
				distanceInfo.setText("Next location: " + nextPlace + ", Distance: " + distanceAway +"m");
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
		//guiSetup.addViewToBottom(distanceInfo);
		guiSetup.addViewToRight(distanceInfo);		
		/*final GMap map = GMap.newDefaultGMap((MapActivity) getActivity(),GoogleMapsKey.pc1DebugKey);
		try {
			map.addOverlay(new CustomItemizedOverlay(unvisitedPins, IO
					.loadDrawableFromId(getActivity(),
							de.rwth.R.drawable.mapdotgreen)));
			map.addOverlay(new CustomItemizedOverlay(visitedPins, IO
					.loadDrawableFromId(getActivity(),
							de.rwth.R.drawable.mapdotblue)));

		} catch (Exception e) {
			e.printStackTrace();
		}*/
		mapView.getMap();
		//((ViewGroup) mapView.getParent()).removeView(mapView);
		if (mapView.getParent()!=null) {
			((ViewGroup) mapView.getParent()).removeView(mapView);
		}
		guiSetup.addViewToBottomRight(mapView, 2f, 200);
		guiSetup.addButtonToBottomView(new Command() {
			
			@Override
			public boolean execute() {
				skipPoi();
				return true;
			}
			
		}, "Next");
		guiSetup.addButtonToBottomView(new Command() {
			
			@Override
			public boolean execute() {
				previousPoi();
				return true;
			}
			
		}, "Previous");
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				if (mapView.getVisibility() == View.VISIBLE)
					mapView.setVisibility(View.GONE);
				else
					mapView.setVisibility(View.VISIBLE);
				return true;
			}
			
		}, "Show/Hide map");
	}
	
	

}
