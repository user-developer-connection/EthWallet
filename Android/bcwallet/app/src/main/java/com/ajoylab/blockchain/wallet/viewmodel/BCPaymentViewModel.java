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

import java.math.BigDecimal;
import java.math.BigInteger;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liuya on 2018/1/23.
 */

public class BCPaymentViewModel extends ViewModel
{
    private static final String TAG = "###BCPaymentViewModel";

    private final MutableLiveData<BigInteger> mPropPrice = new MutableLiveData<>();
    private final MutableLiveData<String> mPropName = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mActualGasPrice = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mActualGasLimit = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData> mDefaultWallet = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData[]> mWalletList = new MutableLiveData<>();

    public BCPaymentViewModel() {
        Log.d(TAG, "CONSTRUCTOR 222 thread: " + Thread.currentThread().getId());
        mActualGasPrice.setValue(gasPriceDefault());
        mActualGasLimit.setValue(gasLimitDefault());
    }

    public LiveData<BigInteger> propPrice() { return mPropPrice; }
    public void setPropPrice(BigInteger propPrice) { mPropPrice.setValue(propPrice); }

    public LiveData<String> propName() { return mPropName; }
    public void setPropName(String propName) { mPropName.setValue(propName); }

    public MutableLiveData<BigInteger> gasPriceActual() { return mActualGasPrice; }
    public BigInteger gasPriceDefault() { return BCGasManager.getInstance().getGasPriceDefault(); }
    public BigInteger gasPriceMin() { return BCGasManager.getInstance().getGasPriceMin(); }
    public BigInteger gasPriceMax() { return BCGasManager.getInstance().getGasPriceMax(); }

    public MutableLiveData<BigInteger> gasLimitActual() {
        return mActualGasLimit;
    }
    public BigInteger gasLimitDefault() { return BCGasManager.getInstance().getGasLimitDefault(); }
    public BigInteger gasLimitMin() { return BCGasManager.getInstance().getGasLimitMin(); }
    public BigInteger gasLimitMax() { return BCGasManager.getInstance().getGasLimitMax(); }

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

        Log.d(TAG, "onResume 111 gasPrice: " + mActualGasPrice.getValue() + " gasLimit: " + mActualGasLimit.getValue());

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
        Log.d(TAG, "onError 111: " + throwable.toString());
        Log.d(TAG, "onError 222: " + throwable.getMessage());
    }
}
