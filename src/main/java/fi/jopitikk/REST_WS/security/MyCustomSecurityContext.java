package fi.jopitikk.REST_WS.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import fi.jopitikk.REST_WS.model.User;

public class MyCustomSecurityContext implements SecurityContext {
    
    private User user;
    private String scheme;
    
    public MyCustomSecurityContext(User user, String scheme) {
        this.user = user;
        this.scheme = scheme;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.user;
    }

    @Override
    public boolean isSecure() {
        return "https".equals(this.scheme);
    }

    @Override
    public boolean isUserInRole(String role) {
        
        if (user.getRoles() != null) {
            return user.getRoles().contains(role);
        }
        return false;
    }
    

}
