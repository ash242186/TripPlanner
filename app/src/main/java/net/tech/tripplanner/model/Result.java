package net.tech.tripplanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwini on 4/8/2017.
 */

public class Result {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("categoryNames")
    @Expose
    private List<Object> categoryNames = new ArrayList<Object>();
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cityId")
    @Expose
    private String cityId;
    @SerializedName("xid")
    @Expose
    private Long xid;
    @SerializedName("keyImageUrl")
    @Expose
    private String keyImageUrl;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("minimumPrice")
    @Expose
    private Double minimumPrice;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("howToReach")
    @Expose
    private String howToReach;
    @SerializedName("whyToVisit")
    @Expose
    private String whyToVisit;
    @SerializedName("url")
    @Expose
    private String url;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Object> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<Object> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Long getXid() {
        return xid;
    }

    public void setXid(Long xid) {
        this.xid = xid;
    }

    public String getKeyImageUrl() {
        return keyImageUrl;
    }

    public void setKeyImageUrl(String keyImageUrl) {
        this.keyImageUrl = keyImageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(Double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHowToReach() {
        return howToReach;
    }

    public void setHowToReach(String howToReach) {
        this.howToReach = howToReach;
    }

    public String getWhyToVisit() {
        return whyToVisit;
    }

    public void setWhyToVisit(String whyToVisit) {
        this.whyToVisit = whyToVisit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Result{" +
                "address='" + address + '\'' +
                ", categoryNames=" + categoryNames +
                ", cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", description='" + description + '\'' +
                ", cityId='" + cityId + '\'' +
                ", xid=" + xid +
                ", keyImageUrl='" + keyImageUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", minimumPrice=" + minimumPrice +
                ", name='" + name + '\'' +
                ", stateName='" + stateName + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", id='" + id + '\'' +
                ", howToReach='" + howToReach + '\'' +
                ", whyToVisit='" + whyToVisit + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
