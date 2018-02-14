package lk.dialog.iot.pcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDto {

    private int id;
    private int deviceDefinitionId;
    private String brand;
    private String type;
    private String model;
    private String deviceCategory;
    private int userId;
    private int deviceParentId;
    private String macAddress;
    private String name;
    private String location;
    private int locationId;
    private String additionalParams;
    private String otherParameters;
    private boolean featured;
    private boolean nonDeletable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceDefinitionId() {
        return deviceDefinitionId;
    }

    public void setDeviceDefinitionId(int deviceDefinitionId) {
        this.deviceDefinitionId = deviceDefinitionId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDeviceParentId() {
        return deviceParentId;
    }

    public void setDeviceParentId(int deviceParentId) {
        this.deviceParentId = deviceParentId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(String additionalParams) {
        this.additionalParams = additionalParams;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public boolean isNonDeletable() {
        return nonDeletable;
    }

    public void setNonDeletable(boolean nonDeletable) {
        this.nonDeletable = nonDeletable;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOtherParameters() {
        return otherParameters;
    }

    public void setOtherParameters(String otherParameters) {
        this.otherParameters = otherParameters;
    }
}