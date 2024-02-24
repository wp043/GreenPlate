package com.example.greenplate.viewmodels;

import com.example.greenplate.models.User;

public class LoginViewModel {
    private static LoginViewModel instance;
    private User user;

    public LoginViewModel() {
        user = new User();
    }

    // DO NOT MODIFY METHOD
    public static synchronized LoginViewModel getInstance() {
        if (instance == null) {
            instance = new LoginViewModel();
        }
        return instance;
    }
}
