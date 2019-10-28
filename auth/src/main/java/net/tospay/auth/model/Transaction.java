package net.tospay.auth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Transaction {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("source_channel")
    @Expose
    private String sourceChannel;

    @SerializedName("requested_amount")
    @Expose
    private String requestedAmount;

    @SerializedName("requested_currency")
    @Expose
    private String requestedCurrency;

    @SerializedName("collected_amount")
    @Expose
    private String collectedAmount;

    @SerializedName("collected_currency")
    @Expose
    private String collectedCurrency;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("transfer_type")
    @Expose
    private String transferType;

    public Transaction() {
    }

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

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(String requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getRequestedCurrency() {
        return requestedCurrency;
    }

    public void setRequestedCurrency(String requestedCurrency) {
        this.requestedCurrency = requestedCurrency;
    }

    public String getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getCollectedCurrency() {
        return collectedCurrency;
    }

    public void setCollectedCurrency(String collectedCurrency) {
        this.collectedCurrency = collectedCurrency;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
