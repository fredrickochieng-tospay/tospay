package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

public class Account {

    @SerializedName("country")
    @Expose
    private Country country;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("network")
    @Expose
    private Network network;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("user_type")
    @Expose
    private String userType;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
