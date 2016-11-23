package cn.truistic.enmicromsg.main.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * MainPagerAdapter
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] titiles;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] titiles) {
        super(fm);
        this.fragments = fragments;
        this.titiles = titiles;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titiles[position];
    }

}
