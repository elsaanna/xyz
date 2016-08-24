package cc.yamyam.api;


import android.graphics.Bitmap;

public interface HTTPRequestBitmapListener {
	public void onHTTPRequestFinished(Bitmap bitmap, int tag);
	public void onHTTPRequestFailed(int tag);
}
