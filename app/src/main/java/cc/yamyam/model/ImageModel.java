package cc.yamyam.model;

import org.json.JSONObject;

import cc.yamyam.general.Constants;

/**
 * Created by siyuan on 17.08.15.
 */
public class ImageModel {

    private String id;
    private String imageName;
    private String imageThumbUrl;
    private String imageUrl;
    private String lat;
    private String lng;



    private String created_at;



    private String phone_number;
    private String note;

    private String placeId;
    private String placeGId;
    private String placeName;
    private String placeAdress;
    private String placeLat;
    private String placeLng;

    public ImageModel() {
    }

    @Override
    public String toString() {
        return  placeName + '\'' +
                placeAdress + '\'' +
                note + " points " + '\'' +
                //"[" + phone_number +"]" +
                " " + created_at + '\''
                ;
    }


    public static ImageModel fromJSON(JSONObject json) {

        try {
            ImageModel p = new ImageModel();

            if(json.has("filename"))  p.imageName = json.getString("filename")+".jpeg";
            if(json.has("id")) p.id = json.getString("id");
            if(json.has("latitude")) p.lat = json.getString("latitude");
            if(json.has("longitude")) p.lng = json.getString("longitude");
            if(json.has("note")) p.note = json.getString("note");
            if(json.has("created_at")) p.created_at = json.getString("created_at");
            if(json.has("phone_number")) p.phone_number = json.getString("phone_number");

            if(json.has("place"))
            {
                JSONObject place = json.getJSONObject("place");
                if(place.has("id")){
                    p.placeId = place.getString("id");
                    if(place.has("place_id"))  p.placeGId = place.getString("place_id");
                    if(place.has("latitude"))  p.placeLat = place.getString("latitude");
                    if(place.has("longitude"))  p.placeLng = place.getString("longitude");
                    if(place.has("name"))  p.placeName = place.getString("name");
                    if(place.has("address"))  p.placeAdress = place.getString("address");

                }
            }

            /*
            JSONArray opts = json.getJSONArray("poll_options");
            for(int i=0;i<opts.length();i++) {
                p.options.add(Option.fromJSON(opts.getJSONObject(i)));
            }

            p.user = User.fromJSON(json);

            if(p.id == null)
                return null;
               */
            return p;
        } catch(Exception e) {

            return null;
        }
    }


    public ImageModel(String id,String imageName) {
        this.id = id;
        this.imageName = imageName;
    }

    public ImageModel(String imageName) {
        this.imageName = imageName;
    }


    public String getCreated_at() {
        return created_at;
    }
    public String getPhone_number() {
        return phone_number;
    }


    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPlaceGId() {
        return placeGId;

    }

    public void setPlaceGId(String placeGId) {
        this.placeGId = placeGId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageThumbUrl() {
        return Constants.IMG_WEB_DIR_THUMB_URL+this.imageName;
       // return this.imageThumbUrl;
    }



    public String getImageUrl() {
        return Constants.IMG_WEB_DIR_URL+this.imageName;
        //return this.imageUrl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPlaceAdress() {
        return placeAdress;
    }

    public void setPlaceAdress(String placeAdress) {
        this.placeAdress = placeAdress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(String placeLat) {
        this.placeLat = placeLat;
    }

    public String getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(String placeLng) {
        this.placeLng = placeLng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
