package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.viewmodel.BCImportWalletViewModel;

/**
 * Created by liuya on 2018/1/17.
 */

public class BCImportPrivateKeyFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "###BCImportPrivateKeyFR";

    private EditText mPrivateKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import_private_key, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrivateKey = view.findViewById(R.id.private_key);
        view.findViewById(R.id.import_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick 111");
        mPrivateKey.setError(null);
        String key = mPrivateKey.getText().toString();
        if (TextUtils.isEmpty(key) || key.length() != 64) {
            mPrivateKey.setError(getString(R.string.error_field_required));
        } else {
            BCImportWalletViewModel model = ViewModelProviders.of(getActivity()).get(BCImportWalletViewModel.class);
            model.OnImportPrivateKeyClicked(key);
        }
    }
}
