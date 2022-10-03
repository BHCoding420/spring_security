package com.example.springsecuirityclient.controller;

import com.example.springsecuirityclient.entity.User;
import com.example.springsecuirityclient.entity.VerificationToken;
import com.example.springsecuirityclient.event.RegistrationCompleteEvent;
import com.example.springsecuirityclient.model.PasswordModel;
import com.example.springsecuirityclient.model.UserModel;
import com.example.springsecuirityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel); // adds user to db
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        )); //create token and send email
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid"))
        {
            return "user verified";
        }
        return "bad user";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {
        VerificationToken verificationToken
                = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,HttpServletRequest request)
    {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null)
        {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
            url = passwordResetTokenMail(user,applicationUrl(request),token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel)
    {
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid"))
        {
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "password reset successful";
        }
        else
        {
           return "Invalid Token";
        }

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl
                + "savePassword?token="
                + token;
        log.info("Click the link to reset password: {}",
                url);
        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl,VerificationToken token) {
        String url = applicationUrl
                + "verifyRegistration?token="
                + token.getToken();
        log.info("Click the link to verify your account: {}",
                url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/" +
                request.getContextPath();
    }
}
