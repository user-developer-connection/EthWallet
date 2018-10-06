package com.ajoylab.blockchain.wallet.ui;

import android.os.Bundle;
import android.os.Debug;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by liuya on 2018/1/25.
 */

public class BCWalletManagementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RecyclerView.RecyclerListener
{
    private static final String TAG = "###BCWalletViewHolder";

    public final static String IS_DEFAULT_WALLET = "is_default_wallet";

    private final RadioButton mRadioButton;
    private final TextView mAccountAddress;
    private final TextView mWalletAccountBalance;
    private BCWalletData mWallet; // Reference
    private BCWalletManagementAdapter.OnSetDefaultWalletListener mOnSetDefaultWalletListener;
    private Disposable mDisposable;

    public BCWalletManagementViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_wallet_management, parent, false));

        mRadioButton = itemView.findViewById(R.id.toggleDefaultButton);
        mAccountAddress = itemView.findViewById(R.id.walletAddress);
        mWalletAccountBalance = itemView.findViewById(R.id.walletAccountBalance);

        mRadioButton.setOnClickListener(this::onClick);
        mAccountAddress.setOnClickListener(this::onClick);
        itemView.findViewById(R.id.walletName).setOnClickListener(this::onClick);
    }

    public void setOnSetDefaultWalletListener(BCWalletManagementAdapter.OnSetDefaultWalletListener listener) {
        mOnSetDefaultWalletListener = listener;
    }

    public void updateContent(BCWalletData data, Bundle bundle) {
        mWallet = null;
        mRadioButton.setEnabled(false);
        mAccountAddress.setText(null);
        mWalletAccountBalance.setText("0");

        if (null != data) {
            mWallet = data;
            mAccountAddress.setText(mWallet.getAddress());
            Log.d(TAG, "updateContent isDefault: " + bundle.getBoolean(IS_DEFAULT_WALLET, false));
            mRadioButton.setChecked(bundle.getBoolean(IS_DEFAULT_WALLET, false));
            mRadioButton.setEnabled(true);

            mDisposable = data.retrieveBalance()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onRetrieveBalance, this::onError);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        Log.d(TAG, "onClick 111");
        if (R.id.toggleDefaultButton == i || R.id.walletAddress == i || R.id.walletName == i) {
            Log.d(TAG, "onClick 222");
            if (null != mOnSetDefaultWalletListener) {
                Log.d(TAG, "onClick 333");
                mOnSetDefaultWalletListener.onSetDefault(mWallet);
            }
        } else {
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        Log.d(TAG, "onViewRecycled 111");
        if (holder == this) {
            Log.d(TAG, "onViewRecycled 222");
            if (null != mDisposable) {
                Log.d(TAG, "onViewRecycled 333");
                mDisposable.dispose();
            }
        }
    }

    private void onRetrieveBalance(BigInteger balanceInWei) {
        Log.d(TAG, "onRetrieveBalance 111: " + balanceInWei);

        try {
            BigDecimal b = BCBalanceUtils.weiToEth(balanceInWei, 4);
            mWalletAccountBalance.setText(b.toPlainString());
        } catch (Exception ex) {
            Log.d(TAG, "onRetrieveBalance 222: " + ex.getMessage());
        }
    }

    private void onError(Throwable exception) {
        Log.d(TAG, "onError 111: " + exception.getMessage());
    }
}
