package cc.yamyam.ui;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import cc.yamyam.image.ImageCache;
import cc.yamyam.image.ImageFetcher;
import cc.yamyam.model.PlacesModel;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

/**
 * Created by siyuan on 19.08.15.
 */
public class PlacesFragment extends BaseFragment implements OnLocationUpdatedListener {

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private PlacesListAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private static final String IMAGE_CACHE_DIR = "thumbs";
    public static Location mLastLocation;
    private ArrayList<PlacesModel> placeslist = new ArrayList<>();
    private LocationGooglePlayServicesProvider provider;
    private TextView status;
    private  ListView listview;
    private ProgressBar progressBar;
    private static String mradius = "500"; // default 500meter
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

        /*
        Button startLocation = (Button) v.findViewById(R.id.start_location);
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation();
            }
        });
        */
        startLocation();
        return v;
    }

    @Override
    public void onLocationUpdated(Location location) {
        mLastLocation = location;
        Toast.makeText(getActivity().getBaseContext(),"Location updated",Toast.LENGTH_LONG).show();
        if(location!=null) {

            loadPlaces(mradius);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_places, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
        return super.onOptionsItemSelected(item);
    }

    private void onAction_refresh()
    {
        startLocation();
        Toast.makeText(getActivity().getBaseContext(),"Start Location",Toast.LENGTH_SHORT).show();
    }

    private void onAction_whereami()
    {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("placeswheremaidialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        PlacesStatusFragment newFragment = PlacesStatusFragment.newInstance(mLastLocation);
        newFragment.show(getActivity().getSupportFragmentManager(), "placeswheremaidialog");
    }

    private void onAction_search() {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        DialogFragment prev = (DialogFragment)getActivity().getSupportFragmentManager().findFragmentByTag("PlacesRadiusFragment");
        if (prev != null) {
            prev.show(ft,"PlacesRadiusFragment");
        }else
        {
            PlacesRadiusFragment newFragment = PlacesRadiusFragment.newInstance(mradius);
            newFragment.show(getActivity().getSupportFragmentManager(), "PlacesRadiusFragment");
            newFragment.setListner(new PlacesRadiusListner() {
                @Override
                public void onSelected(String radius) {
                    loadPlaces(radius);
                }
            });

        }
    }

    private void startLocation() {
        provider = new LocationGooglePlayServicesProvider();
        SmartLocation smartLocation = new SmartLocation.Builder(getActivity()).logging(true).build();
        smartLocation.location().provider(provider).start(this);
    }


    private void stopLocation() {
        SmartLocation.with(getActivity()).location().stop();
        //Toast.makeText(getActivity().getBaseContext(), "Stop Location", Toast.LENGTH_SHORT).show();
    }


    private class PlacesListAdapter extends ArrayAdapter<PlacesModel> {

        public PlacesListAdapter(Context context, List<PlacesModel> list) {
            super(context, R.layout.fragment_places_row, list);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if( convertView == null ){
                //We must create a View:
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_places_row, container, false);
            }
            PlacesModel m = getItem(position);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView address = (TextView)convertView.findViewById(R.id.address);
            ImageView img = (ImageView)convertView.findViewById(R.id.list_image);
            name.setText(m.getName() );
            address.setText( m.getVicinity());
            mImageFetcher.loadImage(m.getIcon(), img);
            return convertView;
        }
    }

    private void loadPlaces(String radius)
    {
        //String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=51.9709541,8.7184133&radius=500&types=food";
        progressBar.setVisibility(View.VISIBLE);
        listview.removeAllViewsInLayout();
        if(radius !=null)
        {
            mradius = radius;
        }else
        {
            radius = mradius;
        }
        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=";
        googlePlacesUrl = googlePlacesUrl + String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude()) + "&radius="+radius+"&types=food";
        HTTPRequest request = new HTTPRequest(googlePlacesUrl,true,true);
        request.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {
                try {
                    placeslist.clear();
                    if(response.getString("status").equals("OK")) {
                        JSONArray entries = response.getJSONArray("results");
                        for (int i = 0; i < entries.length(); ++i) {
                            JSONObject o = entries.getJSONObject(i);
                            PlacesModel m = PlacesModel.fromJSON(o);
                            if (m != null) placeslist.add(m);
                        }

                        mAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        stopLocation();

                    }else
                    {
                        Toast.makeText(getActivity(),"No Results!",Toast.LENGTH_LONG).show();
                        mAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        stopLocation();
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
