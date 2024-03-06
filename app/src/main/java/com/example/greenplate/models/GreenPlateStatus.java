package com.example.greenplate.models;

public class GreenPlateStatus {
    private final boolean success;
    private final String message;

    public GreenPlateStatus(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return String.format("%s: %s", success ? "Success" : "Fail", message);
    }
}
