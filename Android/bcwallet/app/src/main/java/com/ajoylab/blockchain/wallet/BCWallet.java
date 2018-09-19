package com.ajoylab.blockchain.wallet;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCWalletImpl;
import com.ajoylab.blockchain.wallet.ui.BCPaymentActivity;

/**
 * Created by liuya on 2018/1/21.
 */

public class BCWallet
{
    private static final String TAG = "###BCWallet";

    public static void init(Activity activity)
    {
        Log.d(TAG, "CONSTRUCTOR 111");
        BCWalletImpl.getInstance().init(activity);
    }

    public static void makePayment(String propName, String propPrice)
    {
        Log.d(TAG, "makePayment 111");
        BCWalletImpl.getInstance().makePayment(propName, propPrice);
    }

    public static void onCreate(Activity activity) { BCWalletImpl.getInstance().onCreate(activity); }

    public static void onStart(Activity activity)
    {
        BCWalletImpl.getInstance().onStart(activity);
    }

    public static void onResume(Activity activity) { BCWalletImpl.getInstance().onResume(activity); }

    public static void onPause(Activity activity)
    {
        BCWalletImpl.getInstance().onPause(activity);
    }

    public static void onStop(Activity activity)
    {
        BCWalletImpl.getInstance().onStop(activity);
    }

    public static void onDestroy(Activity activity) { BCWalletImpl.getInstance().onDestroy(activity); }

    public static boolean onBackPressed()
    {
        return false;
    }
}
