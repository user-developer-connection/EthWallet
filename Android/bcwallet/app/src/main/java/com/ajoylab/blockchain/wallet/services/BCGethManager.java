package com.ajoylab.blockchain.wallet.services;

/**
 * Created by liuya on 2018/1/18.
 */

import android.util.Log;

import com.ajoylab.blockchain.wallet.BCWallet;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.web3j.crypto.Wallet.create;

public class BCGethManager
{
    private static final String TAG = "###BCGethManager";

    private static final int PRIVATE_KEY_RADIX = 16;
    /**
     * CPU/Memory cost parameter. Must be larger than 1, a power of 2 and less than 2^(128 * r / 8).
     */
    private static final int N = 1 << 9;
    /**
     * Parallelization parameter. Must be a positive integer less than or equal to Integer.MAX_VALUE / (128 * r * 8).
     */
    private static final int P = 1;

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

    /*
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
    }*/

    public Single<String[]> getWalletAccounts() {
        Log.d(TAG, "getWalletAccounts 111");
        return Single.fromCallable(() -> {
            Accounts accounts = mKeyStore.getAccounts();
            int len = (int)accounts.size();
            String[] result = new String[len];

            Log.d(TAG, "getWalletAccounts 222 count: " + len);

            for (int i = 0; i < len; i++) {
                org.ethereum.geth.Account gethAccount = accounts.get(i);
                result[i] = gethAccount.getAddress().getHex().toLowerCase();
            }
            return result;
        }).subscribeOn(Schedulers.io());
    }

    public Completable deleteWallet(String address, String password) {
        return Single.fromCallable(() -> findAccount(address))
                .flatMapCompletable(account -> Completable.fromAction(() -> mKeyStore.deleteAccount(account, password)))
                .subscribeOn(Schedulers.io());
    }

    public Single<BCWalletData> importKeystore(final String store, final String password, final String newPassword) {

        Log.d(TAG, "importKeystore 111");

        return Single.fromCallable(() -> {
            String address = "";
            try {
                JSONObject jsonObject = new JSONObject(store);
                address = "0x" + jsonObject.getString("address");
                Log.d(TAG, "importKeystore 333: " + address);
            } catch (JSONException ex) {
                Log.d(TAG, "importKeystore 444");
                throw new BCException(BCConstants.ERROR_CODE_WALLET_INVALID_KEYSTORE, "Invalid keystore!");
            }

            if (mKeyStore.hasAddress(new Address(address))) {
                //throw new ServiceErrorException(C.ErrorCode.ALREADY_ADDED, "Already added");
                Log.d(TAG, "Already Added");
                throw new BCException(BCConstants.ERROR_CODE_WALLET_ALREADY_ADDED, "Wallet Already Added!");
            }

            Account account = null;
            try {
                Log.d(TAG, "importKeystore 555 pwd: " + password + " nPWD: " + newPassword);
                account = mKeyStore.importKey(store.getBytes(Charset.forName("UTF-8")), password, newPassword);
            } catch (Exception ex) {
                deleteAccount(address, newPassword).subscribe(() -> {}, t -> {});
                Log.d(TAG, "importKeystore 666");
                throw new BCException(BCConstants.ERROR_CODE_WALLET_ADDRESS_OR_PASSWORD_WRONG, ex.getMessage());
                //throw ex;
            }
            Log.d(TAG, "importKeystore 777");
            return new BCWalletData(account.getAddress().getHex().toLowerCase());
        }).subscribeOn(Schedulers.io());
    }

    public Completable deleteAccount(String address, String password) {
        return Single.fromCallable(() -> findAccount(address))
                .flatMapCompletable(account -> Completable.fromAction(() -> mKeyStore.deleteAccount(account, password)))
                .subscribeOn(Schedulers.io());
    }

    public Single<BCWalletData> importPrivateKey(String privateKey, String newPassword) {
        return Single.fromCallable(() -> {
            BigInteger key = new BigInteger(privateKey, PRIVATE_KEY_RADIX);
            ECKeyPair keypair = ECKeyPair.create(key);
            WalletFile walletFile = create(newPassword, keypair, N, P);
            return new ObjectMapper().writeValueAsString(walletFile);
        }).compose(upstream -> importKeystore(upstream.blockingGet(), newPassword, newPassword));
    }

    public Single<byte[]> signTransaction(String fromAddress,
                                          String signerPassword,
                                          String toAddress,
                                          BigInteger amount,
                                          BigInteger gasPrice,
                                          BigInteger gasLimit,
                                          long nonce,
                                          byte[] data,
                                          long chainId) {

        Log.d(TAG, "signTransaction 111 from: " + fromAddress + " to: " + toAddress + " amoutInWei: " + amount.longValue() + " gasPrice: " + gasPrice.intValue() + "gasLimit: " + gasLimit.intValue() + " nonce: " + nonce + " chainID:" + chainId);


        return Single.fromCallable(() -> {
            BigInt amountBI = new BigInt(0);
            amountBI.setString(amount.toString(), 10);

            BigInt gasPriceBI = new BigInt(0);
            gasPriceBI.setString(gasPrice.toString(), 10);

            BigInt gasLimitBI = new BigInt(0);
            gasLimitBI.setString(gasLimit.toString(), 10);

            Transaction tx = new Transaction(nonce, new Address(toAddress), amountBI, gasLimitBI, gasPriceBI, data);

            BigInt chain = new BigInt(chainId); // Chain identifier of the main net
            Account account = findAccount(fromAddress);
            mKeyStore.unlock(account, signerPassword);
            Transaction signed = mKeyStore.signTx(account, tx, chain);
            mKeyStore.lock(account.getAddress());

            Log.d(TAG, "signTransaction 222: " + signed.encodeRLP());
            return signed.encodeRLP();
        }).subscribeOn(Schedulers.io());
    }

    private Account findAccount(String address) throws Exception {
        Accounts accounts = mKeyStore.getAccounts();
        int len = (int)accounts.size();
        for (int i = 0; i < len; i++) {
            try {
                Log.d(TAG, "Address: " + accounts.get(i).getAddress().getHex());
                if (accounts.get(i).getAddress().getHex().equalsIgnoreCase(address)) {
                    return accounts.get(i);
                }
            } catch (Exception ex) {
                /* Quietly: interest only result, maybe next is ok. */
            }
        }
        String message = "Wallet with address: " + address + " not found";
        throw new BCException(BCConstants.ERROR_CODE_WALLET_NOT_FOUND, message);
    }
}
