package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;
import com.ajoylab.blockchain.wallet.viewmodel.BCPaymentViewModel;
import com.fasterxml.jackson.databind.node.BigIntegerNode;

import org.web3j.protocol.core.methods.response.EthGasPrice;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Created by liuya on 2018/1/21.
 */

public class BCPaymentActivity extends BCBaseActivity implements View.OnClickListener
{
    private static final String TAG = "###BCPaymentActivity";

    private BCPaymentViewModel mViewModel;
    private TextView mPropPriceText;
    private TextView mPropNameText;
    private TextView mAccountText;
    private TextView mAccountBalanceText;
    private TextView mGasPriceText;
    private TextView mDefaultGasPrice;
    private SeekBar mGasPriceSeekBar;
    private TextView mGasLimitText;
    private SeekBar mGasLimitSeekBar;
    private TextView mTotalCostText;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate 111");
        setContentView(R.layout.activity_payment);
        setupToolbar();

        mPropPriceText = findViewById(R.id.propPrice);
        mPropNameText = findViewById(R.id.propName);
        mAccountText = findViewById(R.id.accountAddress);
        mAccountBalanceText = findViewById(R.id.accountBalance);
        mGasPriceText = findViewById(R.id.gasPrice);
        mDefaultGasPrice = findViewById(R.id.defaultGasPrice);
        mGasPriceSeekBar = findViewById(R.id.gasPriceSeekBar);
        mGasLimitText = findViewById(R.id.gasLimit);
        mGasLimitSeekBar = findViewById(R.id.gasLimitSeekBar);
        mTotalCostText = findViewById(R.id.totalCost);

        String propName = getIntent().getStringExtra(BCConstants.EXTRA_PROP_NAME);
        String propPrice = getIntent().getStringExtra(BCConstants.EXTRA_PROP_PRICE);
        Log.d(TAG, "onCreate 222 propName: " + propName + " propPrice: " + propPrice);
        mPropNameText.setText(propName);
        mPropPriceText.setText(propPrice + " " + BCConstants.SYMBOL_ETH);

        mViewModel = ViewModelProviders.of(this).get(BCPaymentViewModel.class);
        mViewModel.setPropName(propName);
        mViewModel.setPropPrice(BCBalanceUtils.baseToSubunit(propPrice, BCConstants.ETHER_DECIMALS));
        Log.d(TAG, "onCreate 333 GWEI: " + BCBalanceUtils.weiToGwei(mViewModel.gasPriceDefault()));
        mDefaultGasPrice.setText(BCBalanceUtils.weiToGwei(mViewModel.gasPriceDefault()) + " Gwei");

        mGasPriceSeekBar.setMax(BCBalanceUtils.weiToGweiBI(mViewModel.gasPriceMax().subtract(mViewModel.gasPriceMin())).intValue());
        mGasPriceSeekBar.setProgress(BCBalanceUtils.weiToGweiBI(mViewModel.gasPriceDefault()).intValue());
        mGasPriceSeekBar.refreshDrawableState();
        mGasPriceSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Log.d(TAG, "onViewCreated 555 AAA progress: " + progress);
                        mViewModel.gasPriceActual().setValue(BCBalanceUtils.gweiToWei(BigDecimal.valueOf(progress)).add(mViewModel.gasPriceMin()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        int max = mViewModel.gasLimitMax().subtract(mViewModel.gasLimitMin()).intValue();
        Log.d(TAG, "Limit max is: " + max + " max: " + mViewModel.gasLimitMax().intValue() + " min: " + mViewModel.gasLimitMin().intValue());

        mGasLimitSeekBar.setMax(mViewModel.gasLimitMax().subtract(mViewModel.gasLimitMin()).intValue());
        mGasLimitSeekBar.setProgress(mViewModel.gasLimitDefault().intValue());
        mGasLimitSeekBar.refreshDrawableState();
        mGasLimitSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Log.d(TAG, "onViewCreated 888 progress: " + progress);
                        mViewModel.gasLimitActual().setValue(BigInteger.valueOf(progress).add(mViewModel.gasLimitMin()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        mViewModel.gasPriceActual().observe(this, this::onGasPriceActual);
        mViewModel.gasLimitActual().observe(this, this::onGasLimitActual);
        mViewModel.defaultWallet().observe(this, this::onDefaultWallet);
        mViewModel.defaultWalletBalanceInWei().observe(this, this::onDefaultWalletBalanceInWei);
        mViewModel.transaction().observe(this, this::onPurchaseTransaction);
        mViewModel.isInProgress().observe(this, this::onIsInProgress);
        mViewModel.exception().observe(this, this::onException);

        mAccountText.setOnClickListener(this);
        findViewById(R.id.paymentButton).setOnClickListener(this);
        findViewById(R.id.changeWallIcon).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume 111");
        mViewModel.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (android.R.id.home == i) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if (R.id.changeWallIcon == i || R.id.accountAddress == i) {
            Log.d(TAG, "onClick 111");
            Intent intent = new Intent(this, BCWalletManagementActivity.class);
            startActivity(intent);
        } else if (R.id.paymentButton == i) {
            Log.d(TAG, "onClick 222");
            mViewModel.createTransaction(
                    mAccountText.getText().toString(),
                    "0x0721628278526f691b6122eeb74f5f088e8042e8",
                    mViewModel.propPrice(),
                    mViewModel.gasPriceActual().getValue(),
                    mViewModel.gasLimitActual().getValue());
        }
    }

    private void onGasPriceActual(BigInteger gasPrice) {
        Log.d(TAG, "onActualGasPrice 111: " + gasPrice.longValue());
        String price = BCBalanceUtils.weiToGwei(gasPrice);
        mGasPriceText.setText(price.toString());

        String cost = BCBalanceUtils.weiToEth(gasPrice.multiply(mViewModel.gasLimitActual().getValue())).toPlainString() + " " + BCConstants.SYMBOL_ETH;
        mTotalCostText.setText(cost);
    }

    private void onGasLimitActual(BigInteger gasLimit) {
        Log.d(TAG, "onGasLimitActual 111: " + gasLimit.longValue());
        mGasLimitText.setText(gasLimit.toString());

        String cost = BCBalanceUtils.weiToEth(mViewModel.gasPriceActual().getValue().multiply(gasLimit)).toPlainString() + " " + BCConstants.SYMBOL_ETH;
        mTotalCostText.setText(cost);
    }

    private void onDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onDefaultWallet 111: " + wallet.getAddress());
        mAccountText.setText(wallet.getAddress());
    }

    private void onDefaultWalletBalanceInWei(BigInteger balance) {

        Log.d(TAG, "onDefaultWalletBalanceInWei 111");

        try {
            BigDecimal b = BCBalanceUtils.weiToEth(balance, 4);
            mAccountBalanceText.setText(b.toPlainString());
        } catch (Exception ex) {
            Log.d(TAG, "onDefaultWalletBalanceInWei: " + ex);
        }
    }

    private void onPurchaseTransaction(String tx) {
        Log.d(TAG, "onPurchaseTransaction 111");
        hideDialog();
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_title_pruchasing_succeed)
                .setMessage(tx)
                .setPositiveButton(R.string.alert_dialog_button_ok, (dialog1, id) -> {
                    finish();
                })
                /*
                .setNeutralButton(R.string.copy, (dialog1, id) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("transaction hash", hash);
                    clipboard.setPrimaryClip(clip);
                    finish();
                })*/
                .create();
        mAlertDialog.show();

        Log.d(TAG, "onPurchaseTransaction 222");
        Intent broadcast = new Intent(BCConstants.LOCALBROADCAST_PAYMENT_SUCCEED);
        broadcast.putExtra(BCConstants.LOCALBROADCAST_PAYMENT_EXTRA_PROP_NAME, mPropNameText.getText().toString());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    private void onIsInProgress(boolean isInProgress) {
        Log.d(TAG, "onIsInProgress 111");
        hideDialog();
        if (isInProgress) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_dialog_title_purchasing)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            mAlertDialog.show();
        }
    }

    private void onException(BCException error) {
        Log.d(TAG, "onException 111");
        hideDialog();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_title_pruchasing_faild)
                .setMessage(error.getMessage())
                .setPositiveButton(R.string.alert_dialog_button_ok, (dialog1, id) -> {
                    // Do nothing
                })
                .create();
        dialog.show();

        Log.d(TAG, "onException 111");
        Intent broadcast = new Intent(BCConstants.LOCALBROADCAST_PAYMENT_FAIL);
        broadcast.putExtra(BCConstants.LOCALBROADCAST_PAYMENT_EXTRA_PROP_NAME, mPropNameText.getText().toString());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    private void hideDialog() {
        if (null != mAlertDialog && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
