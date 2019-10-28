package net.tospay.auth.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;

public class PaymentValidationResponse {

    @SerializedName("transaction")
    @Expose
    private Payment transaction;

    @SerializedName("merchant")
    @Expose
    private Merchant merchant;

    public Payment getTransaction() {
        return transaction;
    }

    public void setTransaction(Payment transaction) {
        this.transaction = transaction;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
