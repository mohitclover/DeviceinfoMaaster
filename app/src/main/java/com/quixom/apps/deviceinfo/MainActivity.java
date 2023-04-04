package com.quixom.apps.deviceinfo;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quixom.apps.deviceinfo.fragments.AboutUsFragment;
import com.quixom.apps.deviceinfo.fragments.AppsFragment;
import com.quixom.apps.deviceinfo.fragments.BatteryFragment;
import com.quixom.apps.deviceinfo.fragments.BlueToothFragment;
import com.quixom.apps.deviceinfo.fragments.CPUFragment;
import com.quixom.apps.deviceinfo.fragments.CameraFragment;
import com.quixom.apps.deviceinfo.fragments.DisplayFragment;
import com.quixom.apps.deviceinfo.fragments.HomeFragment;
import com.quixom.apps.deviceinfo.fragments.NetworkFragment;
import com.quixom.apps.deviceinfo.fragments.OSFragment;
import com.quixom.apps.deviceinfo.fragments.PhoneFeaturesFragment;
import com.quixom.apps.deviceinfo.fragments.SensorCategoryFragment;
import com.quixom.apps.deviceinfo.fragments.SimFragment;
import com.quixom.apps.deviceinfo.fragments.StorageFragment;
import com.quixom.apps.deviceinfo.models.DeviceInfo;
import com.quixom.apps.deviceinfo.utilities.BaseActivity;
import com.quixom.apps.deviceinfo.utilities.FragmentUtil;
import com.quixom.apps.deviceinfo.utilities.KeyUtil;
import com.quixom.apps.deviceinfo.utilities.Methods;
import com.quixom.apps.deviceinfo.utilities.RateUsApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    AnimationDrawable animationDrawable;

    public FragmentUtil fragmentUtil;
    public View navHeader;
    public int lastSelectedPosition = -1;

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentUtil = new FragmentUtil(MainActivity.this);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);

        // initializing navigation menu
        setUpNavigationView();
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        getAppsList();
        fragmentUtil.clearBackStackFragmets();
        fragmentUtil.replaceFragment(new HomeFragment(), false, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
    }

    /**
     * Setup navigation View. Manage click event of drawer items.
     */
    public void setUpNavigationView() {

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setItemIconTintList(null);


        View headerLayout = navigationView.getHeaderView(0);
        TextView tvDeviceName = headerLayout.findViewById(R.id.tv_device_name);
        TextView tvModelNumber = headerLayout.findViewById(R.id.tv_model_number);
        LinearLayout llNavHeaderParent = headerLayout.findViewById(R.id.ll_nav_header_parent);

        animationDrawable = (AnimationDrawable) llNavHeaderParent.getBackground();

        animationDrawable.setEnterFadeDuration(10000);
        animationDrawable.setExitFadeDuration(5000);

        animationDrawable.start();

        drawerlistener();
        tvDeviceName.setText("".concat(Build.BRAND));
        tvModelNumber.setText("".concat(Build.MODEL));

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_device:
                        navItemIndex = 0;
                        break;
                    case R.id.nav_os:
                        navItemIndex = 1;
                        break;
                    case R.id.nav_sensor:
                        navItemIndex = 2;
                        break;
                    case R.id.nav_cpu:
                        navItemIndex = 3;
                        break;
                    case R.id.nav_battery:
                        navItemIndex = 4;
                        break;
                    case R.id.nav_network:
                        navItemIndex = 5;
                        break;
                    case R.id.nav_sim:
                        navItemIndex = 6;
                        break;
                    case R.id.nav_camera:
                        navItemIndex = 7;
                        break;
                    case R.id.nav_storage:
                        navItemIndex = 8;
                        break;
                    case R.id.nav_bluetooth:
                        navItemIndex = 9;
                        break;
                    case R.id.nav_display:
                        navItemIndex = 10;
                        break;
                    case R.id.nav_features:
                        navItemIndex = 11;
                        break;
                    case R.id.nav_user_apps:
                        navItemIndex = 12;
                        break;
                    case R.id.nav_system_apps:
                        navItemIndex = 13;
                        break;
                    case R.id.nav_about_us:
                        navItemIndex = 14;
                        break;
                    case R.id.nav_share:
                        navItemIndex = 15;
                        break;
                    case R.id.nav_rate_us:
                        navItemIndex = 16;
                        break;
                    case R.id.nav_feedback:
                        navItemIndex = 17;
                        break;
                    case R.id.nav_connect_us:
                        navItemIndex = 18;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Manage container while the drawer slide.
     */
    public void drawerlistener() {
        try {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

                @SuppressLint("NewApi")
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    //super.onDrawerSlide(drawerView, slideOffset);

                    try {
                        float moveFactor = (drawerLayout.getWidth() * slideOffset);
                        int width = findViewById(R.id.navigationView).getWidth();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        } else {
                            TranslateAnimation anim = new TranslateAnimation(0f, moveFactor, 0f, 0f);
                            anim.setDuration(0);
                            anim.setFillAfter(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }

            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Show activated position which is selected.
     */
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (lastSelectedPosition == navItemIndex) {
            drawerLayout.closeDrawers();
            return;
        }

        lastSelectedPosition = navItemIndex;

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getFragmentFromDrawer();
                if (fragment != null) {
                    fragmentUtil.clearBackStackFragmets();
                    fragmentUtil.replaceFragment(fragment, false, true);
                }
            }
        }, 300);

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    /**
     * Get the fragment while need to push.
     *
     * @return: fragment
     */
    private Fragment getFragmentFromDrawer() {
        switch (navItemIndex) {
            case 0:
                return HomeFragment.getInstance(0);
            case 1:
                return new OSFragment();
            case 2:
                return new SensorCategoryFragment();
            case 3:
                return new CPUFragment();
            case 4:
                return new BatteryFragment();
            case 5:
                return new NetworkFragment();
            case 6:
                return new SimFragment();
            case 7:
                return new CameraFragment();
            case 8:
                return new StorageFragment();
            case 9:
                return new BlueToothFragment();
            case 10:
                return new DisplayFragment();
            case 11:
                return new PhoneFeaturesFragment();
            case 12:
                return AppsFragment.Companion.getInstance(KeyUtil.IS_USER_COME_FROM_USER_APPS);
            case 13:
                return AppsFragment.Companion.getInstance(KeyUtil.IS_USER_COME_FROM_SYSTEM_APPS);
            case 14:
                return new AboutUsFragment();
            case 15:
                Methods.sharing("https://play.google.com/store/apps/details?id=com.quixom.deviceinfo");
                break;
            case 16:
                RateUsApp.Companion.rateUsApp(MainActivity.this);
                break;
            case 17:
                return HomeFragment.getInstance(7);
            case 18:
                return HomeFragment.getInstance(7);
            case 19:
                return HomeFragment.getInstance(7);
            default:
                return new HomeFragment();
        }
        return null;
    }

    /**
     * Open drawer
     */
    public void openDrawer() {
        Methods.hideKeyboard(MainActivity.this);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Close drawer
     */
    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Manage drawer's visibility.
     */
    public void drawerdisable(boolean isenable) {
        if (isenable) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    /**
     * Method for get System installed and User installed apps
     * from the device.
     *
     * @return list of apps
     */

    public List<DeviceInfo> getAppsList() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();

        int flags = PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES;

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);

        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application
                Drawable icon = pm.getApplicationIcon(appInfo);
                deviceInfos.add(new DeviceInfo(1, icon, pm.getApplicationLabel(appInfo).toString(), appInfo.packageName));
            } else {
                // Installed by User
                Drawable icon = pm.getApplicationIcon(appInfo);
                deviceInfos.add(new DeviceInfo(2, icon, pm.getApplicationLabel(appInfo).toString(), appInfo.packageName));
            }
        }
        return deviceInfos;
    }
}
