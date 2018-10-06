package com.ajoylab.blockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import android.util.Log;

import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;

import io.reactivex.disposables.Disposable;

public class BCViewModelBase extends ViewModel {

    private static final String TAG = "###BCViewModelBase";

    protected final MutableLiveData<BCException> mException = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> mIsInProgress = new MutableLiveData<>();
    protected Disposable mDisposable;

    @Override
    protected void onCleared() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public LiveData<BCException> exception() { return mException; }
    public LiveData<Boolean> isInProgress() {
        return mIsInProgress;
    }

    protected void onError(Throwable throwable) {
        Log.d(TAG, "onError XXX: ", throwable);

        if (throwable instanceof BCException) {
            mException.postValue((BCException)throwable);
        }





        /*
        if (null == throwable.getCause() || TextUtils.isEmpty(throwable.getCause().getMessage())) {
            Log.d(TAG, "onError 111: "+ throwable.getCause().getMessage());
            mException.postValue(new BCException(BCConstants.ERROR_CODE_UNKNOWN, null, throwable));
        } else {
            Log.d(TAG, "onError 222: "+ throwable.getMessage());
            mException.postValue(new BCException(BCConstants.ERROR_CODE_UNKNOWN, throwable.getMessage(), throwable));
        }
        */
    }
}
