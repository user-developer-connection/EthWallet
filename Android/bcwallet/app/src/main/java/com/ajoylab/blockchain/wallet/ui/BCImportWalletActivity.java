package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.model.BCViewPagerData;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.viewmodel.BCImportWalletViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuya on 2018/1/17.
 */

public class BCImportWalletActivity extends AppCompatActivity {

    private static final int KEYSTORE_PAGE_INDEX = 0;
    private static final int PRIVATEKEY_PAGE_INDEX = 1;

    private List<BCViewPagerData> mPages = new ArrayList<>();

    private BCImportWalletViewModel mViewModel = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_wallet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //
        mPages.add(KEYSTORE_PAGE_INDEX, new BCViewPagerData(getString(R.string.keystore), new BCImportKeystoreFragment()));
        mPages.add(PRIVATEKEY_PAGE_INDEX, new BCViewPagerData(getString(R.string.private_key), new BCImportPrivateKeyFragment()));

        BCViewPagerAdapter vpAdapter = new BCViewPagerAdapter(getSupportFragmentManager(), mPages);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(vpAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        //
        mViewModel = ViewModelProviders.of(this).get(BCImportWalletViewModel.class);
        mViewModel.wallet().observe(this, this::onWallet);
        mViewModel.error().observe(this, this::onError);
    }

    private void onWallet(BCWalletData wallet) {

    }

    private void onError(Integer err) {

    }

}
