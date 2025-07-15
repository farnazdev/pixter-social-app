package com.example.socialnetwork.controller.adapters;

import com.example.socialnetwork.model.User;

public class UserManager {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static boolean isUserLoaded() {
        return currentUser != null;
    }


}
