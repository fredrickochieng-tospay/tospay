package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.interfaces.AccountType;

public class Account implements Parcelable, AccountType {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("network")
    @Expose
    private String network;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("trunc")
    @Expose
    private String trunc;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("note")
    @Expose
    private String note;

    @SerializedName("alias")
    @Expose
    private String alias;

    @SerializedName("verified")
    @Expose
    private boolean verified;

    private String accountType;
    private boolean isChecked;

    public Account() {
    }

    protected Account(Parcel in) {
        id = in.readString();
        network = in.readString();
        avatar = in.readString();
        trunc = in.readString();
        state = in.readString();
        note = in.readString();
        alias = in.readString();
        verified = in.readByte() != 0;
        accountType = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTrunc() {
        return trunc;
    }

    public void setTrunc(String trunc) {
        this.trunc = trunc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int getType() {
        switch (accountType) {
            case "mobile":
                return AccountType.MOBILE;

            case "card":
                return AccountType.CARD;

            case "bank":
                return AccountType.BANK;

            default:
                return -1;

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(network);
        parcel.writeString(avatar);
        parcel.writeString(trunc);
        parcel.writeString(state);
        parcel.writeString(note);
        parcel.writeString(alias);
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeString(accountType);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
    }
}
