package cc.yamyam;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.yamyam.api.HTTPRequest;
import cc.yamyam.api.HTTPRequestListener;
import cc.yamyam.general.Constants;
import cc.yamyam.model.PhotoModel;
import cc.yamyam.model.PlacesModel;

/**
 * Created by siyuan on 01.08.15.
 */
public class Utils {
    public static String getDeviceID(Context context) {
        TelephonyManager tManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    public static String getAndroidID(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        return Settings.Secure.getString(cResolver, Settings.Secure.ANDROID_ID);
    }

    public static String getUniqueID(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            return getAndroidID(context);
        else
            return getDeviceID(context);
    }

    public static int getPhotoRotation(String path) throws IOException {
        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        );
        return orientation;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        public static Bitmap scaleImage(String path, int targetW, int targetH) {

            if (targetW == 0 || targetH == 0)
                return null;

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            return BitmapFactory.decodeFile(path, bmOptions);
        }
    */
    public static Bitmap scaleImage(String path, int targetW, int targetH) {

        if (targetW == 0 || targetH == 0)
            return null;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        int scaleFactor = calculateInSampleSize(bmOptions, targetW, targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    // private static final int RANDOM_STRING_LENGTH = 10;


    /**
     * This method generates random string
     *
     * @return public static String generateRandomString() {
     * <p/>
     * StringBuffer randStr = new StringBuffer();
     * for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
     * int number = getRandomNumber();
     * char ch = CHAR_LIST.charAt(number);
     * randStr.append(ch);
     * }
     * return randStr.toString();
     * }
     */
    public static String getFilename() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "yamyam_" + timeStamp;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * This method generates random numbers
     *
     * @return int
     * <p/>
     * private static int getRandomNumber() {
     * int randomInt = 0;
     * Random randomGenerator = new Random();
     * randomInt = randomGenerator.nextInt(CHAR_LIST.length());
     * if (randomInt - 1 == -1) {
     * return randomInt;
     * } else {
     * return randomInt - 1;
     * }
     * }
     */
    /*
    public static String savePhotoSmall(String photopath, int targetW, int targetH){
        Bitmap bitmap = Utils.scaleImage(photopath,targetW,targetH);
        return saveBitmap(bitmap);
    }*/
    public static String saveBitmap(Bitmap bitmap, String mCurrentPhotoPath) {
        try {
            File directory = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    ), Constants.DIR_FOR_PHOTO_UPLOAD
            );
            if (!directory.exists())
                directory.mkdir();
            // String filename = Utils.getFilename();
            String[] tmps = mCurrentPhotoPath.split("/");
            String filename = tmps[tmps.length - 1];

            String mFilePath = directory.getAbsolutePath() + "/" + filename;
            FileOutputStream stream = new FileOutputStream(mFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            return mFilePath;
        } catch (Exception e) {
            Log.e("Could not save", e.toString());
        }
        return null;
    }


    public List<PlacesModel> getNearbyplaces(String latitude, String longitude, int meters, String type) {
        List<PlacesModel> list = new ArrayList<PlacesModel>();
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=51.9709541,8.7184133&radius=500&types=food
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCiCe0cEcCcUisrmtnaOfP_dHrTNoBpIzU&location=";
        url = url + latitude + "," + longitude + "&radius=" + meters + "&types=" + type;
        HTTPRequest places = new HTTPRequest(url);
        places.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {
                try {
                    if (response.getString("status").equals("OK")) {
                        //phototmpmodel.populateOptionsFromJSON(response.getJSONArray("data"));


                    }
                } catch (Exception e) {
                    //Utils.log(e);
                }
            }

            @Override
            public void onHTTPRequestFailed(int tag) {
// TODO Auto-generated method stub
                //Utils.log("request fail");
            }
        });

        return list;
    }

    public static int getResourseId(Context c, String var, String res) {
        try {
            return c.getResources().getIdentifier(var, res, c.getPackageName());
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getStringByKey(Context c, String key) {


        int id = getResourseId(c, key, "string");
        if (id != -1) {
            try {
                return c.getString(id);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }

    }

    public static int getUserIdFromPreference(Context context) {
        String userid = getGlobalValue(context, Constants.USER_ID);
        if (userid == null) return 0;
        else return Integer.parseInt(userid);
    }

    public static void writeUserIdToPreference(Context context, String user_id) {
        setGlobalValue(context, Constants.USER_ID, user_id);
    }


    public static void setGlobalValue(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.preference_file_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getGlobalValue(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.preference_file_key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }


    // write json to file
    /*
    public static void writeJsonPhotoModel(OutputStream out, PhotoModel photoModel) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writePhotoModel(writer,photoModel);
        writer.close();
    }
    */

    public static void writeJsonStream(OutputStream out, ArrayList<PhotoModel> photoModel) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writePhotoModelArray(writer, photoModel);
        writer.close();
    }

    public static void writePhotoModelArray(JsonWriter writer, ArrayList<PhotoModel> photoModel) throws IOException {
        writer.beginArray();
        for (PhotoModel p : photoModel) {
            writePhotoModel(writer, p);
        }
        writer.endArray();
    }

    public static void writePlacesModelArray(JsonWriter writer, List<PlacesModel> places) throws IOException {
        writer.beginArray();
        for (PlacesModel p : places) {
            writePlaceModel(writer, p);
        }
        writer.endArray();
    }


    public static void writePlaceModel(JsonWriter writer, PlacesModel p) throws IOException {
        writer.beginObject();
        writer.name("id").value(p.getId());
        writer.name("name").value(p.getName());
        writer.name("place_id").value(p.getPlace_id());
        writer.name("vicinity").value(p.getVicinity());
      //  writer.name("latitude").value(p.getLatitude());
      //  writer.name("longitude").value(p.getLongitude());
        writer.name("reference").value(p.getReference());
        writer.endObject();
    }

    public static void writePhotoModel(JsonWriter writer, PhotoModel p) throws IOException {
        writer.beginObject();
        writer.name("pathAbsolute").value(p.getPathAbsolute());
        writer.name("photoforupload").value(p.getPhotoforupload());
        if (p.getLatitude() != null) {
            writer.name("latitude").value(p.getLatitude());
        } else {
            writer.name("latitude").nullValue();
        }
        if (p.getLongitude() != null) {
            writer.name("longitude").value(p.getLongitude());
        } else {
            writer.name("longitude").nullValue();
        }
        if (p.getNote() != null) {
            writer.name("note").value(p.getNote());
        }
        if (p.getIdshop() != null) {
            writer.name("idshop").value(p.getIdshop());
        }
        if(p.getNearbyplaces()!=null)
        {
            writer.name("places");
            writePlacesModelArray(writer,p.getNearbyplaces());
        }

        writer.endObject();
    }


    public static void readJsonStream(InputStream in,ArrayList<PhotoModel> list) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readPhotoModelArray(reader,list);
        }
        finally{
            reader.close();
        }
    }

    public static ArrayList<PhotoModel> readPhotoModelArray(JsonReader reader,ArrayList<PhotoModel> list) throws IOException {
        //ArrayList<PhotoModel> p= new ArrayList<PhotoModel>();
        reader.beginArray();
        while (reader.hasNext()) {
           // p.add(readPhotoModel(reader));
            PhotoModel m = readPhotoModel(reader);
            list.add(m);
            Log.i("readPhotoModelArray",m.getPathAbsolute());
        }
        reader.endArray();
        return list;
    }

    public static ArrayList<PlacesModel> readPlacesModelArray(JsonReader reader,ArrayList<PlacesModel> list) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            PlacesModel m = readPlacesModel(reader);
            list.add(m);
        }
        reader.endArray();
        return list;
    }


    public static PlacesModel readPlacesModel(JsonReader reader) throws IOException {
        reader.beginObject();
        PlacesModel m = new PlacesModel();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                m.setId(reader.nextString());
            }else if (name.equals("name")) {
                m.setName(reader.nextString());
            } else if (name.equals("place_id")) {
                m.setPlace_id(reader.nextString());
            } else if (name.equals("vicinity")) {
                m.setVicinity(reader.nextString());
            }
            else if (name.equals("reference")) {
                m.setReference(reader.nextString());
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return m;
    }

    public static PhotoModel readPhotoModel(JsonReader reader) throws IOException {
        reader.beginObject();
        PhotoModel m = new PhotoModel();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("pathAbsolute")) {
                m.setPathAbsolute(reader.nextString());
            }else if (name.equals("photoforupload")) {
                m.setPhotoforupload(reader.nextString());
            } else if (name.equals("latitude")) {
                m.setLatitude(reader.nextString());
            } else if (name.equals("longitude")) {
                m.setLongitude(reader.nextString());
            } else if (name.equals("note")) {
                m.setNote(reader.nextString());
            }else if (name.equals("idshop")) {
                m.setIdshop(reader.nextString());
            }else if (name.equals("places")) {
                ArrayList<PlacesModel> pl = new ArrayList<>();
                readPlacesModelArray(reader,pl);
                m.setNearbyplaces(pl);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return m;
    }

    public static Bitmap getBitmapForScreen(Context context,String imgPath){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displaymetrics);
        int targetW = displaymetrics.widthPixels/3;
        int targetH = displaymetrics.heightPixels/4;
        Bitmap bitmap = scaleImage(imgPath, targetW, targetH);

        // rotate the photo
        try {
            int rotation = getPhotoRotation(imgPath);
            bitmap = Utils.rotateBitmap(bitmap, rotation);
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e("yamyam","getBitmapForScreen");
        }
        return bitmap;
    }
}

