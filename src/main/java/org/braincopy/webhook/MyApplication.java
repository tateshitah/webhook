package org.braincopy.webhook;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("ws")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        packages("org.braincopy");
    }
}