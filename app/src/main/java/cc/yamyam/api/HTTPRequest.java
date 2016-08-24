package cc.yamyam.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.yamyam.PhotoApp;
import cc.yamyam.general.Constants;

import static cc.yamyam.general.Constants.API_USERAGENT;
import static cc.yamyam.general.Constants.FAIL;
import static cc.yamyam.general.Constants.OK;

public class HTTPRequest extends Thread {

	private String url;
	private JSONObject response;
	private String type = "get";
	private List<NameValuePair> post = new ArrayList<NameValuePair>();
	private Map<String, Bitmap> images = new HashMap<String,Bitmap>();
	private HTTPRequestListener listener;
	private HTTPRequestQueue queue = null;
	private int tag=0;
	private Handler handler;
	private static String token = null;
	private static Activity activity;
	
	public HTTPRequest() {
		
	}
	
	public HTTPRequest(String method) {

		this(method, true);
	    
	}

	public HTTPRequest(String url, Boolean asynchron, boolean isgoogleurl) {

		this.url = url;
		//Utils.log(url);

		if(asynchron) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message m) {
					if(m.what == OK) {
						if(listener != null) {
							listener.onHTTPRequestFinished(response,tag);
						}
						if(queue != null) {
							queue.onRequestFinished();
						}
					} else {

						if(listener != null) {
							listener.onHTTPRequestFailed(tag);
						}
						if(queue != null) {
							queue.onRequestFailed();
						}
					}
				}
			};
		}
	}


	public HTTPRequest(String method, Boolean asynchron) {
		
		if(url == null) {
			url = Constants.API_URL+method;
		}
		
		//Utils.log(url);
		
		if(asynchron) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message m) {
					if(m.what == OK) {
						if(listener != null) {
							listener.onHTTPRequestFinished(response,tag);
						}
						if(queue != null) {
							queue.onRequestFinished();
						}
					} else {
						
						if(listener != null) {
							listener.onHTTPRequestFailed(tag);
						}
						if(queue != null) {
							queue.onRequestFailed();
						}
					}
				}
		    };
		}
	}



	@Override
	public void run() {
		
		response = getUrlData();
				
		if(response == null || response.has("request_error")) {
			
			handler.sendEmptyMessage(FAIL);
		} else {
			handler.sendEmptyMessage(OK);
		}
		
	}
	
	public static void clearToken() {
		token = null;
	}


	public JSONObject startSynchronRequest() {
		
		return getUrlData();
		
	}

	public String getUrl() {
		return url;
	}
	
	public void addPostValue(String key, String value) {
		if(!type.equals("file")) {
			type = "post";
		}
		post.add(new BasicNameValuePair(key,value));
	}
	
	public void addImageValue(String key, Bitmap bitmap) {
		type = "file";
		images.put(key, bitmap);
	}
	
	public void setUrl(String u) {
		url = u;
	}
	
	public void setQueue(HTTPRequestQueue q) {
		queue = q;
	}
	
	public HTTPRequestQueue getQueue() {
		return queue;
	}
	
	public void setTag(int t) {
		tag = t;
	}

	public void setListener(HTTPRequestListener d) {
		listener = d;
	}


	private JSONObject getUrlData() {    	

		Log.i(PhotoApp.getTag(),"getUrlData");

    	JSONObject getData = new JSONObject();
    	
		try {
			URI uri = new URI(url);

			Log.i(PhotoApp.getTag(),uri.toString());
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 20000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 30000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClientParams.setRedirecting(httpParameters, false);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			client.setCookieStore(getCookieStore());
			
			HttpResponse res;
			Log.i("yamyam","request type:"+type);


			if(type.equals("post")) {

				HttpPost method = new HttpPost(uri);
				method.setEntity(new UrlEncodedFormEntity(post,"UTF-8"));
				method.setHeader("User-Agent", API_USERAGENT);
				res = client.execute(method);
				
			} /*else if(type.equals("file")) {

				HttpPost method = new HttpPost(uri);

				MultipartEntityBuilder entity = MultipartEntityBuilder.create();
				entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				for (Map.Entry<String, Bitmap> entry : i mages.entrySet()) {
					Bitmap bitmap = entry.getValue();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.JPEG, 80, bos);
					byte[] data = bos.toByteArray();
					entity.addPart(entry.getKey(), new ByteArrayBody(data, "picture.jpg"));
				}

				for (int j = 0; j < post.size(); j++) {
					BasicNameValuePair p = (BasicNameValuePair) post.get(j);
					entity.addTextBody(p.getName(), p.getValue());					
				}

				method.setEntity(entity.build());
				res = client.execute(method);				

			}*/ else {
				HttpGet method = new HttpGet(uri);
				method.setHeader("User-Agent", API_USERAGENT);
				res = client.execute(method);
			}
			
			int statusCode = res.getStatusLine().getStatusCode();
			
			if (statusCode != 200 && statusCode != 302) {
				throw new Exception("status code for "+url+" "+statusCode);
			}
			
			HttpEntity entity = res.getEntity();
			String json = EntityUtils.toString(entity, "UTF-8");
			
			
			String locationHeaderString = null;
			Header locationHeader = res.getFirstHeader("Location");
			if(locationHeader != null) {
				locationHeaderString = locationHeader.getValue();
				
			}
			
			if(locationHeaderString != null) { // location header handling
				if(locationHeaderString.equals("/user/missing-info")) {
					getData = new JSONObject();
					getData.put("code", "ERROR");
					getData.put("missing_info", true);
				} else { 
					getData = new JSONObject();
					getData.put("code", "OK");
				}
			} else {
				getData = new JSONObject(json);
				
				// check login
				try {
					if(getData.getJSONObject("content").getString("msg").equals("User not found")) {
						//Utils.log("NO USER");
						
						//Toast.makeText(activity, activity.getString(R.string.login_invalid), Toast.LENGTH_SHORT).show();
						
						//activity.restart(true);
						
					}
				} catch(Exception e) {
					//Utils.log("USER CHECK OK");
				}
				
			}		
			
			// try to get authtoken (=php session id)
			getData.put("token", getSessionId(client.getCookieStore()));
			
			
			
		} catch (Exception e) {

			Log.i(PhotoApp.getTag(),e.toString());
			getData = new JSONObject();
			try {
				getData.put("request_error", true);
			} catch(Exception ee){
				Log.i(PhotoApp.getTag(),ee.toString());
			}
			
		}
    	
    	
		return getData;
	}


	private CookieStore getCookieStore() {
		
		CookieStore store = new BasicCookieStore();
		
		if(token != null) {
			BasicClientCookie cookie = new BasicClientCookie(Constants.API_COOKIE_NAME, token);
			cookie.setDomain(Constants.API_COOKIE_DOMAIN);
			store.addCookie(cookie);
			
			//Utils.log("add cookie "+cookie.getDomain()+" "+cookie.getName()+" "+cookie.getValue());
		}
		
		return store;
		
	}
	
	private String getSessionId(CookieStore c) {
		String authToken = "";
		for(Cookie ec : c.getCookies()) {
			if(ec.getName().equals(Constants.API_COOKIE_NAME)) {
				authToken = ec.getValue();
				break;
			}
		}	
		
		if(authToken != null) {
			token = authToken;
		}
		
		return authToken;		
	}


	public static void setActivity(Activity a) {
		activity = a;
	}


}
