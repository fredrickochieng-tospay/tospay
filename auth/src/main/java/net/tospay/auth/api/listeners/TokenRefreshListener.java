package net.tospay.auth.api.listeners;

public interface TokenRefreshListener {

    void onSuccess();

    void onFailed();
}
