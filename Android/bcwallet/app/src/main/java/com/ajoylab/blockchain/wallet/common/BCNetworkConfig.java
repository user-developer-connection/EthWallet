package com.ajoylab.blockchain.wallet.common;

public class BCNetworkConfig {
    public final String name;
    public final String symbol;
    public final String rpcServerUrl;
    public final String etherscanUrl;
    public final int chainId;
    public final boolean isMainNetwork;

    public BCNetworkConfig(String name, String symbol, String rpcServerUrl, String etherscanUrl, int chainId, boolean isMainNetwork) {
        this.name = name;
        this.symbol = symbol;
        this.rpcServerUrl = rpcServerUrl;
        this.etherscanUrl = etherscanUrl;
        this.chainId = chainId;
        this.isMainNetwork = isMainNetwork;
    }
}
