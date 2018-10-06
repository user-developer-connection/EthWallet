package com.ajoylab.blockchain.wallet.ui;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.model.BCViewPagerData;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.viewmodel.BCImportWalletViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuya on 2018/1/17.
 */

public class BCImportWalletActivity extends BCBaseActivity {

    private static final String TAG = "###BCImportActivity";

    private static final int KEYSTORE_PAGE_INDEX = 0;
    private static final int PRIVATEKEY_PAGE_INDEX = 1;

    private List<BCViewPagerData> mPages = new ArrayList<>();

    private Dialog mAlertDialog;
    private BCImportWalletViewModel mViewModel = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_wallet);
        setupToolbar();

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
        mViewModel.isInProgress().observe(this, this::onIsInProgress);
        mViewModel.exception().observe(this, this::onException);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void onWallet(BCWalletData wallet) {
        setResult(RESULT_OK, null);
        finish();
    }

    private void onIsInProgress(boolean isInProgress) {
        hideDialog();
        if (isInProgress) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_dialog_title_importing_wallet)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            mAlertDialog.show();
        }
    }

    private void onException(BCException ex) {
        Log.d(TAG, "onException 111: " + ex.getMessage());

        hideDialog();
        String message = ex.getMessage();
        if (null == message) {
            message = getString(R.string.alert_dialog_title_import_wallet_fail);
        }
        Log.d(TAG, "onException 222 message: " + message);
        //if (errorEnvelope.code == ALREADY_ADDED) {
        //    message = getString(R.string.error_already_added);
       // }
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton(R.string.alert_dialog_button_ok, null)
                .create();
        mAlertDialog.show();
    }

    private void hideDialog() {
        if (null != mAlertDialog && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
