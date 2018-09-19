package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.model.BCViewPagerData;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;
import com.ajoylab.blockchain.wallet.viewmodel.BCImportWalletViewModel;
import com.ajoylab.blockchain.wallet.viewmodel.BCPaymentViewModel;
import com.ajoylab.blockchain.wallet.viewmodel.BCWalletManagementViewModel;

/**
 * Created by liuya on 2018/1/25.
 */

public class BCWalletManagementActivity extends BCBaseActivity implements BCWalletManagementAdapter.OnSetDefaultWalletListener
{
    private static final String TAG = "###BCWalletManage";

    private BCWalletManagementAdapter mAdapter;
    private BCWalletManagementViewModel mViewModel;
    private TextView mNoWalletText;

    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(R.layout.fragment_payment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoWalletText = view.findViewById(R.id.noWalletText);

        mAdapter = new BCWalletManagementAdapter(this);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh_layout);

        RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(getActivity()).get(BCWalletManagementViewModel.class);
        mViewModel.getWalletList();

        mViewModel.walletList().observe(this, this::onGetWalletList);
        refreshLayout.setOnRefreshListener(mViewModel::getWalletList);
    }
    */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate 111");

        setContentView(R.layout.activity_wallet_management);
        setupToolbar();

        mNoWalletText = findViewById(R.id.noWalletText);

        mAdapter = new BCWalletManagementAdapter(this);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(BCWalletManagementViewModel.class);
        mViewModel.getWalletList();

        mViewModel.walletList().observe(this, this::onGetWalletList);
        refreshLayout.setOnRefreshListener(mViewModel::getWalletList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_import, menu);
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (R.menu.menu_import == i) {
            Log.d(TAG, "onOptionsItemSelected 111");
        } else if (R.menu.menu_add == i) {
            Log.d(TAG, "onOptionsItemSelected 222");
        } else if (android.R.id.home == i) {
            Log.d(TAG, "onOptionsItemSelected 333");
            onBackPressed();
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected 444");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetDefault(BCWalletData wallet) {
        Log.d(TAG, "onSetDefault 111");
        mViewModel.setDefaultWallet(wallet);
        mAdapter.setDefaultWallet(wallet);
    }

    private void onGetWalletList(BCWalletData[] wallets) {
        Log.d(TAG, "onGetWalletList 111");
        if (wallets == null || wallets.length == 0) {
            Log.d(TAG, "onCreate 222");
            /*
            dissableDisplayHomeAsUp();
            AddWalletView addWalletView = new AddWalletView(this, R.layout.layout_empty_add_account);
            addWalletView.setOnNewWalletClickListener(this);
            addWalletView.setOnImportWalletClickListener(this);
            systemView.showEmpty(addWalletView);
            adapter.setWallets(new Wallet[0]);
            hideToolbar();
            */
            mNoWalletText.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate 333");
            //enableDisplayHomeAsUp();
            mNoWalletText.setVisibility(View.GONE);
            mAdapter.setWallets(wallets);
        }
        //invalidateOptionsMenu();
    }
}
