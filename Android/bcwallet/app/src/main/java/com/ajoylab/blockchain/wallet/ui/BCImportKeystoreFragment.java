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

public class BCImportKeystoreFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "###BCImportKeystoreFR";

    private EditText mKeystore;
    private EditText mPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(R.layout.fragment_import_keystore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mKeystore = view.findViewById(R.id.keystore);
        mPassword = view.findViewById(R.id.password);
        view.findViewById(R.id.import_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick 111");
        mKeystore.setError(null);
        String keystore = mKeystore.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(keystore)) {
            mKeystore.setError(getString(R.string.error_field_required));
        } else {
            BCImportWalletViewModel model = ViewModelProviders.of(getActivity()).get(BCImportWalletViewModel.class);
            model.OnImportKeystoreClicked(keystore, password);
        }
    }
}
