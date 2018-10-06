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

public class BCPaymentViewModel extends BCViewModelBase
{
    private static final String TAG = "###BCPaymentViewModel";

    private final MutableLiveData<BigInteger> mPropPrice = new MutableLiveData<>();
    private final MutableLiveData<String> mPropName = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mActualGasPrice = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mActualGasLimit = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData> mDefaultWallet = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> mDefaultWalletBalanceInWei = new MutableLiveData<>();
    private final MutableLiveData<String> mTransaction = new MutableLiveData<>();

    public BCPaymentViewModel() {
        mActualGasPrice.setValue(gasPriceDefault());
        mActualGasLimit.setValue(gasLimitDefault());
    }

    public BigInteger propPrice() { return mPropPrice.getValue(); }
    public void setPropPrice(BigInteger propPrice) { mPropPrice.setValue(propPrice); }
    public void setPropName(String propName) { mPropName.setValue(propName); }
    public LiveData<BCWalletData> defaultWallet() { return mDefaultWallet; }
    public LiveData<BigInteger> defaultWalletBalanceInWei() { return mDefaultWalletBalanceInWei; }
    public LiveData<String> transaction() { return mTransaction; }

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

    public void createTransaction(String from, String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {

        Log.d(TAG, "createTransaction 111 from: " + from + " to: " + to + " amoutInWei: " + amount.longValue() + " gasPrice: " + gasPrice.longValue() + "gasLimit: " + gasLimit.intValue());
        mIsInProgress.postValue(true);
        BCKeyChainManager keyChain = BCKeyChainManager.getInstance();

        mDisposable = keyChain.getPassword(from)
                .flatMap(password -> BCTransactionManager.getInstance().createTransaction(from, to, amount, gasPriceDefault(), gasLimit, null, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCreateTransaction, this::onError);;
    }

    public void onResume() {

        Log.d(TAG, "onResume 111 gasPrice thread: " + Thread.currentThread().getId());

        mDisposable = BCWalletManager.getInstance().getDefaultWallet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDefaultWallet, error -> {});
        Log.d(TAG, "onResume 222");
    }

    private void onDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onDefaultWallet 222 address: " + wallet.getAddress() + " balance: " + wallet.getBalanceInWei());
        mDefaultWallet.setValue(wallet);
        //mDefaultWalletBalanceInWei.setValue(wallet.getBalanceInWei());

        wallet.retrieveBalance()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDefaultWalletBalanceInWei::postValue, error -> {});
    }

    private void onCreateTransaction(String transaction) {
        Log.d(TAG, "onCreateTransactions 111");
        mIsInProgress.postValue(false);
        mTransaction.postValue(transaction);
    }
}
