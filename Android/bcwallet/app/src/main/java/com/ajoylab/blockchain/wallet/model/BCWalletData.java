package com.ajoylab.blockchain.wallet.model;

import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCPreference;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;
import com.fasterxml.jackson.databind.node.BigIntegerNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuya on 2018/1/18.
 */

public class BCWalletData
{
    private static final String TAG = "###BCWalletData";

    private String mAddress = "";
    private BigInteger mBalanceInWei = BigInteger.valueOf(0);

    public BCWalletData(String address) {
        mAddress = address;
    }

    public String getAddress() { return mAddress; }
    public BigInteger getBalanceInWei() { return mBalanceInWei; }
    public boolean isDefaultWallet() { return true; }

    public boolean sameAddress(String address) {
        return mAddress.equalsIgnoreCase(address);
    }


    public Single<BigInteger> retrieveBalance() {

        Log.d(TAG, "retrieveBalance 111");

        return BCWalletManager.getInstance()
                .getBalanceInWei(this)
                .flatMap(balance -> onRetrieveBalance(balance))
                .subscribeOn(Schedulers.io());
    }

    private Single<BigInteger> onRetrieveBalance(BigInteger balance) {
        return Single.fromCallable(() -> {
            Log.d(TAG, "onRetrieveBalance 111: " + balance);
            mBalanceInWei = balance;
            return mBalanceInWei;
        });
    }

    public Completable setDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "setDefaultWallet 111");
        return Completable.fromAction(() -> BCPreference.getInstance().setCurrentWallet(wallet.getAddress()));
    }

    private void onError(Throwable throwable) {
        Log.d(TAG, "onError 111: " + throwable.getLocalizedMessage());
    }
}
