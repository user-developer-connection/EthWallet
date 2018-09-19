package com.ajoylab.blockchain.wallet.services;

/**
 * Created by liuya on 2018/1/18.
 */

import android.util.Log;

import com.ajoylab.blockchain.wallet.model.BCWalletData;

import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.Address;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class BCGethManager
{
    private static final String TAG = "###BCGethManager";

    private KeyStore mKeyStore;

    private static final BCGethManager mInstance = new BCGethManager();
    public static BCGethManager getInstance() { return mInstance; }

    private BCGethManager() {}

    public void init(String absPath)
    {
        Log.d(TAG, "init 111");
        mKeyStore = new KeyStore(absPath, Geth.LightScryptN, Geth.LightScryptP);
    }

    public boolean hasAccount(String address) {
        return mKeyStore.hasAddress(new Address(address));
    }

    public Single<BCWalletData[]> getWalletList() {
        Log.d(TAG, "getWalletList 111");
        return Single.fromCallable(() -> {
            Accounts accounts = mKeyStore.getAccounts();
            int len = (int)accounts.size();
            BCWalletData[] result = new BCWalletData[len];

            Log.d(TAG, "getWalletList 222 count: " + len);

            for (int i = 0; i < len; i++) {
                org.ethereum.geth.Account gethAccount = accounts.get(i);
                result[i] = new BCWalletData(gethAccount.getAddress().getHex().toLowerCase());
            }
            return result;
        }).subscribeOn(Schedulers.io());
    }

    public Single<BCWalletData> importKeystore(final String store, final String password, final String newPassword) {


        return Single.fromCallable(new Callable<BCWalletData>() {
            @Override
            public BCWalletData call() throws Exception {
                String address = "";
                try {
                    JSONObject jsonObject = new JSONObject(store);
                    address =  "0x" + jsonObject.getString("address");
                } catch (JSONException ex) {
                    throw new Exception("Invalid keystore");
                }

                if (mKeyStore.hasAddress(new Address(address))) {
                    //throw new ServiceErrorException(C.ErrorCode.ALREADY_ADDED, "Already added");
                    Log.d(TAG, "Already Added");
                }

                Account account = null;
                try {
                    account = mKeyStore.importKey(store.getBytes(Charset.forName("UTF-8")), password, newPassword);
                } catch (Exception e) {
                    //deleteAccount(address, newPassword).subscribe(() -> {}, t -> {});
                    throw e;
                }
                return new BCWalletData(account.getAddress().getHex().toLowerCase());
            }
        }).subscribeOn(Schedulers.io());
    }
}
