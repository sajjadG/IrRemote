package com.nullpx.irremote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MainActivity class handling the main page things
 * main function is here
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainBtnFragment.OnFragmentInteractionListener, NumBtnFragment.OnFragmentInteractionListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final String TAG = "IrRemote";
    private static final int SELECT_DEVICE_REQUEST = 100;
    private IrTransmitter irTransmitter;
    private ShowcaseView showcaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check and see if device has IR Transmitter if not show the warning and exit/disable the buttons
        //show banner too. snack bar is cool though
        irTest();// TODO: 2/4/17 make it better

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each section of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
//        springIndicator.setViewPager(mViewPager);

//        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
//        inkPageIndicator.setViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);
//        drawer.closeDrawer(GravityCompat.START);

        //load previous models
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Configs.SHARED_PREF, 0);
        Set<String> navMenuItems = sharedPreferences.getStringSet(Configs.SHARED_PREF_KEYS.NAV_MENU_ITEMS.name(), null);
        String lastSelectedItem = sharedPreferences.getString("last_selected_item", "");
        Log.d(TAG, "Last selected item = " + lastSelectedItem);

        if (navMenuItems != null) {
            for (String item : navMenuItems) {
                MenuItem menuItem = addItemToNavMenu(navigationView.getMenu(), item);
                if (menuItem.getTitle().toString().equals(lastSelectedItem)) {
                    onNavigationItemSelected(menuItem);
                }
            }
        }

        if (lastSelectedItem.equals("")) {
            showTuts();
        }
    }

    /**
     * TODO: Migrate to
     * https://github.com/wooplr/Spotlight
     * or
     * https://github.com/deano2390/MaterialShowcaseView
     */
    private void showTuts() {
//        ShowcaseView.Builder res = new ShowcaseView.Builder(this)
////                .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
//                .setTarget(new ViewTarget(((Toolbar) findViewById(R.id.toolbar)).getChildAt(0)))
//                .setContentTitle("title")
//                .setContentText("content");

//        new ShowcaseView.Builder(getActivity())
//                .setTarget( new ViewTarget( ((View) parentView.findViewById(R.id.link_to_register)) ) )
//                .setContentTitle("ShowcaseView")
//                .setContentText("This is highlighting the Home button")
//                .hideOnTouchOutside()
//                .build();

//        Target target = new ActionViewTarget(new AboutActivity(), ActionViewTarget.Type.HOME);

        Button closeButton = new Button(this);
        closeButton.setBackgroundColor(200);
        closeButton.setEnabled(false);

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new PointTarget(330, 570))
//                .setTarget(new ViewTarget(((Toolbar) findViewById(R.id.toolbar)).getChildAt(0)))//works
                .setContentTitle("Add a new device")
                .setContentText("You need to add a new device to be able to use this app")
//                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme2)
                .replaceEndButton(closeButton)
                .build();
    }

    private MenuItem addItemToNavMenu(Menu menu, String model) {

        menu.removeItem(R.id.nav_add_new_device);
        menu.removeItem(R.id.nav_clear_device_list);
        MenuItem item = menu.add(R.id.group_saved_device, Menu.NONE, Menu.NONE, model);
        menu.add(0, R.id.nav_add_new_device, 0, R.string.add_new_device).setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, R.id.nav_clear_device_list, 0, R.string.clear_device_list).setIcon(android.R.drawable.ic_menu_delete);
//            MenuItem add = navigationView.getMenu().add(model);
        String[] split = model.split(" - ");
        switch (split[0]) {
            case "TV":
                item.setIcon(R.drawable.ic_live_tv_black_24dp);
                break;
            case "AC":
                item.setIcon(R.drawable.ic_menu_send);
                break;
            case "Fan":
                item.setIcon(R.drawable.ic_menu_gallery);
                break;
            case "Receiver":
                item.setIcon(R.drawable.ic_invert_colors_black_24dp);
                break;
        }

        if (split[1].equals("LG")) {
        }

        item.setCheckable(true);
        return item;
    }

    private void irTest() {

        try {
            ConsumerIrManager consumerIrManager
                    = (ConsumerIrManager) getApplicationContext().getSystemService(CONSUMER_IR_SERVICE);

            if (consumerIrManager.hasIrEmitter()) {
                Toast.makeText(this, "Device has IR transmitter", Toast.LENGTH_LONG).show();
                Log.i(TAG, " Device has IR ");
                ConsumerIrManager.CarrierFrequencyRange[] carrierFrequencies = consumerIrManager.getCarrierFrequencies();
                for (ConsumerIrManager.CarrierFrequencyRange frequencyRange : carrierFrequencies) {
                    Log.i(TAG, frequencyRange.getMaxFrequency() + " " + frequencyRange.getMinFrequency());
                }
            } else {
                Toast.makeText(this, "Device does not have IR transmitter", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(this, "Error detecting IR Transmitter", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_feedback) {
            startActivity(new Intent(this, FeedbackActivity.class));
        } else if (id == R.id.action_exit) {
            exit();
        }

        return super.onOptionsItemSelected(item);
    }

    private void exit() {
        // TODO: 2/4/17 Show are you sure dialog . or not
        System.exit(2);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_new_device) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, DeviceSelectionActivity.class);

            //retrieve device list we already have
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getItemId() != R.id.nav_add_new_device && menu.getItem(i).getItemId() != R.id.nav_clear_device_list) {
                    list.add(menu.getItem(i).toString().split(" - ")[1]);
                }
            }
            if (!list.isEmpty()) {
                intent.putStringArrayListExtra("device_list", list);
            }
            startActivityForResult(intent, SELECT_DEVICE_REQUEST);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_clear_device_list) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.clear();
            SharedPreferences.Editor edit = getSharedPreferences(Configs.SHARED_PREF, 0).edit();
            edit.putStringSet(Configs.SHARED_PREF_KEYS.NAV_MENU_ITEMS.name(), new HashSet<String>());
            edit.putString("last_selected_item", "");
            edit.apply();

            menu.add(0, R.id.nav_add_new_device, 0, R.string.add_new_device).setIcon(android.R.drawable.ic_menu_add);
            menu.add(0, R.id.nav_clear_device_list, 0, R.string.clear_device_list).setIcon(android.R.drawable.ic_menu_delete);

            navigationView.getMenu().findItem(R.id.nav_clear_device_list).setEnabled(false);
//            findViewById(R.id.nav_clear_device_list).setEnabled(false);//error due to not syncing
//            item.setEnabled(false)//error due to not syncing

            showTuts();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);

        } else {

            Log.d(TAG, item.getTitle() + " selected");

            //change nav header texts and icon
            String[] split = item.getTitle().toString().split(" - ");

            //reload transmitter with the signals of the selected item
            reloadTransmitter(split[0], split[1]);

            //set nav header
            try {
                View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
                TextView tvNavHeaderManufacturer = (TextView) header.findViewById(R.id.nav_header_tv_manufactorer);
                tvNavHeaderManufacturer.setText(split[0]);
                TextView tvNavHeaderModel = (TextView) header.findViewById(R.id.nav_header_tv_model);
                tvNavHeaderModel.setText(split[1]);
                ImageView ivNavHeader = (ImageView) header.findViewById(R.id.nav_header_manufacturer_pic);
                ivNavHeader.setImageDrawable(item.getIcon());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //select the clicked item
            item.setChecked(true);

            //save selected item
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(Configs.SHARED_PREF, 0).edit();
            sharedPreferences.putString("last_selected_item", item.getTitle().toString());
            sharedPreferences.apply();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        SharedPreferences sharedPreferences = getSharedPreferences(Configs.SHARED_PREF, 0);
//        String navNewMenuItem = sharedPreferences.getString(SHARED_PREF_KEYS.NAV_MENU_ITEMS.name(), null);
//        if (navNewMenuItem != null) {
//            menu.add(navNewMenuItem);
//            Log.d(TAG, "new item added to nav menu " + navNewMenuItem);
//        } else {
//            Log.e(TAG, "we have no menu!");
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_DEVICE_REQUEST) {
            if (resultCode == RESULT_OK) {

                if (showcaseView != null && showcaseView.isShowing()) {
                    showcaseView.hide();
                }

                //get added item
                String device = data.getStringExtra("device");
                String brand = data.getStringExtra("brand");
                Log.d(TAG, "new item selected " + device + " " + brand + " ");

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
                MenuItem item = addItemToNavMenu(menu, device + " - " + brand);

                onNavigationItemSelected(item);

                //save the new item to shared pref
                LinkedHashSet<String> models = new LinkedHashSet<>();
                for (int i = 0; i < menu.size(); i++) {
                    if (menu.getItem(i).getItemId() != R.id.nav_add_new_device && menu.getItem(i).getItemId() != R.id.nav_clear_device_list)
                        models.add(menu.getItem(i).getTitle().toString());
                }
                SharedPreferences settings = getSharedPreferences(Configs.SHARED_PREF, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putStringSet(Configs.SHARED_PREF_KEYS.NAV_MENU_ITEMS.name(), models);

                // Commit the edits!get
                editor.apply();

                //load selected model signals
                reloadTransmitter(device, brand);
            } else {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
                if (menu.size() < 3) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                    showcaseView.show();
                }
            }

        }
    }

    private void reloadTransmitter(String device, String brand) {
        Log.d(TAG, "Transmitter reloaded for " + device + " " + brand);
        this.irTransmitter = new IrTransmitter(device, brand, getApplicationContext());
    }

    public void transmitIt(View view) {
        switch (view.getId()) {
            case R.id.btn_power:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.POWER.name());
                break;
            case R.id.btn_mute:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.MUTE.name());
                break;
            case R.id.btn_up_arrow:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.CH_UP.name());
                break;
            case R.id.btn_down_arrow:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.CH_DOWN.name());
                break;
            case R.id.btn_left_arrow:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.VOL_DOWN.name());
                break;
            case R.id.btn_right_arrow:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.VOL_UP.name());
                break;
            case R.id.btn_ok:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.OK.name());
                break;
            case R.id.btn_exit:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.EXIT.name());
                break;
            case R.id.btn_last:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.LAST.name());
                break;
            case R.id.btn_menu:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.MENU.name());
                break;
            case R.id.btn_back:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.BACK.name());
                break;
            case R.id.btn_num_0:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_0.name());
                break;
            case R.id.btn_num_1:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_1.name());
                break;
            case R.id.btn_num_2:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_2.name());
                break;
            case R.id.btn_num_3:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_3.name());
                break;
            case R.id.btn_num_4:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_4.name());
                break;
            case R.id.btn_num_5:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_5.name());
                break;
            case R.id.btn_num_6:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_6.name());
                break;
            case R.id.btn_num_7:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_7.name());
                break;
            case R.id.btn_num_8:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_8.name());
                break;
            case R.id.btn_num_9:
                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.NUM_9.name());
                break;

//            case R.id.btnNum0:
//                irTransmitter.transmit(IrTransmitter.IR_SIGNAL.BACK.name());
//                break;

            default:
                Log.d(TAG, "this button has no code yet");
                Toast.makeText(this, "This button has no code yet... Contact Developer plz", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO: you can use this to communicate with @NumBtnFragment and @MainBtnFragment
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MainBtnFragment.newInstance("str1", "str2");
                case 1:
                    return NumBtnFragment.newInstance("str1", "str2");
                default:
                    return MainBtnFragment.newInstance("str1", "str2");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience
            Drawable myDrawable = getDrawable(R.drawable.selected_dot);
            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            switch (position) {
                case 0:
                    return "Main";
                case 1:
//                    return sb;
                    return "Extra";
                case 2:
                    return "SECTION 3";
            }
            return "SECTION " + position;
        }
    }

}
