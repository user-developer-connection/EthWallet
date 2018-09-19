package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.services.BCGasManager;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;

import java.math.BigInteger;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liuya on 2018/1/23.
 */

public class BCWalletManagementViewModel extends ViewModel
{
    private static final String TAG = "###BCWalletManagementVM";

    private final MutableLiveData<BigInteger> mPropPrice = new MutableLiveData<>();
    private final MutableLiveData<String> mPropName = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mGasPrice = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mGasLimit = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData> mDefaultWallet = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData[]> mWalletList = new MutableLiveData<>();

    public BCWalletManagementViewModel() {}

    public LiveData<BigInteger> propPrice() { return mPropPrice; }
    public void setPropPrice(BigInteger propPrice) { mPropPrice.setValue(propPrice); }

    public LiveData<String> propName() { return mPropName; }
    public void setPropName(String propName) { mPropName.setValue(propName); }

    public LiveData<BigInteger> gasPrice() { return mGasPrice; }
    public void SetGasPrice(BigInteger price) { mGasPrice.setValue(price); }

    public LiveData<BigInteger> gasLimit() {
        return mGasLimit;
    }
    public void SetGasLimit(BigInteger limit) { mGasLimit.setValue(limit); }

    public LiveData<BCWalletData> defaultWallet() { return mDefaultWallet; }
    public void setDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "setDefaultWallet 222");
        BCWalletManager.getInstance().setDefaultWallet(wallet)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> onDefaultWallet(wallet), this::onError);
    }

    public LiveData<BCWalletData[]> walletList() {
        return mWalletList;
    }

    public void onResume() {
        mGasPrice.postValue(BCGasManager.getInstance().getGasPriceDefault());
        mGasLimit.postValue(BCGasManager.getInstance().getGasLimitDefault());

        Log.d(TAG, "onResume 111 gasPrice: " + mGasPrice + " gasLimit: " + mGasLimit);

        BCWalletManager.getInstance().getDefaultWallet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDefaultWallet, this::onError);
        Log.d(TAG, "onResume 222");
    }

    public void getWalletList() {
        Log.d(TAG, "getWalletList 111");

        BCWalletManager.getInstance().getWalletList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetWalletList, this::onError);

    }

    private void onDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onDefaultWallet 222 address: " + wallet.getAddress());
        mDefaultWallet.postValue(wallet);
    }

    private void onGetWalletList(BCWalletData[] list) {
        Log.d(TAG, "onGetWalletList 111 count: " + list.length);
        mWalletList.postValue(list);
    }

    private void onError(Throwable throwable) {
        Log.d(TAG, "onError 111: " + throwable.getLocalizedMessage());
    }
}
