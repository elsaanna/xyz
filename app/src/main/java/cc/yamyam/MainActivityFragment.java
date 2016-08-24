package cc.yamyam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.yamyam.adapter.PhotoListAdapter;
import cc.yamyam.api.HTTPRequest;
import cc.yamyam.api.HTTPRequestListener;
import cc.yamyam.general.Constants;
import cc.yamyam.model.PhotoModel;
import cc.yamyam.model.PlacesModel;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    //private final static String GOOGLEAPIKEY = "AIzaSyCMChHt223-uR6mZT6BEo70h6ZPOFZKsHE";
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=51.9709541,8.7184133&radius=500&types=food
    private MenuItem menuItem_takephoto;
    private MenuItem menuItem_publish;

    protected Location mLastLocation;

    protected Location mCurrentLocation = null;

    //private Locator locator;
    private List<PlacesModel> listplaces;
    private static FrameLayout mBusyLayout;
    private static ImageView mLoadingImage;
   // protected TextView mStatusText;
    private static ProgressBar mLoadingBar;

    public static final String TAG = PhotoApp.TAG + "|MainActFragment";
    private boolean isUI_busy = false;
    private int user_id=0;
    private final static int CAMERA_ACTION = 0;
    private final static String JPEG_FILE_SUFFIX = ".jpg";
    private RotateAnimation spin;
    private ArrayList<PhotoModel> listPhotos = new ArrayList();
    private String mCurrentPhotoPath;
    private File tmpfile;

    private ListView list;
    //private GridView gridview;
    private LocationGooglePlayServicesProvider provider;
    private TextView locationText;
    private TextView activityText;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        user_id = Utils.getUserIdFromPreference(getActivity());
        View V =inflater.inflate(R.layout.fragment_main, container, false);

        //mStatusText = (TextView) V.findViewById(R.id.status_text);

        mBusyLayout = (FrameLayout) V.findViewById(R.id.busyLayout);
        mLoadingBar = (ProgressBar) V.findViewById(R.id.loadingBar);
        mLoadingImage = (ImageView) V.findViewById(R.id.loadingImage);

        setRetainInstance(true);
        spin = new RotateAnimation(
                0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        spin.setDuration(1000);
        spin.setRepeatCount(Animation.INFINITE);

        enableUi(true);
        initList(V);

        // Bind event clicks
        Button startLocation = (Button) V.findViewById(R.id.start_location);
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation();
            }
        });

        Button stopLocation = (Button) V.findViewById(R.id.stop_location);
        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocation();
            }
        });

        // bind textviews
        locationText = (TextView) V.findViewById(R.id.status_text);
        activityText = (TextView) V.findViewById(R.id.activity_text);

        // Keep the screen always on
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showLast();


        return V;
    }

    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        SmartLocation smartLocation = new SmartLocation.Builder(getActivity()).logging(true).build();
        smartLocation.location().provider(provider).start(this);
        smartLocation.activity().start(this);
        Log.i("yamyam", "startLocation");
        loadLastPlaces();

    }

    private void stopLocation() {
        SmartLocation.with(getActivity()).location().stop();
        locationText.setText("Location stopped!");

        SmartLocation.with(getActivity()).activity().stop();
        activityText.setText("Activity Recognition stopped!");

    }

    private void showLast() {
        Location lastLocation = SmartLocation.with(getActivity()).location().getLastLocation();
        if (lastLocation != null) {
            locationText.setText(
                    String.format("[From Cache] Latitude %.6f, Longitude %.6f",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude())
            );
        }

        DetectedActivity detectedActivity = SmartLocation.with(getActivity()).activity().getLastActivity();
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("[From Cache] Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        }
    }

    public void initList(View view){
        try {
            //printData();
            readPhotoModelArrayFromFile();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if(list==null ||list.getAdapter()==null) {
            PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), listPhotos);
            list = (ListView) view.findViewById(R.id.list);
            list.setAdapter(adapter);
        }

    }

    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);
        menuItem_takephoto = menu.findItem(R.id.action_camera);
        menuItem_publish = menu.findItem(R.id.action_publish);

        updateMenuPublish();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(mLastLocation!=null) menuItem_takephoto.setEnabled(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_ACTION:
                onCameraIntent(requestCode, resultCode, data);
                break;
            default:

        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i("yamyam", "onStop is called!!!!");
        // after stop save to file
        // stopLocation();
        try {
            writePhotoModelListToFile(Context.MODE_PRIVATE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //locator.stopUpdates();

    }

    @Override
    public void onResume() {
        super.onResume();

        //locator.startUpdates(true);

        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(false);
            Toast.makeText(getActivity(),"pls switch GPS on", Toast.LENGTH_LONG);
        }

        if(mLastLocation==null){
            if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(false);
        }else{
            if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(true);
        }
    }

    /*
    private void bindLocator() {
        locator = new Locator(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Locator.LOCATION_CHANGED) {
                    Location current = (Location) msg.obj;
                    if(Locator.isBetterLocation(mLastLocation,current))
                    {
                        mLastLocation = current;
                        updateUI();
                        loadLastPlaces();
                    }
                }
            }
        });
    }
    */

    private void loadLastPlaces()
    {

        if(mLastLocation!=null) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=";
            url = url + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&radius=500&types=food";
            HTTPRequest places = new HTTPRequest(url, true, true);
            places.setListener(new HTTPRequestListener() {
                @Override
                public void onHTTPRequestFinished(JSONObject response, int tag) {
                    try {
                        if (response.getString("status").equals("OK")) {
                            JSONArray array = response.getJSONArray("results");
                            try {
                                listplaces = new ArrayList<PlacesModel>();
                                for(int i=0;i<array.length();i++) {
                                    listplaces.add(PlacesModel.fromJSON(array.getJSONObject(i)));
                                }
                                menuItem_takephoto.setEnabled(true);
                            } catch(Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            Log.i(TAG, "loadLastPlaces" + array.length());
                        } else {
                            if("ZERO_RESULTS".equals(response.getString("status")))
                            {
                                listplaces = new ArrayList<PlacesModel>();
                                menuItem_takephoto.setEnabled(false);

                                locationText.setText(" No places founded 200 meters nearby ! \n");

                            }else
                            {
                                locationText.setText(response.getString("status"));
                            }
                        }
                    } catch (Exception e) {
                        //Utils.log(e);
                        Log.i(TAG, "loadLastPlaces:"+e.toString());
                        locationText.setText(e.toString());
                    }
                }

                @Override
                public void onHTTPRequestFailed(int tag) {
                    //Utils.log("request fail");
                    Log.i(TAG, "loadLastPlaces onHTTPRequestFailed" + tag);
                    locationText.setText("No Connection at the moment. Try it later."+tag);
                }
            });
            places.start();
        }
    }

    private void updateUI() {
        /*
        if(mLastLocation!=null)
        {
            if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(true);
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String status = "Lat/Lng: " + String.valueOf(mLastLocation.getLatitude()) + ", " +String.valueOf(mLastLocation.getLongitude());
            status = status +" "+f.format(mLastLocation.getTime());
            status = status +" Service:"+mLastLocation.getProvider()+ "Status["+locator.getStatus()+"|"+locator.getStatusGPS()+"]";
            mStatusText.setText(status);

            if(mLastLocation.getProvider()==LocationManager.GPS_PROVIDER && locator.getStatusGPS()==Locator.GPSSEARCHING)
            {
                if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(false);
            }
        }else
        {
           if(menuItem_takephoto!=null)menuItem_takephoto.setEnabled(false);
            mStatusText.setText("Pls switch GPS on");
        }
        */

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_publish:
                onAction_publish();
                return true;
            case R.id.action_camera:
                try {
                    dispatchTakePictureIntent(CAMERA_ACTION);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Got an error creating a tmp file",
                            Toast.LENGTH_LONG
                    ).show();
                }
                return true;
            case R.id.action_remove:
                onAction_remove();
                return true;
            case R.id.action_remove_file:
                onAction_remove_file();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

   private  void onAction_remove_file()
   {
       File dir = getActivity().getFilesDir();
       File file = new File(dir, Constants.FILE_DATA_NAME);

       boolean deleted = file.delete();
       if(deleted)Log.i("yamyam","deleted:"+file.getAbsolutePath());
       else Log.i("yamyam","deleted false:"+file.getAbsolutePath());
   }


    private void dispatchTakePictureIntent(int actionCode) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = createTmpFile();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(takePictureIntent, actionCode);
    }


    private File createTmpFile() throws IOException {
        // Create a tmp file name
        // Android's camera cannot write to the private app folder
        // So we better use external storage here.
        File directory = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ), Constants.DIR_FOR_PHOTO
        );


        if (!directory.exists())
            directory.mkdir();

        File image = File.createTempFile(
                Utils.getFilename(), JPEG_FILE_SUFFIX,
                directory
        );

        tmpfile = image;
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "Created tmp file: " + image.getAbsolutePath());
        return image;
    }


    private void readPhotoModelArrayFromFile()
    {

        try {
            FileInputStream fin = getActivity().openFileInput(Constants.FILE_DATA_NAME);
            Utils.readJsonStream(fin, this.listPhotos);
        }catch (Exception e)
        {
            //Log.e("readPhotoModelArrayFromFile","readPhotoModelArrayFromFile error");
            e.printStackTrace();
        }

    }

    private void printData()
    {
        try {
            int c;
            FileInputStream fin = getActivity().openFileInput(Constants.FILE_DATA_NAME);
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            Log.i("readFile",temp);
            fin.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    // Context.Mode_APPEND, Mode_private
    private void writePhotoModelListToFile(int mode) throws Exception{
        FileOutputStream fOut = getActivity().openFileOutput(Constants.FILE_DATA_NAME,mode);
        Utils.writeJsonStream(fOut, this.listPhotos);
        fOut.close();
    }

    private void onAction_remove()
    {
        if(list==null || list.getAdapter()==null || listPhotos.size()==0) return;
        ArrayList<PhotoModel> tmp= new ArrayList<PhotoModel>();
        PhotoListAdapter adp = (PhotoListAdapter)list.getAdapter();
        for(int i=0; i<listPhotos.size(); i++)
        {
            if (listPhotos.get(i).getChecked()) {
                tmp.add(listPhotos.get(i));
            }
        }

        for(int i=0; i<tmp.size(); i++)
        {
            adp.remove(tmp.get(i));
        }

        adp.notifyDataSetChanged();

        updateMenuPublish();
    }

    private void onAction_publish()
    {
        if(listPhotos.size()==0)
        {
            Toast.makeText(getActivity(),"Sorry, No photo to publish.",Toast.LENGTH_LONG);
            return;
        }
        String imagepath;

        for(int i=0,imax=listPhotos.size(); i<imax; i++) {
            enableUi(false);
            mLoadingBar.setProgress(0);
            //imagepath = listPhotos.get(i).getPathAbsolute();  // this is for big size photos
            imagepath = listPhotos.get(i).getPhotoforupload(); // this is for small size photos
            //Log.i(TAG, "image Path:" + listPhotos.get(i).getPathAbsolute());

            PhotoMapClient.postPhoto(getActivity(), Utils.getUniqueID(getActivity()),
                    //mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                    Double.valueOf(listPhotos.get(i).getLatitude()), Double.valueOf(listPhotos.get(i).getLongitude()),
                    imagepath,
                    listPhotos.get(i).getNote(),
                    listPhotos.get(i).getIdshop(),
                    listPhotos.get(i).getShopReference(),
                    String.valueOf(user_id),
                    new ProgressCallback() {
                        @Override
                        public void onProgress(long i, long i2) {
                            mLoadingBar.setProgress((int) i * 100 / (int) i2);
                        }
                    },
                    new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            if (e != null) {
                                Log.e(TAG, "Got an error, check adb");
                                e.printStackTrace();
                            } else {
                                try {
                                    JSONObject response = new JSONObject(s);
                                    String msg = response.getString("msg");
                                    Toast.makeText(
                                            getActivity(),
                                            "Server: " + msg,
                                            Toast.LENGTH_LONG
                                    ).show();

                                } catch (Exception jsonException) {
                                    Log.i(TAG, "Response is not json");
                                    Toast.makeText(
                                            getActivity(),
                                            "Invalid response from the server",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                            enableUi(true);
                        }
                    }
            );


        }

        list.removeAllViewsInLayout();
        final PhotoListAdapter adp = (PhotoListAdapter)list.getAdapter();
        adp.clear();

        File dir = getActivity().getFilesDir();
        File file = new File(dir, Constants.FILE_DATA_NAME);
        boolean deleted = file.delete();

        updateMenuPublish();

    }



    private void enableUi(boolean enable) {
        Log.i(TAG, "enableUI:" + enable);
        if (!enable) {
            mBusyLayout.setVisibility(View.VISIBLE);
            mLoadingImage.startAnimation(spin);

        } else {
            mLoadingImage.clearAnimation();
            mBusyLayout.setVisibility(View.GONE);
        }
        isUI_busy = !enable;
    }

    private void initListView_() {
        Log.i(TAG, "initListView size:" + listPhotos.size());
        if(list==null ||list.getAdapter()==null) {
            PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), listPhotos);
            list = (ListView) getView().findViewById(R.id.list);
            list.setAdapter(adapter);
        }
        enableUi(!isUI_busy);
    }

    private void onCameraIntent(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {
            case Activity.RESULT_OK:
                handleCameraPhoto();
                //mPhotoSend.setEnabled(true);
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(getActivity(),
                        "Cool! You *did* cancel your photo, are you happy now?",
                        Toast.LENGTH_SHORT
                ).show();
                Log.i(TAG, "User has canceled photo");
                break;
            default:
                Toast.makeText(getActivity(),
                        "System Failure :P",
                        Toast.LENGTH_SHORT
                ).show();
                Log.i(TAG, "Something went wrong with the camera");
        }
    }

    private void handleCameraPhoto(){
        Log.i(TAG, "Handling camera result");
        Bitmap bitmap = Utils.getBitmapForScreen(getActivity(),mCurrentPhotoPath);
        String photoforupload = Utils.saveBitmap(bitmap,mCurrentPhotoPath);
        //String photoforupload = Utils.savePhotoSmall(mCurrentPhotoPath,800,600);
        PhotoModel pm = new PhotoModel(this.mCurrentPhotoPath);
        pm.setPhotoforupload(photoforupload);
        pm.setLatitude(String.valueOf(mLastLocation.getLatitude()));
        pm.setLongitude(String.valueOf(mLastLocation.getLongitude()));
        pm.setNearbyplaces(this.listplaces);

        PhotoListAdapter adp = (PhotoListAdapter)list.getAdapter();
        adp.insert(pm,0);
        adp.notifyDataSetChanged();


        updateMenuPublish();

    }

    private void updateMenuPublish()
    {
        if(listPhotos.size()>0)
        {
            menuItem_publish.setEnabled(true);
        }else
        {
            menuItem_publish.setEnabled(false);
        }
    }

    private void restoreState(View V) {
        // If retain instance is set to true, there's no need to play with saved
        // instance state
        if (mCurrentPhotoPath != null) {
            handleCameraPhoto();
        }
        enableUi(!isUI_busy);
    }


    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        showActivity(detectedActivity);
    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
        mLastLocation = location;

        loadLastPlaces();
    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            locationText.setText(text);

            // We are going to get the address for the current position
            SmartLocation.with(getActivity()).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\n[Reverse Geocoding] ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));

                        if(listplaces!=null && listplaces.size()==0)
                        {
                            locationText.setText("No Places founded 200 meters nearby! \n"+builder.toString());
                        }else
                        {
                            locationText.setText(builder.toString());
                        }

                    }
                }
            });
        } else {
            locationText.setText("Null location");
        }
    }

    private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        } else {
            activityText.setText("Null activity");
        }
    }
}
