package com.ajoylab.blockchain.wallet.model;

import android.util.Log;

import com.ajoylab.blockchain.wallet.services.BCWalletManager;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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


    public void retrieveBalance() {

        Log.d(TAG, "retrieveBalance 111");
        BCWalletManager.getInstance().getBalanceInWei(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRetrieveBalance, this::onError);
    }

    private void onRetrieveBalance(BigInteger balance) {
        Log.d(TAG, "onRetrieveBalance 111: " + balance);
        mBalanceInWei = balance;
    }

    private void onError(Throwable throwable) {
        Log.d(TAG, "onError 111: " + throwable.getLocalizedMessage());
    }
}
