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

    public Single<String> getPassword(BCWalletData wallet) {
        return Single.fromCallable(() -> {
            String addr = wallet.getAddress();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return new String(BCSecurityKeyStore.getData(mContext, addr, addr, addr + "iv"));
            } else {
                return BCSecurityKeyStorePreApi23.getData(mContext, addr);
            }
        });
    }

    public Completable setPassword(BCWalletData wallet, String password) {
        return Completable.fromAction(() -> {
            String addr = wallet.getAddress();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BCSecurityKeyStore.setData(mContext, password.getBytes(), addr, addr, addr + "iv");
            } else {
                BCSecurityKeyStorePreApi23.setData(mContext, addr, password);
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
