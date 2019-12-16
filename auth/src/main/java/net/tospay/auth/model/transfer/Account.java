package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

public class Account implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("country")
    @Expose
    private Country country;

    @SerializedName("network")
    @Expose
    private Network network;

    public Account() {
    }

    public Account(String id, String type, String currency) {
        this.id = id;
        this.type = type;
        this.currency = currency;
    }

    protected Account(Parcel in) {
        id = in.readString();
        type = in.readString();
        currency = in.readString();
        country = in.readParcelable(Country.class.getClassLoader());
        network = in.readParcelable(Network.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(currency);
        dest.writeParcelable(country, flags);
        dest.writeParcelable(network, flags);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }

        Account account = (Account) obj;
        return this.id.equals(account.getId());
    }
}
