package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.common.BCPreference;
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

public class BCWalletManagementViewModel extends BCViewModelBase
{
    private static final String TAG = "###BCWalletManagementVM";

    private final MutableLiveData<BCWalletData> mDefaultWallet = new MutableLiveData<>();
    private final MutableLiveData<BCWalletData[]> mWalletList = new MutableLiveData<>();

    public BCWalletManagementViewModel() {}

    public LiveData<BCWalletData[]> walletList() {
        return mWalletList;
    }

    public LiveData<BCWalletData> defaultWallet() { return mDefaultWallet; }

    public void onResume() {
        Log.d(TAG, "onResume");
        refreshWalletAccounts();
    }

    public void refreshWalletAccounts() {
        Log.d(TAG, "refreshWalletAccounts 111");
        mIsInProgress.postValue(true);
        mDisposable = BCWalletManager.getInstance().refreshWalletAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRefreshWalletAccounts, this::onError);

    }

    private void onRefreshWalletAccounts(BCWalletData[] list) {
        Log.d(TAG, "onRefreshWalletAccounts 111 count: " + list.length);

        mWalletList.postValue(list);
        if (list.length <= 0) {
            Log.d(TAG, "onRefreshWalletAccounts 222 No Wallet Return: ");
            mIsInProgress.postValue(false);
            return;
        }

        String prefDefaultWallet = BCPreference.getInstance().getCurrentWallet();
        Log.d(TAG, "onRefreshWalletAccounts 222 address: " + prefDefaultWallet);
        if (null == prefDefaultWallet) {
            prefDefaultWallet = list[0].getAddress();
        }

        for (BCWalletData wallet : list) {
            if (wallet.sameAddress(prefDefaultWallet)) {
                Log.d(TAG, "onRefreshWalletAccounts 333 address: " + prefDefaultWallet);
                onDefaultWallet(wallet);
            }
        }

        mIsInProgress.postValue(false);
    }

    public void handleSelectDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "setDefaultWallet 222");
        mDisposable = BCWalletManager.getInstance().setDefaultWallet(wallet)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> onDefaultWallet(wallet), this::onError);
    }

    private void onDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onDefaultWallet 222 address: " + wallet.getAddress());
        mDefaultWallet.postValue(wallet);
    }
}
