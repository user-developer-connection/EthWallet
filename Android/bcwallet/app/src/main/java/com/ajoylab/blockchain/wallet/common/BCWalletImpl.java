package com.ajoylab.blockchain.wallet.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.services.BCGasManager;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.services.BCKeyChainManager;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;
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
    private BCWallet.PaymentListener mPaymentListener = null;
    private BroadcastReceiver mBroadcastReceiver = null;

    private static final BCWalletImpl mInstance = new BCWalletImpl();
    public static BCWalletImpl getInstance() { return mInstance; }

    private BCWalletImpl()
    {
    }

    public void init(Activity activity, BCWallet.PaymentListener listener)
    {
        Log.d(TAG, "init 111: " + activity.getFilesDir());
        mActivity = activity;
        mPaymentListener = listener;

        File file = new File(activity.getFilesDir(), "keystore/keystore");
        Log.d(TAG, "init 222: " + file.getAbsolutePath());

        BCGethManager.getInstance().init(file.getAbsolutePath());
        BCGasManager.getInstance().init();
        BCPreference.getInstance().init(activity);
        BCKeyChainManager.getInstance().init(activity);
        BCWalletManager.getInstance();

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "init 333 onReceive:" + intent);

                switch (intent.getAction()) {
                    case BCConstants.LOCALBROADCAST_PAYMENT_SUCCEED:
                        Log.d(TAG, "init 444 onReceive:");
                        if (null != mPaymentListener) {
                            String propName = intent.getStringExtra(BCConstants.LOCALBROADCAST_PAYMENT_EXTRA_PROP_NAME);
                            Log.d(TAG, "init 555 propName: " + propName);
                            mPaymentListener.onPaymentSucceed(propName);
                        }
                        break;
                    case BCConstants.LOCALBROADCAST_PAYMENT_FAIL:
                        Log.d(TAG, "init 666 onReceive:");
                        if (null != mPaymentListener) {
                            String propName = intent.getStringExtra(BCConstants.LOCALBROADCAST_PAYMENT_EXTRA_PROP_NAME);
                            Log.d(TAG, "init 777 propName: " + propName);
                            mPaymentListener.onPaymentFail(propName);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BCConstants.LOCALBROADCAST_PAYMENT_SUCCEED);
        filter.addAction(BCConstants.LOCALBROADCAST_PAYMENT_FAIL);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(mActivity);
        manager.registerReceiver(mBroadcastReceiver, filter);

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
        //mActivity.startActivityForResult(intent, BCConstants.REQUEST_CODE_PAYMENT);
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
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
    }
}
