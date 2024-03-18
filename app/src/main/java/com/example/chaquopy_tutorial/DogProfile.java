package com.example.chaquopy_tutorial;

public class DogProfile {
    private String name;
    private String breed;
    private String photoPath;
    private int targetSteps;

    public DogProfile(String name, String breed, String photoPath, int targetSteps) {
        this.name = name;
        this.breed = breed;
        this.photoPath = photoPath;
        this.targetSteps = targetSteps;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public int getTargetSteps() {
        return targetSteps;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setTargetSteps(int targetSteps) {
        this.targetSteps = targetSteps;
    }
}
