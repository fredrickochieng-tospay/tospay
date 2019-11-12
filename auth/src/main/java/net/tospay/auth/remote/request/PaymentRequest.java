package net.tospay.auth.remote.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentRequest implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("account_id")
    @Expose
    private String accountId;

    @SerializedName("payment_token")
    @Expose
    private String paymentToken;

    public PaymentRequest() {
    }

    protected PaymentRequest(Parcel in) {
        type = in.readString();
        accountId = in.readString();
        paymentToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(accountId);
        dest.writeString(paymentToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentRequest> CREATOR = new Creator<PaymentRequest>() {
        @Override
        public PaymentRequest createFromParcel(Parcel in) {
            return new PaymentRequest(in);
        }

        @Override
        public PaymentRequest[] newArray(int size) {
            return new PaymentRequest[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}
