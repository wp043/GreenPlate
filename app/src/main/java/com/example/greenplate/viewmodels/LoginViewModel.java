package com.example.greenplate.viewmodels;

import com.example.greenplate.models.User;

public class LoginViewModel {
    private static LoginViewModel instance;
    final private User user;

    public LoginViewModel() {
        user = new User();
    }

    // DO NOT MODIFY METHOD
    public void setUser(String email, String password) {
        user.setEmail(email);
        user.setPassword(password);
    }
}
