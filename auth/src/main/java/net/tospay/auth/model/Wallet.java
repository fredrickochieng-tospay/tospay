package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.interfaces.AccountType;

import java.math.BigDecimal;

public class Wallet implements Parcelable, AccountType {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("account_balance")
    @Expose
    private Double accountBalance;

    @SerializedName("last_balance")
    @Expose
    private Double lastBalance;

    public Wallet() {

    }

    protected Wallet(Parcel in) {
        id = in.readString();
        currency = in.readString();
        if (in.readByte() == 0) {
            accountBalance = null;
        } else {
            accountBalance = in.readDouble();
        }
        if (in.readByte() == 0) {
            lastBalance = null;
        } else {
            lastBalance = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(currency);
        if (accountBalance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(accountBalance);
        }
        if (lastBalance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lastBalance);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Double getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(Double lastBalance) {
        this.lastBalance = lastBalance;
    }

    @Override
    public int getType() {
        return AccountType.WALLET;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", currency='" + currency + '\'' +
                ", accountBalance=" + accountBalance +
                ", lastBalance=" + lastBalance +
                '}';
    }
}
