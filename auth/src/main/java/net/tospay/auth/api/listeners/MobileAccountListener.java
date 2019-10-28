package net.tospay.auth.api.listeners;

public interface MobileAccountListener extends BaseListener {

    void onAccountAdded(String accountId);
}
