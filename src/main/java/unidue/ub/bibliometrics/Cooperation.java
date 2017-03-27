package unidue.ub.bibliometrics;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cooperation {
    
    private Hashtable<String,Institution> affilIDMain;

    //private String affilIDCorresponding;

    private Hashtable<String,Institution> other;
    
    private String title;
    
    public Cooperation(Hashtable<String,Institution> affilIDMain, String affilIDCorresponding, Hashtable<String,Institution> other, String title) {
        this.affilIDMain = affilIDMain;
        //this.affilIDCorresponding = affilIDCorresponding;
        this.other = other;
        this.title = title;
    }
    
    public JSONObject createGeoJsonVectorCollaboration() {
        Institution mainInstitution = affilIDMain.get(affilIDMain.keys().nextElement());
        JSONObject geoJSON = new JSONObject();
        JSONObject geometry = new JSONObject();
        geoJSON.put("type","Feature");
        JSONObject properties = new JSONObject();
        properties.put("name", title);
        geoJSON.put("properties", properties);
        properties.put("popupContent", title);
        
        geometry.put("type", "LineString");
        JSONArray coordinates = new JSONArray();
        coordinates.put((new JSONArray()).put(mainInstitution.getLongitude()).put(mainInstitution.getLatitude()));
        Iterator<Entry<String, Institution>> institutionIterator = other.entrySet().iterator();
        while (institutionIterator.hasNext()) {
            Institution institution = institutionIterator.next().getValue();            
            coordinates.put((new JSONArray()).put(institution.getLongitude()).put(institution.getLatitude())).put((new JSONArray()).put(mainInstitution.getLongitude()).put(mainInstitution.getLatitude()));
        }
        return geoJSON;
    }
    
    public JSONObject createJSONLinesCorrespondingCollaboration() {
        if (affilIDMain.size() == 1) {
            return null;
        } else {
        JSONObject geoJSON = new JSONObject();
        JSONObject geometry = new JSONObject();
        geoJSON.put("type","Feature");
        JSONObject properties = new JSONObject();
        properties.put("name", title);
        geoJSON.put("properties", properties);
        properties.put("popupContent", title);
        geometry.put("type", "LineString");
        JSONArray coordinates = new JSONArray();
        Iterator<Entry<String, Institution>> institutionIterator = other.entrySet().iterator();
        while (institutionIterator.hasNext()) {
            Institution institution = institutionIterator.next().getValue();            
            coordinates.put((new JSONArray()).put(institution.getLongitude()).put(institution.getLatitude()));
        }
        return geoJSON;
        }
    }


}
