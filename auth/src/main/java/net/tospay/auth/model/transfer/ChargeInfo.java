package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeInfo {

    @SerializedName("destination")
    @Expose
    private Destination destination;

    @SerializedName("partnerInfo")
    @Expose
    private PartnerInfo partnerInfo;

    @SerializedName("railInfo")
    @Expose
    private RailInfo railInfo;

    @SerializedName("source")
    @Expose
    private Source source;

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public PartnerInfo getPartnerInfo() {
        return partnerInfo;
    }

    public void setPartnerInfo(PartnerInfo partnerInfo) {
        this.partnerInfo = partnerInfo;
    }

    public RailInfo getRailInfo() {
        return railInfo;
    }

    public void setRailInfo(RailInfo railInfo) {
        this.railInfo = railInfo;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
