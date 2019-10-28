package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;

public interface OnPaymentValidationListener extends BaseListener {

    void onValidationSuccess(PaymentValidationResponse response);

}
