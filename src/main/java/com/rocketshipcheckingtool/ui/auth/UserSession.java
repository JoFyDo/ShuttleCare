package com.rocketshipcheckingtool.ui.auth;

public class UserSession {
    private static UserRole role;

    public static void setRole(UserRole r) {
        role = r;
    }

    public static UserRole getRole() {
        return role;
    }
}
