package unidue.ub.bibliometrics;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

public class Institution {

	private String institution;
	
	private String department;

	private String affilID;

	private String city;

	private String country;

	private int counter;

	private double longitude;

	private double latitude;

	public Institution() {
		this.affilID = "";
		this.counter=1;
		this.institution = "";
		this.city = "";
		this.country = "";
		this.latitude = 0;
		this.longitude = 0;
	}
	
	public Institution(String id) {
		this.affilID = id;
		this.counter = 1;
	}

	public Institution(String affilID, int counter) {
		this.affilID = affilID;
		this.counter = counter;
	}
	
	
	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @return the affilID
	 */
	public String getAffilID() {
		return affilID;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param institution the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @param affilID the affilID to set
	 */
	public void setAffilID(String affilID) {
		this.affilID = affilID;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void increaseCounter(int increment) {
		this.counter++;
	}

	public JSONObject createGeoJsonPoint() throws HttpException, JDOMException, IOException, SAXException {
		JSONObject geoJSONInd = new JSONObject();
		JSONObject geometry = new JSONObject();
		geometry.put("type", "Point");
		JSONArray coordinates = new JSONArray();
		coordinates.put(longitude).put(latitude);
		geometry.put("coordinates", coordinates);
		geoJSONInd.put("geometry", geometry);

		geoJSONInd.put("type", "Feature");
		JSONObject properties = new JSONObject();
		properties.put("name", city);
		geoJSONInd.put("properties", properties);
		String description = institution;
		if (!department.isEmpty())
			description = description + "<br />" + department;
		properties.put("popupContent", description);
		return geoJSONInd;
	}

	public void addToOutput(Element output) {
		Element institutionElement = new Element("institution");
		institutionElement.addContent(new Element("description").setText(institution));
		institutionElement.addContent(new Element("location").setText(city));
		institutionElement.addContent(new Element("country").setText(country));
		institutionElement.addContent(new Element("latitude").setText(String.valueOf(latitude)));
		institutionElement.addContent(new Element("longitude").setText(String.valueOf(longitude)));
		output.addContent(institution);
	}
}
