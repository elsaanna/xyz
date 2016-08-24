package cc.yamyam.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by siyuan on 05.08.15.
 */
public class PlacesModel {
    private String id;
    private String name;
    private String place_id;
    private String vicinity;
    private String latitude;
    private String longitude;
    private String reference;
    private String icon;

    public ArrayList<PhotoModel> getListPhotos() {
        return listPhotos;
    }

    public void setListPhotos(ArrayList<PhotoModel> listPhotos) {
        this.listPhotos = listPhotos;
    }

    private   ArrayList<PhotoModel> listPhotos= new ArrayList<>();

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public PlacesModel(String id, String name, String place_id, String vicinity, String reference) {
        this.id = id;
        this.name = name;
        this.place_id = place_id;
        this.vicinity = vicinity;
        this.reference = reference;
    }

    public PlacesModel() {
    }

    public static PlacesModel fromJSON(JSONObject json) {
        try {
            PlacesModel o = new PlacesModel();
            if(json.has("id"))
                o.id = json.getString("id");
            if(json.has("place_id"))
                o.place_id = json.getString("place_id");
            if(json.has("name"))
                o.name = json.getString("name");
            if(json.has("vicinity"))
                o.vicinity = json.getString("vicinity");
            if(json.has("reference"))
                o.reference = json.getString("reference");
            if(json.has("icon"))
                o.icon = json.getString("icon");
            return o;
        } catch(Exception e) {
            Log.e("YAMYAM", e.toString());
            return null;
        }
    }

    public static PlacesModel fromJSONYam(JSONObject json) {
        try {
            PlacesModel o = new PlacesModel();
            if(json.has("id"))
                o.id = json.getString("id");
            if(json.has("place_id"))
                o.place_id = json.getString("place_id");
            if(json.has("name"))
                o.name = json.getString("name");
            if(json.has("address"))
                o.vicinity = json.getString("address");
            if(json.has("reference"))
                o.reference = json.getString("reference");
            if(json.has("photo")) {
                String ps = json.getString("photo");
                if(ps==null || "null".equalsIgnoreCase(ps) || ps.trim().length()==0)
                {
                    o.icon = null;
                }else {
                    o.icon = json.getString("photo");
                }
            }
            if(json.has("photos"))
            {
                JSONArray entries = json.getJSONArray("photos");
                for (int i = 0; i < entries.length(); ++i) {
                    JSONObject obj = entries.getJSONObject(i);
                    PhotoModel m = PhotoModel.fromJSON(obj);
                    if (m != null) o.getListPhotos().add(m);
                }

            }
            return o;
        } catch(Exception e) {
            Log.e("YAMYAM", e.toString());
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return name ;
    }
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
