package net.tospay.auth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Transaction {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    @SerializedName("transactionTransferId")
    @Expose
    private String transactionTransferId;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("source_channel")
    @Expose
    private String sourceChannel;

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("charge")
    @Expose
    private String charge;

    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    @SerializedName("date_updated")
    @Expose
    private String dateUpdated;

    @SerializedName("status")
    @Expose
    private String status;

    public Transaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTransferId() {
        return transactionTransferId;
    }

    public void setTransactionTransferId(String transactionTransferId) {
        this.transactionTransferId = transactionTransferId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
