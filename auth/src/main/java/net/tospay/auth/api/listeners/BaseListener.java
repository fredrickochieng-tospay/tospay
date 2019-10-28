package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.TospayException;

public interface BaseListener {

    void onError(TospayException error);

    //call this method if user is unauthenticated
    default void onUnAuthenticated() {

    }
}
