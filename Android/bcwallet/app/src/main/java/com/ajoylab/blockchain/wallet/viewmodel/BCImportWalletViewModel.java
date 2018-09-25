package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.services.BCKeyChainManager;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.Single;

/**
 * Created by liuya on 2018/1/18.
 */

public class BCImportWalletViewModel extends ViewModel
{
    private static final String TAG = "###BCImportActivityVM";

    private final MutableLiveData<BCWalletData> mWallet = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    public LiveData<BCWalletData> wallet() {
        return mWallet;
    }
    public LiveData<Integer> error() { return mError; }

    public void OnImportKeystoreClicked(String keystore, String password) {
        Log.d(TAG, "OnImportKeystoreClicked 111");

        BCWalletManager.getInstance().importKeyStoreToWallet(keystore, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWallet, this::onError);
    }

    public void OnImportPrivateKeyClicked(String privateKey) {
        Log.d(TAG, "OnImportPrivateKeyClicked 111");

        BCWalletManager.getInstance().importPrivateKeyToWallet(privateKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWallet, this::onError);
    }

    private void onWallet(BCWalletData wallet) {
        Log.d(TAG, "onWallet 111: " + wallet.getAddress());
        mWallet.postValue(wallet);
    }

    public void onError(Throwable throwable) {
        Log.d(TAG, "onError 111: " + throwable.getMessage());
    }
}
