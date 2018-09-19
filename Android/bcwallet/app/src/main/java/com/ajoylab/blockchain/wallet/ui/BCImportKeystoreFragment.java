package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
    private EditText keystore = null;
    private EditText password = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(R.layout.fragment_import_keystore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        keystore = view.findViewById(R.id.keystore);
        password = view.findViewById(R.id.password);
        view.findViewById(R.id.import_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.keystore.setError(null);
        String keystore = this.keystore.getText().toString();
        String password = this.password.getText().toString();
        if (TextUtils.isEmpty(keystore)) {
            this.keystore.setError(getString(R.string.error_field_required));
        } else {
            BCImportWalletViewModel model = ViewModelProviders.of(getActivity()).get(BCImportWalletViewModel.class);
            model.OnImportKeystoreClicked(keystore, password);
        }
    }
}
