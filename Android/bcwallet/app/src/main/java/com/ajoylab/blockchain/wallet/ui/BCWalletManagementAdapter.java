package com.ajoylab.blockchain.wallet.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.model.BCWalletData;

/**
 * Created by liuya on 2018/1/25.
 */

public class BCWalletManagementAdapter extends RecyclerView.Adapter<BCWalletManagementViewHolder>
{
    private static final String TAG = "###BCWalletManageAdpt";

    public interface OnSetDefaultWalletListener {
        void onSetDefault(BCWalletData wallet);
    }

    private BCWalletData[] mWallets = new BCWalletData[0];
    private BCWalletData mDefaultWallet = null;
    private final OnSetDefaultWalletListener mOnSetDefaultWalletListener;

    public BCWalletManagementAdapter(OnSetDefaultWalletListener defaultListener) {
        Log.d(TAG, "onCreate 111");
        mOnSetDefaultWalletListener = defaultListener;
    }

    public void setDefaultWallet(BCWalletData wallet) {
        mDefaultWallet = wallet;
        notifyDataSetChanged();
    }

    public void setWallets(BCWalletData[] wallets) {
        mWallets = (null == wallets ? new BCWalletData[0] : wallets);
        notifyDataSetChanged();
    }

    @Override
    public BCWalletManagementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BCWalletManagementViewHolder vHolder = null;
        switch (viewType) {
            case BCConstants.PAYMENT_WALLET_VIEW_HOLDER_VIEW_TYPE:
                vHolder = new BCWalletManagementViewHolder(parent);
                vHolder.setOnSetDefaultWalletListener(mOnSetDefaultWalletListener);
                break;
            default:
                break;
        }
        return vHolder;
    }

    @Override
    public void onBindViewHolder(BCWalletManagementViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case BCConstants.PAYMENT_WALLET_VIEW_HOLDER_VIEW_TYPE:
                BCWalletData wallet = mWallets[position];
                Bundle bundle = new Bundle();
                bundle.putBoolean(
                        BCWalletManagementViewHolder.IS_DEFAULT_WALLET,
                        null != mDefaultWallet && mDefaultWallet.sameAddress(wallet.getAddress()));
                holder.updateContent(wallet, bundle);
            break;
        }
    }

    @Override
    public int getItemCount() {
        return mWallets.length;
    }

    @Override
    public int getItemViewType(int position) {
        return BCConstants.PAYMENT_WALLET_VIEW_HOLDER_VIEW_TYPE;
    }
}
