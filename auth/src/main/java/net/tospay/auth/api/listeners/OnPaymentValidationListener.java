package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.PaymentValidationResponse;

public interface OnPaymentValidationListener extends BaseListener {

    void onValidationSuccess(PaymentValidationResponse response);

}
