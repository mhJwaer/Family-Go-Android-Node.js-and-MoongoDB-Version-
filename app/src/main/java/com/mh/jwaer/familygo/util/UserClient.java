package com.mh.jwaer.familygo.util;

import com.mh.jwaer.familygo.data.models.UserModel;

public class UserClient {
    private static UserModel user = null;

    private UserClient() {

    }

    public static UserModel getUser() {
        if (user == null) {
            user = new UserModel();
        }
        return user;
    }

    public static void setUser(UserModel user) {
        UserClient.user = user;
    }

}
