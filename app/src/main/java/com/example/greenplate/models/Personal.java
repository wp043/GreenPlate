package com.example.greenplate.models;

public class Personal {
    private String height;
    private String weight;
    private String gender;

    public Personal(String height, String weight, String gender) {
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
