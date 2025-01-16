package org.m7medmagdi.demo1.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.m7medmagdi.demo1.config.SessionManager;
import org.m7medmagdi.demo1.dao.TaskDAO;
import org.m7medmagdi.demo1.model.Task;

import java.util.List;

@Path("/tasks")
public class TaskResource {
    private final TaskDAO taskDAO = new TaskDAO();

    @OPTIONS
    @Path("{path: .*}") // Match all paths
    public Response handleOptions(@PathParam("path") String path) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*") // Allow all origins
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS") // Allowed methods
                .header("Access-Control-Allow-Headers", "Content-Type, X-Session-ID") // Allowed headers
                .build();
    }

    // Add CORS headers to the response
    private Response.ResponseBuilder addCorsHeaders(Response.ResponseBuilder response) {
        return response.header("Access-Control-Allow-Origin", "*") // Allow all origins
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS") // Allowed methods
                .header("Access-Control-Allow-Headers", "Content-Type, X-Session-ID"); // Allowed headers
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@HeaderParam("X-Session-ID") String sessionId) {
        // Check if the session is valid
        if (!SessionManager.isValidSession(sessionId)) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized0\"}").build();
        }

        // Get the logged-in user's ID from the session
        Long userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized1\"}").build();
        }

        // Retrieve all tasks for the logged-in user
        List<Task> tasks = taskDAO.findByUser(userId);
        return addCorsHeaders(Response.ok(tasks)).build(); // Add CORS headers
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(@HeaderParam("X-Session-ID") String sessionId, Task task) {
        // Check if the session is valid
        if (!SessionManager.isValidSession(sessionId)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("Access-Control-Allow-Origin", "*") // Add CORS header
                    .entity("{\"message\": \"Unauthorized0\"}")
                    .build();
        }

        // Get the logged-in user's ID from the session
        Long userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("Access-Control-Allow-Origin", "*") // Add CORS header
                    .entity("{\"message\": \"Unauthorized1\"}")
                    .build();
        }

        // Validate the task object
        if (task == null || task.getTitle() == null || task.getTitle().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*") // Add CORS header
                    .entity("{\"message\": \"Task title is required\"}")
                    .build();
        }

        // Link the task to the logged-in user and save it
        try {
            taskDAO.save(task, userId);
            return Response.status(Response.Status.CREATED)
                    .header("Access-Control-Allow-Origin", "*") // Add CORS header
                    .entity("{\"message\": \"Task created successfully\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*") // Add CORS header
                    .entity("{\"message\": \"Failed to create task\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(
            @HeaderParam("X-Session-ID") String sessionId,
            @PathParam("id") Long taskId,
            Task updatedTask
    ) {
        // Check if the session is valid
        if (!SessionManager.isValidSession(sessionId)) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized0\"}").build();
        }

        // Get the logged-in user's ID from the session
        Long userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized1\"}").build();
        }

        // Find the task and ensure it belongs to the logged-in user
        Task existingTask = taskDAO.findById(taskId, userId);
        if (existingTask == null) {
            return addCorsHeaders(Response.status(Response.Status.NOT_FOUND)).entity("{\"message\": \"Task not found\"}").build();
        }

        // Update the task properties
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        // Save the updated task
        taskDAO.update(existingTask, userId);
        return addCorsHeaders(Response.ok()).build(); // Add CORS headers
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(
            @HeaderParam("X-Session-ID") String sessionId,
            @PathParam("id") Long taskId
    ) {
        // Check if the session is valid
        if (!SessionManager.isValidSession(sessionId)) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized0\"}").build();
        }

        // Get the logged-in user's ID from the session
        Long userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return addCorsHeaders(Response.status(Response.Status.UNAUTHORIZED)).entity("{\"message\": \"Unauthorized1\"}").build();
        }

        // Find the task and ensure it belongs to the logged-in user
        Task task = taskDAO.findById(taskId, userId);
        if (task == null) {
            return addCorsHeaders(Response.status(Response.Status.NOT_FOUND)).entity("{\"message\": \"Task not found\"}").build();
        }

        // Delete the task
        taskDAO.delete(taskId, userId);
        return addCorsHeaders(Response.noContent()).build(); // Add CORS headers
    }


}