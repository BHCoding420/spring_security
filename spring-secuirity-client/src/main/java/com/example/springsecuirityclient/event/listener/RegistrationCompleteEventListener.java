package com.example.springsecuirityclient.event.listener;

import com.example.springsecuirityclient.entity.User;
import com.example.springsecuirityclient.event.RegistrationCompleteEvent;
import com.example.springsecuirityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event)
    {
        //Create verification token for User
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user); // save token in token repo

        //send Mail to User
        String url = event.getApplicationUrl()
                + "verifyRegistration?token="
                + token;
        log.info("Click the link to verify your account: {}",
                 url);
    }
}
