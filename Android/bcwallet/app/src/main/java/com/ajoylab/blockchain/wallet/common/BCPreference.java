package com.ajoylab.blockchain.wallet.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ajoylab.blockchain.wallet.services.BCWalletManager;

public class BCPreference
{
	private static final String TAG = "###BCPreference";

	private static final String PREF_CURRENT_WALLET_ACCOUNT = "pref_current_wallet_account";

	private static final BCPreference mInstance = new BCPreference();
	public static BCPreference getInstance() { return mInstance; }

	private SharedPreferences mPref = null;

	public BCPreference() {}

	public void init(Context context) {
		Log.d(TAG, "init 111: " + Thread.currentThread().getId());
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getCurrentWallet() {
		Log.d(TAG, "getCurrentWallet 111: " + Thread.currentThread().getId());
		if (null == mPref)
			return "";

		Log.d(TAG, "getCurrentWallet 222: " + mPref.getString(PREF_CURRENT_WALLET_ACCOUNT, null));
		return mPref.getString(PREF_CURRENT_WALLET_ACCOUNT, null);
	}

	public void setCurrentWallet(String address) {
		Log.d(TAG, "setCurrentWallet 111 address: " + address);
		if (null == mPref)
			return;

		Log.d(TAG, "setCurrentWallet 222");

		mPref.edit().putString(PREF_CURRENT_WALLET_ACCOUNT, address).apply();
	}
}
