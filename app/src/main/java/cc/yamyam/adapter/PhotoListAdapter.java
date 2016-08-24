package cc.yamyam.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

import cc.yamyam.R;
import cc.yamyam.Utils;
import cc.yamyam.model.PhotoModel;
import cc.yamyam.model.PlacesModel;

/**
 * Created by siyuan on 14.08.15.
 */
public class PhotoListAdapter extends ArrayAdapter<PhotoModel> {


    public PhotoListAdapter(Activity context,
                            ArrayList<PhotoModel> ptm) {
        super(context, R.layout.list_single, ptm);

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());;
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        final PhotoModel t = getItem(position);
        CheckBox checkbox_selected = (CheckBox) rowView.findViewById(R.id.checkbox_selected);
        checkbox_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c =(CheckBox) v;
                t.setChecked(c.isChecked());
            }
        });

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        if(t.getBitmap()==null) t.setBitmap( Utils.getBitmapForScreen(getContext(), t.getPathAbsolute()));
        imageView.setImageBitmap(t.getBitmap());

        final Spinner spinner = (Spinner) rowView.findViewById(R.id.note_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.note_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] arraynote = view.getResources().getStringArray(R.array.note_array);
                t.setNote(arraynote[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // init note
        if (t.getNote() != null) {
            int n = 0;
            int imax = adapter.getCount();
            String[] notes = getContext().getResources().getStringArray(R.array.note_array);
            for (int i = 0; i < imax; i++) {
                if (t.getNote().equals(notes[i])) {
                    n = i;
                    break;
                }
            }
            spinner.setSelection(n);
        }

        final Spinner spinnerplaces = (Spinner) rowView.findViewById(R.id.places_spinner);

        final ArrayAdapter<PlacesModel> adpplaces = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, t.getNearbyplaces());
        adpplaces.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerplaces.setAdapter(adpplaces);
        spinnerplaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                t.setIdshop(t.getNearbyplaces().get(position).getPlace_id());
                t.setShopReference(t.getNearbyplaces().get(position).getReference());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // init shop id
        int postmp = 0;
        if(t.getIdshop()!=null && t.getNearbyplaces()!=null)
        {
            int imax = t.getNearbyplaces().size();
            for (int i = 0; i < imax; i++) {
                    if (t.getNearbyplaces().get(i).getPlace_id().equals(t.getIdshop())) {
                        postmp = i;
                        break;
                    }
                }
            spinnerplaces.setSelection(postmp);
        }
        return rowView;
    }


    /*
    public void addItem(PhotoModel item) {

        ArrayList<PhotoModel> l = new ArrayList();
        l.add(item);
        for (int i = 0; i < ptm.length; i++) {
            l.add(ptm[i]);
        }
        ptm = l.toArray(ptm);
    }

    @Override
    public int getCount() {
        return ptm.length;
    }

    public static PhotoModel[] removeElements(PhotoModel[] input, PhotoModel deleteMe) {
        List result = new LinkedList();

        for (PhotoModel item : input)
            if (!deleteMe.getPathAbsolute().equals(item.getPathAbsolute()))
                result.add(item);

        return (PhotoModel[]) result.toArray(input);
    }
    */
}
