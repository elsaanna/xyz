package cc.yamyam.api;

public interface HTTPRequestQueueListener {
	public void onHTTPRequestQueueFinished();
	public void onHTTPRequestQueueFailed();
}
