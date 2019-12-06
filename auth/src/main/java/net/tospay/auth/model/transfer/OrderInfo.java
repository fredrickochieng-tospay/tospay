package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderInfo {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    @SerializedName("callback_url")
    @Expose
    private String callbackUrl;

    @SerializedName("collection_channels")
    @Expose
    private String collectionChannels;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("type")
    @Expose
    private String type;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCollectionChannels() {
        return collectionChannels;
    }

    public void setCollectionChannels(String collectionChannels) {
        this.collectionChannels = collectionChannels;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
