package org.m7medmagdi.demo1;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Register all your resource classes
        classes.add(org.m7medmagdi.demo1.rest.TaskResource.class);
        classes.add(org.m7medmagdi.demo1.rest.AuthResource.class);

        // Register the CORS filter
        classes.add(org.m7medmagdi.demo1.config.CorsFilter.class);

        return classes;
    }
}