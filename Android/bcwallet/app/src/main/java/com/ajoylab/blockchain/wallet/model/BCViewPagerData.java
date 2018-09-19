package com.ajoylab.blockchain.wallet.model;

import android.support.v4.app.Fragment;

/**
 * Created by liuya on 2018/1/17.
 */

public class BCViewPagerData
{
    private static final String TAG = "###BCViewPagerData";

    public String title;
    public Fragment fragment;

    public BCViewPagerData(String title, Fragment fragment)
    {
        this.title = title;
        this.fragment = fragment;
    }
}
