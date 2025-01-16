package org.m7medmagdi.demo1.rest;




import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.m7medmagdi.demo1.config.SessionManager;
import org.m7medmagdi.demo1.dao.UserDAO;
import org.m7medmagdi.demo1.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

@Path("/auth")
public class AuthResource {
    private final UserDAO userDAO = new UserDAO();



    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Email and password are required\"}")
                    .build();
        }

        // Hash the password before saving
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setCreatedAt(new Date());

        userDAO.save(user);
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"User registered successfully\"}")
                .build();

    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Email and password are required\"}")
                    .build();
        }

        if (userDAO.validatePassword(user.getEmail(), user.getPassword())) {
            // Find the user by email
            User loggedInUser = userDAO.findByEmail(user.getEmail());

            // Create a session and store the user ID
            String sessionId = SessionManager.createSession(loggedInUser.getId());

            // Return the session ID to the client
            return Response.ok()
                    .entity("{\"sessionId\": \"" + sessionId + "\"}")
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid credentials\"}")
                    .build();
        }
    }
}