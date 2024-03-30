package com.example.chaquopy_tutorial;
import java.util.Map;
import java.io.Serializable;

public class DogProfile implements Serializable {
    private int id;
    private String name;
    private String breed;
    private String photoPath;
    private int targetSteps;
    private Map<String, Integer> stepsData;

    public DogProfile() {}

    public DogProfile(int id, String name, String breed, String photoPath, int targetSteps) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.photoPath = photoPath;
        this.targetSteps = targetSteps;
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

    public Map<String, Integer> getStepsData() {
        return stepsData;
    }

    public void setStepsData(Map<String, Integer> stepsData) {
        this.stepsData = stepsData;
    }

    public String toString() {
        return "DogProfile { id = " + id + ", name = " + name + ", breed = " + breed + ", targetSteps = " + targetSteps + ", stepsData = " + stepsData + " }";
    }
}
