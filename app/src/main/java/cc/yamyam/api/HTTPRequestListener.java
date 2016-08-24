package cc.yamyam.api;

import org.json.JSONObject;

public interface HTTPRequestListener {
	public void onHTTPRequestFinished(JSONObject response, int tag);
	public void onHTTPRequestFailed(int tag);
}
