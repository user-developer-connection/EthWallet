package com.ajoylab.blockchain.wallet.services;

import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCPreference;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.geth.Accounts;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.web3j.crypto.Wallet.create;

/**
 * Created by liuya on 2018/1/24.
 */

public class BCWalletManager
{
    private static final String TAG = "###BCWalletManager";

    private static final BCWalletManager mInstance = new BCWalletManager();
    public static BCWalletManager getInstance() { return mInstance; }

    //private Map<String, BCWalletData> mWalletList = new HashMap<>();
    //private final List<BCWalletData> mWalletList = new ArrayList<>();

    public BCWalletManager() {
        Log.d(TAG, "CONSTRCTOR");
        //refreshWalletAccounts();
    }

    //public BCWalletData[] getWalletAccounts() { return mWalletList.toArray(new BCWalletData[mWalletList.size()]); }

    private Single<BCWalletData> findWallet(String address) {
        Log.d(TAG, "findWallet 111 thread: " + Thread.currentThread().getId());
        Log.d(TAG, "findWallet 222 addr: " + address);

        /*
        return Single.fromCallable(() -> {
            for (BCWalletData wallet : mWalletList) {
                if (wallet.sameAddress(address)) {
                    Log.d(TAG, "findWallet 333");
                    return wallet;
                }
            }

            Log.d(TAG, "findWallet 444 return null");
            return null;
        });
        */



        return refreshWalletAccounts().flatMap(accounts -> {
            for (BCWalletData wallet : accounts) {
                if (wallet.sameAddress(address)) {
                    Log.d(TAG, "findWallet 333");
                    return Single.just(wallet);
                }
            }
            Log.d(TAG, "findWallet 444 return null");
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

        /*
        String prefDefaultWallet = BCPreference.getInstance().getCurrentWallet();
        if (null == prefDefaultWallet && mWalletList.size() > 0) {
            prefDefaultWallet = mWalletList.get(0).getAddress();
            BCPreference.getInstance().setCurrentWallet(prefDefaultWallet);
        }

        Log.d(TAG, "getDefaultWallet 222 address: " + prefDefaultWallet);
        return findWallet(prefDefaultWallet);
        */

        return doGetDefaultWallet()
                .onErrorResumeNext(refreshWalletAccounts()
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

    public Single<BCWalletData[]> refreshWalletAccounts() {
        Log.d(TAG, "refreshWalletAccounts 111");
        return BCGethManager.getInstance()
                .getWalletAccounts()
                .compose(upstream -> onRefreshWalletAccounts(upstream.blockingGet()));
    }

    /*
    public Single<BCWalletData[]> refreshWalletAccounts() {
        Log.d(TAG, "getWallList 111");
        mWalletList.clear();
        return BCGethManager.getInstance()
                .getWalletAccounts()
                .compose(upstream -> onRefreshWalletAccounts(upstream.blockingGet()));
    }
    */

    private Single<BCWalletData[]> onRefreshWalletAccounts(String[] wallets) {

        Log.d(TAG, "onRefreshWalletAccounts 111 count: " + wallets.length);
        return Single.fromCallable(() -> {
            int len = wallets.length;
            BCWalletData[] result = new BCWalletData[len];
            for (int i = 0; i < len; i++) {
                result[i] = new BCWalletData(wallets[i]);
                result[i].retrieveBalance();
            }
            return result;
        }).subscribeOn(Schedulers.io());
    }

    public Single<BCWalletData> importKeyStoreToWallet(String keystore, String password) {

        Log.d(TAG, "importKeyStoreToWallet 111");
        BCKeyChainManager keyChain = BCKeyChainManager.getInstance();
        String pwd = keyChain.generatePassword();
        Log.d(TAG, "importKeyStoreToWallet 222 pwd: " + pwd);

        return BCGethManager.getInstance()
                .importKeystore(keystore, password, pwd)
                .flatMap(bcWalletData ->
                        keyChain.setPassword(bcWalletData, pwd)
                                .onErrorResumeNext(err -> deleteWallet(bcWalletData.getAddress(), pwd))
                                .toSingle(() -> bcWalletData));
    }

    public Single<BCWalletData> importPrivateKeyToWallet(String privateKey) {
        Log.d(TAG, "importPrivateKeyToWallet 111");
        BCKeyChainManager keyChain = BCKeyChainManager.getInstance();
        String pwd = keyChain.generatePassword();
        Log.d(TAG, "importPrivateKeyToWallet 222 pwd: " + pwd);

        return BCGethManager.getInstance()
                .importPrivateKey(privateKey, pwd)
                .flatMap(bcWalletData ->
                        keyChain.setPassword(bcWalletData, pwd)
                                .onErrorResumeNext(err -> deleteWallet(bcWalletData.getAddress(), pwd))
                                .toSingle(() -> bcWalletData));
    }

    public Completable deleteWallet(String address, String password) {
        return BCGethManager.getInstance().deleteWallet(address, password);
    }

    public Single<BigInteger> getBalanceInWei(BCWalletData wallet) {

        Log.d(TAG, "getBalanceInWei 111");

        return Single.fromCallable(() -> Web3jFactory
                .build(new HttpService("https://kovan.infura.io/llyrtzQ3YhkdESt2Fzrk"))
                .ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.LATEST)
                .send()
                .getBalance())
                .subscribeOn(Schedulers.io());
    }
}
