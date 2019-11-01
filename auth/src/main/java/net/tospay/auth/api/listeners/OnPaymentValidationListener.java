package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.PaymentResult;

public interface OnPaymentValidationListener extends BaseListener {

    void onValidationSuccess(PaymentResult response);

}
