package cc.yamyam.ui;

import android.app.Dialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import cc.yamyam.R;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;


public class PlacesStatusFragment extends DialogFragment {


    private  static Location mlocation;
    private TextView status;

    public static PlacesStatusFragment newInstance (Location location) {

        PlacesStatusFragment  dialog = new PlacesStatusFragment();
        mlocation = location;

        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_places_status, null);
        status = (TextView)dialogView.findViewById(R.id.status_text_place);
        showLocation(mlocation);
        builder.setTitle(getString(R.string.whereami));
        builder.setIcon(R.drawable.ic_action_location_found);
        builder.setView(dialogView);
        return builder.create();


    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places_status, container, false);
        status = (TextView)v.findViewById(R.id.status_text_place);
       showLocation(location);
        return v;
    }
    */

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            // status.setText(text);

            // We are going to get the address for the current position
            SmartLocation.with(getActivity()).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        status.setText(builder.toString());
                    }
                }
            });
        } else {
            status.setText("Null location");
        }
    }


}
