package net.tospay.auth.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;

public class PaymentResult {

    @SerializedName("transaction")
    @Expose
    private PaymentTransaction paymentTransaction;

    @SerializedName("merchant")
    @Expose
    private Merchant merchant;

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "paymentTransaction=" + paymentTransaction +
                ", merchant=" + merchant +
                '}';
    }
}
