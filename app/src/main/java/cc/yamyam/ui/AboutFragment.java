package cc.yamyam.ui;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.yamyam.BaseFragment;
import cc.yamyam.R;
import cc.yamyam.Utils;
import cc.yamyam.general.Constants;

/**
 * Created by siyuan on 17.08.15.
 */
public class AboutFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_about, container, false);
        TextView t = (TextView)view.findViewById(R.id.about_text);
        String txt="";
        String user_id = Utils.getGlobalValue(getActivity(), Constants.USER_ID);
        if(user_id!=null){
            String nickname = Utils.getGlobalValue(getActivity(),Constants.USER_NICKNAME);
            txt +="\n Welcome "+nickname+ " [ "+ user_id+" ]";
        }
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0);
            txt += "\n Version Name : "+ info.versionName +"\n Version code:"+info.versionCode;
        }catch (Exception e)
        {

        }



        t.setText(txt);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
}
