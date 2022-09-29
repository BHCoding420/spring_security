package com.example.springsecuirityclient.service;

import com.example.springsecuirityclient.entity.User;
import com.example.springsecuirityclient.entity.VerificationToken;
import com.example.springsecuirityclient.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);
}
