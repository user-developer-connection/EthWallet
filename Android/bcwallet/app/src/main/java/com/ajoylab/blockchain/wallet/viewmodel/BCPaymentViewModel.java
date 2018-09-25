package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.services.BCGasManager;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.services.BCKeyChainManager;
import com.ajoylab.blockchain.wallet.services.BCTransactionManager;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;
import com.ajoylab.blockchain.wallet.ui.BCWalletManagementActivity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
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
    //private final MutableLiveData<BCWalletData[]> mWalletList = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mDefaultWalletBalanceInWei = new MutableLiveData<>();

    public BCPaymentViewModel() {
        Log.d(TAG, "CONSTRUCTOR 222 thread: " + Thread.currentThread().getId());
        mActualGasPrice.setValue(gasPriceDefault());
        mActualGasLimit.setValue(gasLimitDefault());
    }

    //public LiveData<BigInteger> propPrice() { return mPropPrice; }
    public void setPropPrice(BigInteger propPrice) { mPropPrice.setValue(propPrice); }

    //public LiveData<String> propName() { return mPropName; }
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

    public LiveData<BigInteger> defaultWalletBalanceInWei() { return mDefaultWalletBalanceInWei; }

    public void createTransaction(String from, String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {

        Log.d(TAG, "createTransaction 111 from: " + from + " to: " + to + " amoutInWei: " + amount + " gasPrice: " + gasPrice.intValue() + "gasLimit: " + gasLimit.intValue());
        BCKeyChainManager keyChain = BCKeyChainManager.getInstance();
        String pwd = keyChain.generatePassword();
        Log.d(TAG, "createTransaction 222 pwd: " + pwd);

        BCTransactionManager.getInstance().createTransaction(new BCWalletData(from), to, amount, gasPrice, gasLimit, null, pwd)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCreateTransaction, this::onError);
    }

    public void onResume() {

        Log.d(TAG, "onResume 111 gasPrice: " + mActualGasPrice.getValue() + " gasLimit: " + mActualGasLimit.getValue());

        BCWalletManager.getInstance().getDefaultWallet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDefaultWallet, this::onError);
        Log.d(TAG, "onResume 222");
    }

    /*
    public void getWalletList() {
        Log.d(TAG, "getWalletList 111");

        BCWalletManager.getInstance().getWalletList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetWalletList, this::onError);

    }*/

    /*
    private void getBalance() {

        Log.d(TAG, "getBalance 111");
        BCWalletManager.getInstance().getBalanceInWei(mDefaultWallet.getValue())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDefaultWalletBalanceInWei::postValue, this::onError);
    }
    */


    private void onDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onDefaultWallet 222 address: " + wallet.getAddress());
        mDefaultWallet.setValue(wallet);
        mDefaultWalletBalanceInWei.postValue(wallet.getBalanceInWei());

        BCWalletManager.getInstance().getBalanceInWei(wallet)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDefaultWalletBalanceInWei::postValue, this::onError);
    }

    private void onCreateTransaction(String transaction) {
        Log.d(TAG, "onCreateTransactions 111");
    }

    /*
    private void onGetWalletList(BCWalletData[] list) {
        Log.d(TAG, "onGetWalletList 111 count: " + list.length);
        mWalletList.postValue(list);
    }*/

    private void onError(Throwable throwable) {
        Log.d(TAG, "onError 111: " + throwable.toString());
        Log.d(TAG, "onError 222: " + throwable.getMessage());
    }
}
