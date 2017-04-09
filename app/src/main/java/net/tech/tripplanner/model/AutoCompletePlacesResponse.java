package net.tech.tripplanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ashwini on 4/8/2017.
 */

public class AutoCompletePlacesResponse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("xid")
    @Expose
    private Long xid;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("ct")
    @Expose
    private String ct;
    @SerializedName("st")
    @Expose
    private String st;
    @SerializedName("co")
    @Expose
    private String co;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getXid() {
        return xid;
    }

    public void setXid(Long xid) {
        this.xid = xid;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    /*@Override
    public String toString() {
        return "AutoCompletePlacesResponse{" +
                "id='" + id + '\'' +
                ", xid=" + xid +
                ", lon=" + lon +
                ", lat=" + lat +
                ", text='" + text + '\'' +
                ", url='" + url + '\'' +
                ", ct='" + ct + '\'' +
                ", st='" + st + '\'' +
                ", co='" + co + '\'' +
                '}';
    }*/

    @Override
    public String toString() {
        return text;
    }
}
