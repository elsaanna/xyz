package cc.yamyam.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.yamyam.general.Constants;

public class HTTPRequestBitmap extends Thread {

	private String url;
	private Bitmap bitmap;
	private HTTPRequestBitmapListener listener;
	private int tag=0;
	private Handler handler;



	public HTTPRequestBitmap() {
		this(true);
	}

	public HTTPRequestBitmap(Boolean asynchron) {

		if(asynchron) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message m) {
					if(m.what == Constants.OK) {
						if(listener != null) {
							listener.onHTTPRequestFinished(bitmap,tag);
						}
					} else {
						if(listener != null) {
							listener.onHTTPRequestFailed(tag);
						}
					}
				}
		    };
		}

	}


	@Override
	public void run() {

		bitmap = getBitmapData();

		if(bitmap == null) {
			handler.sendEmptyMessage( Constants.FAIL);
		} else {
			handler.sendEmptyMessage( Constants.OK);
		}

	}

	public Bitmap startSynchronRequest() {

		return getBitmapData();

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String u) {
		url = u;
	}

	public void setTag(int t) {
		tag = t;
	}

	public void setListener(HTTPRequestBitmapListener d) {
		listener = d;
	}

	private Bitmap getBitmapData() {

		try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap bm = BitmapFactory.decodeStream(input);
	        return bm;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }

	}




}
