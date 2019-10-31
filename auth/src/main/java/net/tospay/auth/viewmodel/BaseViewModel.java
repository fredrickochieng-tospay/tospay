package net.tospay.auth.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import net.tospay.auth.TospayAuth;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.app.SharedPrefManager;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel<N> extends ViewModel {

    private final ObservableBoolean mIsLoading = new ObservableBoolean();
    private final ObservableBoolean mIsError = new ObservableBoolean(false);
    private final ObservableField<String> mErrorMessage = new ObservableField<>("");

    private WeakReference<N> mNavigator;

    private TospayAuth mTospayAuth;
    private TospayGateway mTospayGateway;
    private SharedPrefManager mSharedPrefManager;

    public BaseViewModel() {
    }

    public BaseViewModel(TospayAuth tospayAuth) {
        this.mTospayAuth = tospayAuth;
    }

    public BaseViewModel(TospayGateway mTospayGateway) {
        this.mTospayGateway = mTospayGateway;
    }

    public BaseViewModel(SharedPrefManager mSharedPrefManager) {
        this.mSharedPrefManager = mSharedPrefManager;
    }

    public BaseViewModel(TospayAuth tospayAuth, TospayGateway mTospayGateway) {
        this.mTospayAuth = tospayAuth;
        this.mTospayGateway = mTospayGateway;
    }

    public BaseViewModel(TospayAuth mTospayAuth, SharedPrefManager mSharedPrefManager) {
        this.mTospayAuth = mTospayAuth;
        this.mSharedPrefManager = mSharedPrefManager;
    }

    public TospayAuth getTospayAuth() {
        return mTospayAuth;
    }

    public TospayGateway getTospayGateway() {
        return mTospayGateway;
    }

    public SharedPrefManager getSharedPrefManager() {
        return mSharedPrefManager;
    }

    public N getNavigator() {
        return mNavigator.get();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public ObservableBoolean getIsError() {
        return mIsError;
    }

    public void setIsError(boolean isError) {
        mIsError.set(isError);
    }

    public ObservableField<String> getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage.set(errorMessage);
    }
}
