package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.QrResponse;

public interface QrListener extends BaseListener {

    void onSuccess(QrResponse response);
}
