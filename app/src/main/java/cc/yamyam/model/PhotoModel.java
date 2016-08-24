package cc.yamyam.model;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siyuan on 01.08.15.
 */
public class PhotoModel {

    private String note;
    private String latitude;
    private String pathAbsolute;
    private Bitmap bitmap;
    private String longitude;
    private String idshop;
    private String shopReference;
    private String photoforupload; // reduced sized photo absolute path for uploading
    private List<PlacesModel> nearbyplaces=new ArrayList<PlacesModel>();
    private boolean checked=false;



    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public PhotoModel(String pathAbsolute) {
        this.pathAbsolute = pathAbsolute;
    }
    public PhotoModel() {
    }

    public String getPathAbsolute() {
        return pathAbsolute;
    }

    public void setPathAbsolute(String pathAbsolute) {
        this.pathAbsolute = pathAbsolute;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

    public String getIdshop() {
        return idshop;
    }
    public String getShopReference() {
        return shopReference;
    }

    public void setShopReference(String shopReference) {
        this.shopReference = shopReference;
    }

    public void setIdshop(String idshop) {
        this.idshop = idshop;
    }

    public String getPhotoforupload() {
        return photoforupload;
    }

    public void setPhotoforupload(String photoforupload) {
        this.photoforupload = photoforupload;
    }



    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<PlacesModel> getNearbyplaces() {
        return nearbyplaces;
    }

    public void setNearbyplaces(List<PlacesModel> nearbyplaces) {
        this.nearbyplaces = nearbyplaces;
    }

    public void populateNearbyPlacesFromJSON(JSONArray json) {
        try {
            for(int i=0;i<json.length();i++) {
                nearbyplaces.add(PlacesModel.fromJSON(json.getJSONObject(i)));
            }
        } catch(Exception e) {
            Log.e("YAMYAM", e.toString());
        }

    }
//{"id":357,"user_id":17,"filename":"7ad3fbb6-0f93-4f19-b238-2415b799c005","latitude":52.026043,
// "longitude":8.531486,"created_at":"2015-08-29T12:46:40.000Z","updated_at":"2015-08-29T12:46:40.000Z","note":4,"place_id":47,"user":

    private String id;
    private String user_id;
    private String filename;
    private String place_id;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static PhotoModel fromJSON(JSONObject json) {
        try {
            PhotoModel o = new PhotoModel();
            if(json.has("id"))
                o.id = json.getString("id");
            if(json.has("user_id"))
                o.user_id = json.getString("user_id");
            if(json.has("filename"))
                o.filename = json.getString("filename");
            if(json.has("latitude"))
                o.latitude = json.getString("latitude");
            if(json.has("longitude"))
                o.longitude = json.getString("longitude");
            if(json.has("note"))
                o.note = json.getString("note");
            if(json.has("place_id"))
                o.place_id = json.getString("place_id");
            return o;
        } catch(Exception e) {
            Log.e("YAMYAM", e.toString());
            return null;
        }
    }

}
