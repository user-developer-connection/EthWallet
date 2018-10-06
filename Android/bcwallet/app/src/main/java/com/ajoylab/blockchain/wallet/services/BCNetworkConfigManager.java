package com.ajoylab.blockchain.wallet.services;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCNetworkConfig;

/**
 * Created by liuya on 2018/2/9.
 */

public class BCNetworkConfigManager {

    // ChainID: https://ethereum.stackexchange.com/questions/17051/how-to-select-a-network-id-or-is-there-a-list-of-network-ids
    private final BCNetworkConfig[] mConfigs = new BCNetworkConfig[] {
            new BCNetworkConfig(BCConstants.NETWORK_NAME_ETHEREUM, BCConstants.SYMBOL_ETH,
                    "https://mainnet.infura.io/NGSzjTdRBpWw15EnNOe7",
                    "https://etherscan.io/tx/",1, true),
            new BCNetworkConfig(BCConstants.NETWORK_NAME_KOVAN, BCConstants.SYMBOL_ETH,
                    "https://kovan.infura.io/NGSzjTdRBpWw15EnNOe7",
                    "https://kovan.etherscan.io/tx/", 42, false)
    };

    private BCNetworkConfig mDefaultNetwork = null;

    private static final BCNetworkConfigManager mInstance = new BCNetworkConfigManager();
    public static BCNetworkConfigManager getInstance() { return mInstance; }

    public BCNetworkConfigManager() {
        mDefaultNetwork = mConfigs[1];
    }

    public BCNetworkConfig getDefaultNetwork() { return mDefaultNetwork; }
}
