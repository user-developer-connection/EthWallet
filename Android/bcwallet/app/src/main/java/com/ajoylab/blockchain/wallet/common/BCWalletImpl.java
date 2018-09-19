package com.ajoylab.blockchain.wallet.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ajoylab.blockchain.wallet.services.BCGasManager;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.ui.BCPaymentActivity;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by liuya on 2018/1/21.
 */

public class BCWalletImpl
{
    private static final String TAG = "###BCWalletImpl";

    private Activity mActivity = null;
    private boolean mInitialized = false;

    private static final BCWalletImpl mInstance = new BCWalletImpl();
    public static BCWalletImpl getInstance() { return mInstance; }

    private BCWalletImpl()
    {
    }

    public void init(Activity activity)
    {
        Log.d(TAG, "init 111: " + activity.getFilesDir());
        mActivity = activity;

        File file = new File(activity.getFilesDir(), "keystore/keystore");
        Log.d(TAG, "init 222: " + file.getAbsolutePath());

        BCGethManager.getInstance().init(file.getAbsolutePath());
        BCGasManager.getInstance().init();
        BCPreference.getInstance().init(activity);

        Log.d(TAG, "init 333");
        mInitialized = true;
    }

    public void makePayment(String propName, String propPrice)
    {
        Log.d(TAG, "makePayment 111");
        Intent intent = new Intent(mActivity, BCPaymentActivity.class);
        intent.putExtra(BCConstants.EXTRA_PROP_NAME, propName);
        intent.putExtra(BCConstants.EXTRA_PROP_PRICE, propPrice);
        mActivity.startActivity(intent);
    }

    public void onCreate(Activity activity)
    {
        Log.d(TAG, "onCreate 111");
    }

    public void onStart(Activity activity)
    {
        Log.d(TAG, "onStart 111 mInitialized: " + mInitialized);
        if (!mInitialized)
            return;

        Log.d(TAG, "onStart 222");
    }

    public void onStop(Activity activity)
    {
        Log.d(TAG, "onStop 111");
        if (!mInitialized)
            return;
    }

    public void onResume(Activity activity)
    {
    }

    public void onPause(Activity activity)
    {
    }

    public void onDestroy(Activity activity)
    {
        Log.d(TAG, "onDestroy 111");
    }
}
