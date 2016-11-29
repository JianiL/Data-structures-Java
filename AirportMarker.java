package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		colorDetermine(pg);
		pg.ellipse(x, y, 5, 5);
		pg.popStyle();
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		String air_city = getCity();
		String air_name = getName();
		String air_country = getCountry();
		String air_code = getCode();
		String title = air_name + air_city + air_country + air_code;
		pg.pushStyle();
		pg.rectMode(PConstants.CORNER);
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(air_city + air_name + air_country + air_code) +6, 18, 5);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
		pg.popStyle();
		// show routes
		
	}
	
	private void colorDetermine(PGraphics pg) {
		String id_code = getCode();
		//System.out.println(id_code);
		if (id_code.length() == 2) {
			pg.fill(0, 0, 0);
		}
		else {
			pg.fill(255, 255, 0);
		}
		 
	}
	
	public String getCity() {
		return (String) getStringProperty("city").replace("\"", ", ");
	}
	public String getName() {
		return (String) getStringProperty("name").replace("\"", "");
	}
	public String getCountry() {
		return (String) getStringProperty("country").replace("\"", "");
	}
	public String getCode() {
		return (String) getStringProperty("code").replace("\"", " ");
	}
	public float getAltitude() {
		return Float.parseFloat(getProperty("altitude").toString());
	}
	
}
