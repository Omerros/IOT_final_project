package com.example.chaquopy_tutorial;

import java.io.Serializable;

public class DogProfile implements Serializable {
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

    // Getters and setters
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
}
