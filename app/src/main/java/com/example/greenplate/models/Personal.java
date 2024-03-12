package com.example.greenplate.models;

public class Personal {
    private String age;
    private String height;
    private String weight;
    private String gender;

    public Personal(String age, String height, String weight, String gender) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }

    public String getAge() {
        return this.age;
    }
    public void setAge(String age) {
        this.age = age;
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
