package cc.yamyam.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cc.yamyam.R;


public class PlacesRadiusFragment extends DialogFragment implements DialogInterface.OnKeyListener {


    //private static String mradius;
    private ListView list;
    private String radius;
    private PlacesRadiusListner listner;

    // TODO: Rename and change types and number of parameters
    public static PlacesRadiusFragment newInstance(String radius) {
        PlacesRadiusFragment fragment = new PlacesRadiusFragment();
        Bundle b = new Bundle();
        b.putString("radius",radius);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        radius = getArguments().getString("radius");
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_places_radius, null);
        list = (ListView)dialogView.findViewById(R.id.list);
        ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.radius_array, android.R.layout.select_dialog_singlechoice);
        list.setAdapter(listAdapter);
        if(radius!=null){
            int ps = Integer.parseInt(radius) /100;
            list.setSelection(ps - 1);
            list.setItemChecked(ps - 1, true);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                radius = list.getItemAtPosition(pos).toString();
                getDialog().dismiss();
                listner.onSelected(radius);

            }

        });

        builder.setOnKeyListener(this);
        builder.setTitle("Radius in Meters");
        builder.setView(dialogView);

        return builder.create();


    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            getDialog().dismiss();
        }

        return false;
    }

    public void setListner(PlacesRadiusListner listner) {
        this.listner = listner;
    }

    public ListView getList() {
        return list;
    }}
