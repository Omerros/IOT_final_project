package com.example.chaquopy_tutorial;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class DogProfile implements Serializable {
    private int id;
    private String name;
    private String breed;
    private String photoPath;
    private int targetSteps;
    private Map<String, List<Object>> deviceData;
    private String alarm;
    private String inOut;
    private String lightDark;
    private String wifi;
    public DogProfile() {}

    public DogProfile(int id, String name, String breed, String photoPath, int targetSteps, Map<String, List<Object>> deviceData, String alarm, String inOut, String lightDark, String wifi) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.photoPath = photoPath;
        this.targetSteps = targetSteps;
        this.deviceData = deviceData;
        this.alarm = alarm;
        this.inOut = inOut;
        this.lightDark = lightDark;
        this.wifi = wifi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getTargetSteps() {
        return targetSteps;
    }

    public void setTargetSteps(int targetSteps) {
        this.targetSteps = targetSteps;
    }

    public Map<String, List<Object>> getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(Map<String, List<Object>>deviceData) {
        this.deviceData = deviceData;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getInOut() {
        return inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    public String getLightDark() {
        return lightDark;
    }

    public void setLightDark(String lightDark) {
        this.lightDark = lightDark;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.name = wifi;
    }

    public String toString() {
        return "DogProfile { id = " + id + ", name = " + name + ", breed = " + breed + ", targetSteps = " + targetSteps + ", deviceData = " + deviceData + ", alarm = " + alarm + " }";
    }
}
