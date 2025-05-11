package com.rocketshipcheckingtool.ui.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSession {
    private static UserRole role;
    private static final Logger logger = LoggerFactory.getLogger(UserSession.class);

    public static void setRole(UserRole r) {
        role = r;
        logger.info("User role set to '{}'", r);
    }

    public static UserRole getRole() {
        logger.debug("User role accessed: '{}'", role);
        return role;
    }
}
