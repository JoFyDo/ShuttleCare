package com.rocketshipcheckingtool.ui.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The UserSession class manages the current user's session, specifically their role.
 * It provides static methods to set and retrieve the user's role.
 */
public class UserSession {
    private static UserRole role; // The current user's role in the session.
    private static final Logger logger = LoggerFactory.getLogger(UserSession.class); // Logger instance for logging session activities.

    /**
     * Sets the user's role for the current session.
     *
     * @param r The UserRole to set for the session.
     */
    public static void setRole(UserRole r) {
        role = r;
        logger.info("User role set to '{}'", r);
    }

    /**
     * Retrieves the user's role for the current session.
     *
     * @return The UserRole of the current session.
     */
    public static UserRole getRole() {
        logger.debug("User role accessed: '{}'", role);
        return role;
    }
}