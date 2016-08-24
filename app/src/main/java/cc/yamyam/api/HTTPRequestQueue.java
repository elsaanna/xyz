package cc.yamyam.api;

import java.util.ArrayList;


public class HTTPRequestQueue {

	private ArrayList<HTTPRequest> queue = new ArrayList<HTTPRequest>();
	private HTTPRequestQueueListener listener;
	private int c;
	private Boolean fail = false;
	
	public void start() {
		for(HTTPRequest r : queue) {
			r.start();
		}
	}

	public void setListener(HTTPRequestQueueListener d) {
		listener = d;
	}
	
	public void addRequest(HTTPRequest r) {
		r.setQueue(this);
		queue.add(r);
	}
	
	public void onRequestFinished() {
		c++;
		if(c == queue.size()) {
			if(fail) {
				listener.onHTTPRequestQueueFailed();
			} else {
				listener.onHTTPRequestQueueFinished();
			}
		}
		
	}
	
	public void onRequestFailed() {
		c++;
		fail = true;
		
		if(c == queue.size()) {
			listener.onHTTPRequestQueueFailed();
		}
	}
	
	
    
    

}
