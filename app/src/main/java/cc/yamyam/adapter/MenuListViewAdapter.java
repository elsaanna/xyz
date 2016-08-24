package cc.yamyam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.yamyam.R;
import cc.yamyam.model.MenuEntry;

/**
 * Created by siyuan on 17.08.15.
 */
public class MenuListViewAdapter extends ArrayAdapter<MenuEntry> {

    private final Context context;
    private final ArrayList<MenuEntry> values;

    public MenuListViewAdapter(Context context, ArrayList<MenuEntry> values) {
        super(context, R.layout.menu_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.menu_list_item, parent, false);

        MenuEntry m = values.get(position);

        ((TextView)rowView.findViewById(R.id.label)).setText(m.getLabel());
        ((ImageView)rowView.findViewById(R.id.icon)).setImageResource(m.getIcon());

        return rowView;
    }

}

