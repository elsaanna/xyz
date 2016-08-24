package cc.yamyam;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

import cc.yamyam.general.Constants;

/**
 * Created by siyuan on 01.08.15.
 */
public class PhotoMapClient {
    //public static final String API_ENDPOINT = "http://trololo.xxx/";
    //public static final String API_ENDPOINT = "http://trololo.xxx/demos/photomap/";
//    public static final String API_ENDPOINT = "http://yamyam.cc:3000/";
    //public static final String API_ENDPOINT = "http://yamyam.cc/";
    public static final String STORE = "photos/store";
    public static final String PHOTOS = "photos/index";
    // public static final String CREDENTIALS = "Basic ZXNrZXJkYTpoYXgwcg==";

    private static String getUrl(String rel) {
        return Constants.API_URL + rel;
    }

    public static void getPhotos(Context context, FutureCallback<JsonObject> callback) {

        Ion.getDefault(context).configure().setLogging(PhotoApp.TAG, Log.DEBUG);
        // Ion.getDefault(context).setLogging(PhotoApp.TAG, Log.DEBUG);
        //Ion.with(context, getUrl(PHOTOS)).asJsonObject().setCallback(callback);
        Ion.with(context).load(getUrl(PHOTOS)).asJsonObject().setCallback(callback);
    }

    private static String getB64Auth (String login, String pass) {
        String source=login+":"+pass;
        String ret="Basic "+ Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }
    public static void postPhoto(Context context,
                                 String device,
                                 double latitude, double longitude,
                                 String photoPath,
                                 String photoNoten,
                                 String shopid,
                                 String reference,
                                 String user_id,
                                 ProgressCallback progressCallback,
                                 FutureCallback<String> callback) {
        // Ion.getDefault(context).setLogging(PhotoApp.TAG, Log.DEBUG);

        Ion.getDefault(context).configure().setLogging(PhotoApp.TAG, Log.DEBUG);
        Ion.getDefault(context).with(context).load(getUrl(STORE))
                .setHeader("Authorization", getB64Auth("yam", "yam"))
                .uploadProgress(progressCallback)
                .setMultipartParameter("latitude", Double.toString(latitude))
                .setMultipartParameter("longitude", Double.toString(longitude))
                .setMultipartParameter("device", device)
                .setMultipartParameter("note", photoNoten)
                .setMultipartParameter("shopid", shopid)
                .setMultipartParameter("reference", reference)
                .setMultipartParameter("user_id", user_id)
                .setMultipartFile("photo", new File(photoPath))
                .asString().setCallback(callback);
    }


}
