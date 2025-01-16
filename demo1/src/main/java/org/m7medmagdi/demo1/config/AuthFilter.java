package org.m7medmagdi.demo1.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class AuthFilter implements ContainerRequestFilter {
    private static final String SESSION_HEADER = "X-Session-ID";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Skip authentication for public endpoints (e.g., register, login)
        if (requestContext.getUriInfo().getPath().startsWith("auth")) {
            return;
        }

        // Get the session ID from the header
        String sessionId = requestContext.getHeaderString(SESSION_HEADER);
        if (sessionId == null || !SessionManager.isValidSession(sessionId)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Unauthorized\"}")
                    .build());
        }
    }
}