package com.ajoylab.blockchain.wallet.model;

/**
 * Created by liuya on 2018/1/18.
 */

public class BCWalletData
{
    private String mAddress = "";

    public BCWalletData(String address) {
        mAddress = address;
    }

    public String getAddress() { return mAddress; }
    public boolean isDefaultWallet() { return true; }

    public boolean sameAddress(String address) {
        return mAddress.equals(address);
    }
}
