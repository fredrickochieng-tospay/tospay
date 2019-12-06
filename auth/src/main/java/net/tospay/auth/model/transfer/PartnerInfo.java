package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PartnerInfo {

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
