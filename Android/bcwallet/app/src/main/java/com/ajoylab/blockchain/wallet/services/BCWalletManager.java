package com.ajoylab.blockchain.wallet.services;

import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCPreference;
import com.ajoylab.blockchain.wallet.model.BCWalletData;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuya on 2018/1/24.
 */

public class BCWalletManager
{
    private static final String TAG = "###BCWalletManager";

    private static final BCWalletManager mInstance = new BCWalletManager();
    public static BCWalletManager getInstance() { return mInstance; }

    public BCWalletManager() {}

    private Single<BCWalletData> findWallet(String address) {
        Log.d(TAG, "findWallet 111 thread: " + Thread.currentThread().getId());
        return getWalletList().flatMap(accounts -> {
            for (BCWalletData wallet : accounts) {
                if (wallet.sameAddress(address)) {
                    return Single.just(wallet);
                }
            }
            return null;
        });
    }

    private Single<BCWalletData> doGetDefaultWallet() {
        Log.d(TAG, "doGetDefaultWallet 111: "  + Thread.currentThread().getId());

        return Single.fromCallable(() -> BCPreference.getInstance().getCurrentWallet())
                .flatMap(this::findWallet)
                .subscribeOn(Schedulers.io());
    }

    public Single<BCWalletData> getDefaultWallet() {

        Log.d(TAG, "getDefaultWallet 111 thread: " + Thread.currentThread().getId());

        return doGetDefaultWallet()
                .onErrorResumeNext(getWalletList()
                .to(single -> Flowable.fromArray(single.blockingGet()))
                .firstOrError()
                .flatMapCompletable(this::setDefaultWallet)
                .andThen(doGetDefaultWallet()))
                .subscribeOn(Schedulers.io());
    }

    public Completable setDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "setDefaultWallet 111");
        return Completable.fromAction(() -> BCPreference.getInstance().setCurrentWallet(wallet.getAddress()));
    }

    public Single<BCWalletData[]> getWalletList() {
        Log.d(TAG, "getWallList 111");
        return BCGethManager.getInstance().getWalletList();
    }

    public Single<BCWalletData> importKeystoreToWallet(String store, String password, String newPassword) {
        return BCGethManager.getInstance().importKeystore(store, password, newPassword);
    }
}
