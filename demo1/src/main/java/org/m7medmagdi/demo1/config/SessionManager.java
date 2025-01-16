package org.m7medmagdi.demo1.config;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    // sessionId -> userId
    private static final Map<String, Long> activeSessions = new HashMap<>();

    // Create a new session and store the user ID
    public static String createSession(Long userId) {
        String sessionId = java.util.UUID.randomUUID().toString();
        activeSessions.put(sessionId, userId);
        return sessionId;
    }

    // Check if a session is valid
    public static boolean isValidSession(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    // Get the user ID from the session
    public static Long getUserId(String sessionId) {
        return activeSessions.get(sessionId);
    }

    // Invalidate (delete) a session
    public static void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}