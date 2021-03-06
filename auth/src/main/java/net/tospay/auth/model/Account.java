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

    private int accountType;

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
        accountType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(network);
        dest.writeString(avatar);
        dest.writeString(trunc);
        dest.writeString(state);
        dest.writeString(note);
        dest.writeString(alias);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeInt(accountType);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    @Override
    public int getType() {
        return accountType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", network='" + network + '\'' +
                ", avatar='" + avatar + '\'' +
                ", trunc='" + trunc + '\'' +
                ", state='" + state + '\'' +
                ", note='" + note + '\'' +
                ", alias='" + alias + '\'' +
                ", verified=" + verified +
                ", accountType=" + accountType +
                '}';
    }
}
