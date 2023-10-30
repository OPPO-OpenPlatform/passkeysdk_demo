package com.osec.fido2test.ui.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.osec.fido2test.ui.fragment.BaseTestFragment;
import com.osec.fido2test.ui.fragment.LocalTestFragment;
import com.google.android.material.tabs.TabLayout;
import com.omes.fido2test.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() " + this + " savedInstanceState=" + savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() " + this);
    }

    public static String getAppVersionName(@NonNull Context context) {
        String versionName = "0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
        }
        return versionName;
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String appVersion = getAppVersionName(this);
            Log.d(TAG, "initView appVersion=" + appVersion);
            actionBar.setTitle(getString(R.string.app_name_version, appVersion));
        }
    }

    private void initData() {
        Log.d(TAG, "initData");
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new MainFragmentsAdapter(getSupportFragmentManager()));
        TabLayout tlLayout = findViewById(R.id.tabs);
        tlLayout.setupWithViewPager(viewPager);
        tlLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private class MainFragmentsAdapter extends FragmentStatePagerAdapter {

        private final List<AdapterData> mFragments;

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
            mFragments.add(new AdapterData(getString(R.string.test_local), new LocalTestFragment()));
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "MainFragmentsAdapter getItem " + position);
            return mFragments.get(position).mFragment;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).mTitle;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(TAG, "MainFragmentsAdapter setPrimaryItem: " + object);
            if (object instanceof BaseTestFragment) {
                ((BaseTestFragment) object).onVisible();
            }
        }
    }

    private static class AdapterData {
        private final String mTitle;
        private final Fragment mFragment;

        public AdapterData(String title, Fragment fragment) {
            this.mTitle = title;
            this.mFragment = fragment;
        }
    }
}
