package cc.yamyam.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.yamyam.BaseFragment;
import cc.yamyam.R;
import cc.yamyam.api.HTTPRequest;
import cc.yamyam.api.HTTPRequestListener;
import cc.yamyam.general.Constants;
import cc.yamyam.image.ImageCache;
import cc.yamyam.image.ImageFetcher;
import cc.yamyam.model.PhotoModel;
import cc.yamyam.model.PlacesModel;

/**
 * Created by siyuan on 19.08.15.
 */
public class MyrestFragment extends BaseFragment  {

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private PlacesListAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private ArrayList<PlacesModel> placeslist = new ArrayList<>();

    private  ListView listview;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAdapter = new PlacesListAdapter(getActivity(),placeslist);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_places, container, false);
         listview = (ListView) v.findViewById(R.id.list);
        //status =(TextView) v.findViewById(R.id.status);
        listview.setAdapter(mAdapter);
        progressBar =(ProgressBar)v.findViewById(R.id.progressbarListPlaces);
        loadPlaces();
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
       // inflater.inflate(R.menu.menu_fragment_places, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        switch (item.getItemId()) {
            case R.id.action_whereami:
                onAction_whereami();
                return true;
            case R.id.action_refresh:
                onAction_refresh();
                return true;
            case R.id.action_search:
                onAction_search();
                return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }




    private class PlacesListAdapter extends ArrayAdapter<PlacesModel> {

        public PlacesListAdapter(Context context, List<PlacesModel> list) {
            super(context, R.layout.fragment_myrest, list);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if( convertView == null ){
                //We must create a View:
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_myrest, container, false);
            }
            PlacesModel m = getItem(position);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView address = (TextView)convertView.findViewById(R.id.address);
            ImageView img = (ImageView)convertView.findViewById(R.id.list_image);
            TextView countPhotos = (TextView)convertView.findViewById(R.id.countPhotos);
            name.setText(m.getName());
            address.setText(m.getVicinity());
            if(m.getListPhotos()!=null)
            {
                countPhotos.setText(m.getListPhotos().size()+" Photos");
            }

            String imgUrl = m.getIcon();
            if(imgUrl==null )
            {
                if(m.getListPhotos()!=null && m.getListPhotos().size()>0)
                {
                    PhotoModel p = m.getListPhotos().get(0);
                    imgUrl = Constants.IMG_WEB_DIR_THUMB_URL+p.getFilename()+".jpeg";
                }
            }

            mImageFetcher.loadImage(imgUrl, img);
            return convertView;
        }
    }

    private void loadPlaces()
    {
       //
        progressBar.setVisibility(View.VISIBLE);
        listview.removeAllViewsInLayout();
        String myresurl = "places?currentPage=0&userId=17";
        HTTPRequest request = new HTTPRequest(myresurl);
        request.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {
                try {
                    placeslist.clear();
                    if(response.getString("status").equals("OK")) {
                        JSONArray entries = response.getJSONArray("results");
                        for (int i = 0; i < entries.length(); ++i) {
                            JSONObject o = entries.getJSONObject(i);
                            PlacesModel m = PlacesModel.fromJSONYam(o);
                            if (m != null) placeslist.add(m);
                        }
                        mAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }else
                    {
                        Toast.makeText(getActivity(), "No Results!", Toast.LENGTH_LONG).show();
                        mAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    }

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
