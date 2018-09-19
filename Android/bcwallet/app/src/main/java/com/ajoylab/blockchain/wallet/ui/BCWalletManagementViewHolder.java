package com.ajoylab.blockchain.wallet.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.model.BCWalletData;

/**
 * Created by liuya on 2018/1/25.
 */

public class BCWalletManagementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final static String IS_DEFAULT_WALLET = "is_default_wallet";

    private final RadioButton mRadioButton;
    private final TextView mAccountAddress;
    private BCWalletData mWallet; // Reference
    private BCWalletManagementAdapter.OnSetDefaultWalletListener mOnSetDefaultWalletListener;

    public BCWalletManagementViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_payment_wallet, parent, false));

        mRadioButton = itemView.findViewById(R.id.radioButton);
        mAccountAddress = itemView.findViewById(R.id.address);
    }

    public void setOnSetDefaultWalletListener(BCWalletManagementAdapter.OnSetDefaultWalletListener listener) {
        mOnSetDefaultWalletListener = listener;
    }

    public void updateContent(BCWalletData data, Bundle bundle) {
        mWallet = null;
        mRadioButton.setEnabled(false);
        mAccountAddress.setText(null);
        if (null != data) {
            mWallet = null;
            mRadioButton.setEnabled(bundle.getBoolean(IS_DEFAULT_WALLET, false));
            mAccountAddress.setText(mWallet.getAddress());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (R.id.radioButton == i || R.id.address == i) {
            if (null != mOnSetDefaultWalletListener) {
                mOnSetDefaultWalletListener.onSetDefault(mWallet);
            }
        } else {
        }
    }
}
