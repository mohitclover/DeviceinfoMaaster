package com.quixom.apps.deviceinfo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.quixom.apps.deviceinfo.R;
import com.quixom.apps.deviceinfo.utilities.Validation;
import com.quixom.apps.deviceinfo.waveview.WaveView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryFragment extends BaseFragment {

    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    WaveView progressBar;
    @BindView(R.id.tv_battery_type)
    TextView tvBatteryType;
    @BindView(R.id.tv_power_source)
    TextView tvPowerSource;
    @BindView(R.id.tv_battery_temperature)
    TextView tvBatteryTemperature;
    @BindView(R.id.tv_battery_voltage)
    TextView tvBatteryVoltage;
    @BindView(R.id.tv_battery_scale)
    TextView tvBatteryScale;
    @BindView(R.id.tv_battery_health)
    TextView tvBatteryHealth;
    @BindView(R.id.fab_battery_charging)
    FloatingActionButton fabBatteryCharging;
    @BindView(R.id.tv_battery_level)
    TextView tvBatteryLevel;

    int health;
    int icon_small;
    int level;
    int plugged;
    boolean present;
    int scale;
    int status;
    String technology;
    int temperature;
    int voltage;
    int deviceStatus;

    private BroadcastReceiver mBatLow = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fabBatteryCharging.setImageResource(R.drawable.ic_low_battery);
        }
    };

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);

            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);

            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);

            technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            temperature = (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10);

            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            try {
                getBatteryInfo();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* View view = inflater.inflate(R.layout.fragment_battery, container, false);
        unbinder = ButterKnife.bind(this, view);
*/

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.BatteryTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_battery, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark_green));
            window.setNavigationBarColor(getResources().getColor(R.color.dark_green));
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mActivity.registerReceiver(mBatInfoReceiver, filter);
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        mActivity.registerReceiver(mBatLow, filter2);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar();
    }

    private void getBatteryInfo() {

        if (fabBatteryCharging != null) {
            if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                fabBatteryCharging.setVisibility(View.VISIBLE);
                fabBatteryCharging.setImageResource(R.drawable.ic_battery);
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                fabBatteryCharging.setVisibility(View.GONE);
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL) {
                fabBatteryCharging.setVisibility(View.GONE);
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                fabBatteryCharging.setVisibility(View.GONE);
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                fabBatteryCharging.setVisibility(View.GONE);
            }
        }

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                try {
                    if (counter <= level) {
                        progressBar.setProgress(counter);
                        progressBar.postDelayed(this, 10000);
                        counter++;
                        handler.postDelayed(this, 20);
                    } else {
                        handler.removeCallbacks(this);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 20);
        tvBatteryTemperature.setText("".concat(String.valueOf(temperature)).concat(mActivity.getResources().getString(R.string.c_symbol)));

        if (Validation.isRequiredField(technology)) {
            tvBatteryType.setText("".concat(technology));
        }

        tvBatteryVoltage.setText("".concat(String.valueOf(voltage).concat("mV")));
        tvBatteryScale.setText("".concat(String.valueOf(scale)));
        tvBatteryLevel.setText("".concat(String.valueOf(level)).concat("%"));

        if (health == 1) {
            tvBatteryHealth.setText(mResources.getString(R.string.unknown));
        } else if (health == 2) {
            tvBatteryHealth.setText(mResources.getString(R.string.good));
        } else if (health == 3) {
            tvBatteryHealth.setText(mResources.getString(R.string.over_heated));
        } else if (health == 4) {
            tvBatteryHealth.setText(mResources.getString(R.string.dead));
        } else if (health == 5) {
            tvBatteryHealth.setText(mResources.getString(R.string.over_voltage));
        } else if (health == 6) {
            tvBatteryHealth.setText(mResources.getString(R.string.failed));
        } else {
            tvBatteryHealth.setText(mResources.getString(R.string.cold));
        }

        if (plugged == 1) {
            tvPowerSource.setText(mResources.getString(R.string.ac_power));
        } else {
            tvPowerSource.setText(mResources.getString(R.string.battery));
        }
    }





    private void initToolbar() {
        ivMenu.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        tvTitle.setText(mResources.getString(R.string.battery));
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.openDrawer();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBatInfoReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBatLow);
    }
}
