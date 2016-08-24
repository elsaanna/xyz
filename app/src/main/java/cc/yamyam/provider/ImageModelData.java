package cc.yamyam.provider;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.yamyam.api.HTTPRequest;
import cc.yamyam.api.HTTPRequestListener;
import cc.yamyam.model.ImageModel;

/**
 * Created by siyuan on 20.08.15.
 */
public class ImageModelData {

    public static ArrayList<ImageModel> mListImages = new ArrayList<ImageModel>();
    public String method;
    private ArrayAdapter adp;

    public ImageModelData(String method,ArrayAdapter adp) {
        this.method = method;
        this.adp = adp;
    }

    public void loadImgs(){
        HTTPRequest request = new HTTPRequest(this.method);
        request.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {
                try {
                    mListImages.clear();
                    JSONArray entries = response.getJSONArray("photos");
                    for (int i = 0; i < entries.length(); ++i) {
                        JSONObject o = entries.getJSONObject(i);
                        ImageModel m = ImageModel.fromJSON(o);
                        if(m!=null) mListImages.add(m);
                    }
                    adp.notifyDataSetChanged();
                }catch(Exception e) {
                    Log.e("yamyam", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onHTTPRequestFailed(int tag) {
                Log.e("yamyam","onHTTPRequestFailed");
            }

        });
        request.start();
    }
}
