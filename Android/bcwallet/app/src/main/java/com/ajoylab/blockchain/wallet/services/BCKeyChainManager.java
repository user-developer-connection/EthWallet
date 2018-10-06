package com.ajoylab.blockchain.wallet.services;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.model.BCWalletData;

import java.security.SecureRandom;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by liuya on 2018/2/5.
 */

public class BCKeyChainManager
{
    private static final String TAG = "###BCKeyChainManager";

    private static final BCKeyChainManager mInstance = new BCKeyChainManager();
    public static BCKeyChainManager getInstance() { return mInstance; }

    private Context mContext;

    private BCKeyChainManager() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public Single<String> getPassword(String address) {
        return Single.fromCallable(() -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return new String(BCSecurityKeyStore.getData(mContext, address, address, address + "iv"));
            } else {
                return BCSecurityKeyStorePreApi23.getData(mContext, address);
            }
        });
    }

    public Completable setPassword(String address, String password) {
        return Completable.fromAction(() -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BCSecurityKeyStore.setData(mContext, password.getBytes(), address, address, address + "iv");
            } else {
                BCSecurityKeyStorePreApi23.setData(mContext, address, password);
            }
        });
    }

    /*
    public Single<String> generatePassword() {
        return Single.fromCallable(() -> {
            byte bytes[] = new byte[256];
            SecureRandom random = new SecureRandom();
            random.nextBytes(bytes);
            Log.d(TAG, "generatePassword: " + new String(bytes));
            return new String(bytes);
        });
    }
    */

    public String generatePassword() {
        byte bytes[] = new byte[256];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
