package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(1000,700, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 150, 50, 900, 600);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			//System.out.println(feature.getProperties());
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getLocations());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}
	private void addKey() {
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Airports Key", xbase+35, ybase+25);
		fill(0);
		ellipse(xbase+20, 
				ybase+70, 
				10, 
				10);
		fill(255, 225, 0);
		ellipse(xbase+20, ybase+140, 10, 10);
		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("Airpots without code", xbase+30, ybase+70);
		text("Airpots with code", xbase+30, ybase+140);
		
	}
	
	public void mouseMoved() {
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerIfHover(airportList);
	}
	
	private void selectMarkerIfHover(List<Marker> markers) {
		//Aborted if there is a marker already selected
		if (lastSelected != null) {
			return;
		}
		for (Marker m : markers) {
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map, mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	public void mouseClicked() {
		if (lastClicked != null) {
			unhideMarkers();
			unselectMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) {
			checkAirportClicked();
			}
		
	}
	private void unhideMarkers() {
		for (Marker marker : airportList) {
			marker.setHidden(false);
		}
		for (Marker marker : routeList) {
			marker.setHidden(false);
		}
	}
	
	private void unselectMarkers() {
		for (Marker marker : airportList) {
			marker.setSelected(false);
		}
		for (Marker marker : routeList) {
			marker.setSelected(false);
		}
	}
	
	private void checkAirportClicked() {
		if (lastClicked != null) return;
		for (Marker m : airportList) {
			AirportMarker marker = (AirportMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				for (Marker mhide : airportList) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}	
				for (Marker mhide : routeList) {
					SimpleLinesMarker test = (SimpleLinesMarker)mhide;
					Location checkloc1 = test.getLocations().get(0);
					Location checkloc2 = test.getLocations().get(1);
					if (marker.getLocation() == checkloc1) {
						test.setHidden(false);
						for (Marker air : airportList) {
							if (air != lastClicked && air.getLocation() == checkloc2) {
								air.setHidden(false);
								air.setSelected(true);
								}
							}	
						}
					else {
						test.setHidden(true);
						}
		
					}
				return;
				}
				
			}
		}
	

}
