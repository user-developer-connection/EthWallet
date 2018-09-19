package com.ajoylab.blockchain.wallet.common;

/**
 * Created by liuya on 2018/1/23.
 */

public class BCConstants
{
    public static final String EXTRA_PROP_PRICE = "extra_prop_price";
    public static final String EXTRA_PROP_NAME = "extra_prop_name";

    public static final String SYMBOL_ETH = "ETH";

    public static final long DEFAULT_GAS_PRICE = 20000000000L;
    public static final long DEFAULT_GAS_LIMIT = 90000L;
    public static final String DEFAULT_GAS_LIMIT_FOR_TOKENS = "144000";
    public static final long GAS_LIMIT_MIN = 21000L;
    public static final long GAS_LIMIT_MAX = 300000L;
    public static final long GAS_PRICE_MIN = 1000000000L; // 1 Gwei
    public static final long GAS_PRICE_MAX = 300000000000L; // 300 Gwei
    //public static final long NETWORK_FEE_MAX = 90000000000000000L; // 90000000 Gwei - 0.09 ETH
    public static final int ETHER_DECIMALS = 18;

    public static final int PAYMENT_WALLET_VIEW_HOLDER_VIEW_TYPE = 1001;
}
