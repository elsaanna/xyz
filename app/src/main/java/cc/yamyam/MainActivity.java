package cc.yamyam;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cc.yamyam.adapter.MenuListViewAdapter;
import cc.yamyam.general.Constants;
import cc.yamyam.model.MenuEntry;
import cc.yamyam.ui.AboutFragment;
import cc.yamyam.ui.ExitActivity;
import cc.yamyam.ui.ImageGridFragment;
import cc.yamyam.ui.LocatorActivity;
import cc.yamyam.ui.MyrestFragment;
import cc.yamyam.ui.PlacesFragment;
import cc.yamyam.ui.RegisterActivity;


public class MainActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<MenuEntry> menuEntries = new ArrayList<MenuEntry>();
    private static final String TAG = "MainActivity";
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    public  static String userid=null;
    public  static String username=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userid = Utils.getGlobalValue(this, Constants.USER_ID);
        username = Utils.getGlobalValue(this, Constants.USER_NICKNAME);

        if(userid==null)
        {
            Intent i = new Intent(this,RegisterActivity.class);
            startActivity(i);
        }
        setContentView(R.layout.activity_main);
        initMenu();

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, new ImageGridFragment(), TAG);
            ft.commit();
        }

        mTitle = mDrawerTitle = getTitle();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_tasty);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);



    }

    public void initMenu(){
        menuEntries.add(new MenuEntry("takefoto", getString(R.string.menu_takefoto), R.drawable.ic_action_camera));
        //menuEntries.add(new MenuEntry("locator", getString(R.string.menu_locator), R.drawable.ic_action_about));
        menuEntries.add(new MenuEntry("gallery", getString(R.string.menu_gallery), R.drawable.ic_action_view_as_grid));
        menuEntries.add(new MenuEntry("myrest", getString(R.string.menu_myrest), R.drawable.ic_action_view_as_list));
        menuEntries.add(new MenuEntry("places", getString(R.string.menu_places), R.drawable.ic_action_view_as_list));
        menuEntries.add(new MenuEntry("about", getString(R.string.menu_about), R.drawable.ic_action_about));
        menuEntries.add(new MenuEntry("quit", getString(R.string.menu_quit), R.drawable.ic_action_remove));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setAdapter(new MenuListViewAdapter(this, menuEntries));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);



    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        drawerLayout.closeDrawer(drawerList);
        MenuEntry me = menuEntries.get(position);
        setTitle(me.getLabel());
        if(me.getId().equals("about")) {
            Fragment f = new AboutFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        }else if(me.getId().equals("gallery")) {
            Fragment f = new ImageGridFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        }
        else if(me.getId().equals("places")) {
            Fragment f = new PlacesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        }
        else if(me.getId().equals("takefoto")) {
            Fragment f = new MainActivityFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        }
        else if(me.getId().equals("locator")) {
            Intent i = new Intent(this, LocatorActivity.class);
            startActivity(i);
        }
        else if(me.getId().equals("myrest")) {
            Fragment f = new MyrestFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        }
        else if(me.getId().equals("quit")) {
            this.finish();
            super.onDestroy();
            ExitActivity.exitApplication(this);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        /*
        if(id == R.id.action_exit){
            onExitApp();
            return true;
        }
        if(id == R.id.action_register){
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
            return true;
        }
            */

        return super.onOptionsItemSelected(item);
    }

    private void onExitApp(){

        this.finish();
        super.onDestroy();
        /*
        android.os.Process.killProcess(android.os.Process.myPid());
        ActivityManager mActivityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses(getApplicationContext().getPackageName());
        System.exit(0);
         */
        ExitActivity.exitApplication(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        if(menu.findItem(R.id.action_camera)!=null) {
            menu.findItem(R.id.action_camera).setVisible(!drawerOpen);
            menu.findItem(R.id.action_publish).setVisible(!drawerOpen);
            menu.findItem(R.id.action_remove).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }


}
