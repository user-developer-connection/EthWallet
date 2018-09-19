package com.ajoylab.blockchain.wallet.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;

import com.ajoylab.blockchain.wallet.model.BCViewPagerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuya on 2018/1/17.
 */

public class BCViewPagerAdapter extends FragmentPagerAdapter {

    private List<BCViewPagerData> mPages;

    public BCViewPagerAdapter(FragmentManager fm, List<BCViewPagerData> pages) {
        super(fm);

        mPages = pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).title;
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position).fragment;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }
}
