package fi.jopitikk.REST_WS.app;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.services.UserService;

@ApplicationPath("webapi")
public class MyAppReg extends ResourceConfig {

    public MyAppReg() {
        
        register(RolesAllowedDynamicFeature.class);
        
    }
}
