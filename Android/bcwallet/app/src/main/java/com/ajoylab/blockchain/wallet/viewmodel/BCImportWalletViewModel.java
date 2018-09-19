package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.services.BCGethManager;

/**
 * Created by liuya on 2018/1/18.
 */

public class BCImportWalletViewModel extends ViewModel
{
    private final MutableLiveData<BCWalletData> mWallet = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    public LiveData<BCWalletData> wallet() {
        return mWallet;
    }
    public LiveData<Integer> error() { return mError; }

    public void OnImportKeystoreClicked(String keystore, String password) {
        BCGethManager.getInstance()
                .importKeystore(keystore, password, "88888888")
                .subscribe(this::onWallet, this::onError);
    }

    public void OnImportPrivateKeyClicked(String key) {
    }

    private void onWallet(BCWalletData wallet) {
    }

    public void onError(Throwable throwable) {
    }
}
