package com.ajoylab.blockchain.wallet.services;

import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.fasterxml.jackson.databind.node.BigIntegerNode;

import org.ethereum.geth.BigInt;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by liuya on 2018/1/24.
 */

public class BCGasManager
{
    private static final String TAG = "###BCGasManager";

    private BigInteger mGasPriceDefault; // Gas Price retrived from GETH
    private BigInteger mGasPriceMin;
    private BigInteger mGasPriceMax;

    private BigInteger mGasLimitDefault; // Gas Limit set by default
    //private BigInteger mGasLimitActual; // Gas Limit defined by users
    private BigInteger mGasLimitMin;
    private BigInteger mGasLimitMax;

    private static final BCGasManager mInstance = new BCGasManager();
    public static BCGasManager getInstance() { return mInstance; }

    private BCGasManager() {
    }

    public void init() {
        Log.d(TAG, "init 111");
        mGasPriceDefault = BigInteger.valueOf(BCConstants.DEFAULT_GAS_PRICE);
        mGasPriceMin = BigInteger.valueOf(BCConstants.GAS_PRICE_MIN);
        mGasPriceMax = BigInteger.valueOf(BCConstants.GAS_PRICE_MAX);

        mGasLimitDefault = BigInteger.valueOf(BCConstants.DEFAULT_GAS_LIMIT);
        mGasLimitMin = BigInteger.valueOf(BCConstants.GAS_LIMIT_MIN);
        mGasLimitMax = BigInteger.valueOf(BCConstants.GAS_LIMIT_MAX);

        Observable.interval(0, 60, TimeUnit.SECONDS).doOnNext(l -> fetchGasSettings()).subscribe();
    }

    public BigInteger getGasPriceDefault() { return mGasPriceDefault; }
    public BigInteger getGasPriceMin() { return mGasPriceMin; }
    public BigInteger getGasPriceMax() { return mGasPriceMax; }

    public BigInteger getGasLimitDefault() { return mGasLimitDefault; }
    public BigInteger getGasLimitMin() { return mGasLimitMin; }
    public BigInteger getGasLimitMax() { return mGasLimitMax; }

    private void fetchGasSettings() {

        Log.d(TAG, "fetchGasSettings 111");
        final Web3j web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/llyrtzQ3YhkdESt2Fzrk"));

        try {
            EthGasPrice price = web3j.ethGasPrice().send();
            mGasPriceDefault = price.getGasPrice();
            Log.d(TAG, "fetchGasSettings OK: " + mGasPriceDefault);
        } catch (Exception ex) {
            Log.d(TAG, "fetchGasSettings: " + ex);
        }
    }
}
