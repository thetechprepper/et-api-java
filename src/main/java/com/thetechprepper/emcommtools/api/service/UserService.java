package com.thetechprepper.emcommtools.api.service;

import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.thetechprepper.emcommtools.api.model.User;

@Service
public class UserService {

    private static final String USER_CONFIG_PATH = System.getProperty("user.home") + "/.config/emcomm-tools/user.json";

    private final Gson gson = new Gson();

    public User getUserConfig() throws IOException {

        try (FileReader reader = new FileReader(USER_CONFIG_PATH)) {
            return gson.fromJson(reader, User.class);
        }
    }
}