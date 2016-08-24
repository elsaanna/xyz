package cc.yamyam;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cc.yamyam.model.PhotoModel;

/**
 * Created by siyuan on 03.08.15.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<PhotoModel> l;
    public ImageAdapter(Context c, List<PhotoModel> list) {
        mContext = c;
        l = list;
    }

    public int getCount() {
        return l.size();
    }

    public Object getItem(int position) {
        return l.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(l[position]);
        imageView.setImageBitmap(l.get(position).getBitmap());
        return imageView;
    }

    public void addItem(PhotoModel item)
    {
        ArrayList<PhotoModel> l = new ArrayList();
        l.add(item);
    }

}
