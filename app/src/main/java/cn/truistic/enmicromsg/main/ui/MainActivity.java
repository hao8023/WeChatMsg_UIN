package cn.truistic.enmicromsg.main.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

import cn.truistic.enmicromsg.R;
import cn.truistic.enmicromsg.base.BaseActivity;
import cn.truistic.enmicromsg.common.util.DeviceUtil;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase.loadLibs(this);
        initView();
        Log.d("deviceid", DeviceUtil.getDeviceId(this));
    }

    private void initView() {
//        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        setSupportActionBar(toolbar);

        HomeFragment homeFragment = new HomeFragment();
        GroupsFragment groupsFragment = new GroupsFragment();
        ExportFragment exportFragment = new ExportFragment();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(groupsFragment);
        fragments.add(exportFragment);
        String[] titles = {getString(R.string.main_title_home)
                , getString(R.string.main_title_grouds), getString(R.string.main_title_export)};
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

}
