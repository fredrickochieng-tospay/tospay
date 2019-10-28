package net.tospay.auth.api.listeners;


import net.tospay.auth.model.TospayUser;

public interface UserListener extends BaseListener {

    void onUser(TospayUser user);
}
