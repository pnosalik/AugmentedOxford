package ox.augmented;

import geo.GeoObj;
import geo.GeoUtils;
import gl.Color;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gui.GuiSetup;

import java.util.List;

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
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import commands.Command;

public class CustomARSetup extends Setup {
	public int activeTourID; //set by caller module
	private Tour theActiveTour;
	private Poi theCurrentPoi;
	
	private String nextPlace;
	private Location nextLocation;
	private GLCamera camera;
	private World world;
	private ActionWaitForAccuracy minAccuracyAction;
	private Action rotateGLCameraAction;
	private GuiSetup guiSetup;
	private TextView distanceInfo;
	
	private static final String LOG_TAG = "CustomARSetup";
	
	public Context context;
	//public CustomARSetup(Context mainActivity){
	//	this.context = mainActivity;
	//}
	
	public CustomARSetup(){
		
	}
	
	public void setTour(int id) {
		activeTourID = id;
		theActiveTour =  new Tour(context.getResources().openRawResource(activeTourID));
	}
	
	public void setTour(Tour tour) {
		theActiveTour =  tour;
	}
	
	@Override
	public void _a_initFieldsIfNecessary() {
		camera = new GLCamera();
		world = new World(camera);
		
		/*
		pois[0] = new Poi("Room", 51.363075, -1.333717, "Some Test Data \n\n\n\n Some More Lines\n\n\n\n\n\n\n\n\n\n\n Test \n\n\n\n\n A really really really really really really really really really really really really really really really really really long sentence");
		pois[1] = new Poi("School", 51.364023, -1.335228, "My old school");
		pois[2] = new Poi("Road", 51.362281, -1.336687, "Some public road");
		pois[3] = new Poi("Tristan's Home", 51.362573, -1.331921, "Tristan's house");
		pois[0] = new Poi("Ben's Home", 52.544650, -2.263560, "Ben's house");
		pois[0].setDataSource(new TwitterSource(pois[0],""));*/
		
		/*pois[0] = new Poi("Home", 42.703844, 23.369362, "Some Test Data \n\n\n\n Some More Lines\n\n\n\n\n\n\n\n\n\n\n Test \n\n\n\n\n A really really really really really really really really really really really really really really really really really long sentence");
		pois[1] = new Poi("Vesi", 42.706066, 23.368703, "My old school");
		pois[2] = new Poi("Fantastiko", 42.704753, 23.367389, "Some public road");
		pois[3] = new Poi("Desi", 42.703820, 23.359130, "Tristan's house");
		pois[4] = new Poi("Baba", 42.703820, 23.359130, "Ben's house");
		//pois[0].setDataSource(new TwitterSource(pois[0],""));
		*/
	
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
	
	private void addNextPoi(final GLFactory objectFactory) {
		if (theActiveTour.hasNext()){
			final Poi p = theActiveTour.getCurrentPoi();
			theCurrentPoi = p;
			nextPlace = p.getName();
			nextLocation = new Location("nextLocation");
			nextLocation.setLatitude(p.getLatitude());
			nextLocation.setLongitude(p.getLongitude());

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
					displayInfo(p.getName(),p.getInfo());
					if (theCurrentPoi==p){
						theActiveTour.incrementIndex();
						addNextPoi(objectFactory);
						o.setColor(Color.blue());
					}
					return true;
				}
			});
			world.add(o);
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
		glRenderer.addRenderElement(world);
		addNextPoi(objectFactory);
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
				//Location l = world.getMyCamera().getGPSLocation();
				Location l = GeoUtils.getCurrentLocation(getActivity());
				Float distance = l.distanceTo(nextLocation);
				distanceInfo.setText(
						"Current location: Lat: " + l.getLatitude() + " Long: " + l.getLongitude() +
						"Next location: " + nextPlace + " Lat: " + nextLocation.getLatitude() + " long: " + nextLocation.getLongitude() +
						" Distance: " + distance +" m");
				//distanceInfo.setText("Next location: " + nextPlace + ", Distance: " + distance +"m");
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
		guiSetup.addViewToBottom(distanceInfo);
	}
	

}
