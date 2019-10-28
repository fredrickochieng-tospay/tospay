package net.tospay.auth.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Account;

public class PaymentRequest {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("payment_token")
    @Expose
    private String paymentToken;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
