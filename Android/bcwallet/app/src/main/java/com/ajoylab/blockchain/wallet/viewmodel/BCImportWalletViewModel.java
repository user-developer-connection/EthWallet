package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.services.BCGethManager;
import com.ajoylab.blockchain.wallet.services.BCKeyChainManager;
import com.ajoylab.blockchain.wallet.services.BCWalletManager;
import com.ajoylab.blockchain.wallet.ui.BCWalletManagementViewHolder;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.Single;

/**
 * Created by liuya on 2018/1/18.
 */

public class BCImportWalletViewModel extends BCViewModelBase
{
    private static final String TAG = "###BCImportActivityVM";

    private final MutableLiveData<BCWalletData> mWallet = new MutableLiveData<>();

    public LiveData<BCWalletData> wallet() { return mWallet; }

    public void OnImportKeystoreClicked(String keystore, String password) {
        Log.d(TAG, "OnImportKeystoreClicked 111");
        mIsInProgress.postValue(true);
        mDisposable = BCWalletManager.getInstance().importKeyStoreToWallet(keystore, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWallet, this::onError);
    }

    public void OnImportPrivateKeyClicked(String privateKey) {
        Log.d(TAG, "OnImportPrivateKeyClicked 111");
        mIsInProgress.postValue(true);
        mDisposable = BCWalletManager.getInstance().importPrivateKeyToWallet(privateKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWallet, this::onError);
    }

    private void onWallet(BCWalletData wallet) {
        BCWalletManager.getInstance().setDefaultWallet(wallet);
        Log.d(TAG, "onWallet 111: " + wallet.getAddress());
        mIsInProgress.postValue(false);
        mWallet.postValue(wallet);
    }
}
