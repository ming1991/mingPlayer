package com.itheima.mobileplayer31.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class MainAdadpter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    public void updateFragments(ArrayList<Fragment> fragments){
        this.fragments.clear();
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }
    public MainAdadpter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
