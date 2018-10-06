package com.ajoylab.blockchain.wallet.services;

import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.fasterxml.jackson.databind.node.BigIntegerNode;

import org.ethereum.geth.BigInt;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuya on 2018/1/24.
 */

public class BCTransactionManager
{
    private static final String TAG = "###BCTransactionManager";

    private static final BCTransactionManager mInstance = new BCTransactionManager();
    public static BCTransactionManager getInstance() { return mInstance; }

    private BCTransactionManager() {
    }

    public void init() {
        Log.d(TAG, "init 111");
    }


    public Single<String> createTransaction(String fromAddress,
                                            String toAddress,
                                            BigInteger subunitAmount,
                                            BigInteger gasPrice,
                                            BigInteger gasLimit,
                                            byte[] data,
                                            String password) {

        Log.d(TAG, "createTransaction 111 from: " + fromAddress + " to: " + toAddress + " amoutInWei: " + subunitAmount.longValue() + " gasPrice: " + gasPrice.longValue() + "gasLimit: " + gasLimit.intValue());

        final Web3j web3j = Web3jFactory.build(new HttpService(BCNetworkConfigManager.getInstance().getDefaultNetwork().rpcServerUrl));

        return Single.fromCallable(() -> {
            EthGetTransactionCount tx = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
            Log.d(TAG, "createTransaction 222: " + tx.getTransactionCount());
            return tx.getTransactionCount();
        })
        .flatMap(nonce -> BCGethManager.getInstance().signTransaction(fromAddress, password, toAddress, subunitAmount, gasPrice, gasLimit, nonce.longValue(), data, 42))
        .flatMap(signedTx -> Single.fromCallable(() -> {
            Log.d(TAG, "createTransaction 333");
            EthSendTransaction raw = web3j.ethSendRawTransaction(Numeric.toHexString(signedTx)).send();
            if (raw.hasError()) {
                Log.d(TAG, "createTransaction 444: " + raw.getError().getMessage());
                throw new BCException(BCConstants.ERROR_CODE_UNKNOWN, raw.getError().getMessage());
            }
            return raw.getTransactionHash();
        }))
        .subscribeOn(Schedulers.io());
    }
}
