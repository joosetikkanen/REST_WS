package fi.jopitikk.REST_WS.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import static fi.jopitikk.REST_WS.security.SecretService.*;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BASIC_AUTHORIZATION_HEADER_PREFIX = "Basic ";
    private static final String JWT_AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    //private static final String SECURED_URL_PREFIX = "secured";
    
    //private static final ErrorMessage UNAUTHORIZED_ErrMESSAGE = new ErrorMessage("User cannot access the resource.", 401, "http://myDocs.org");

    @Context private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        UserService userService = new UserService();
        User user = null;
        List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
        

        if (authHeader != null && authHeader.size() > 0) {
            
            // Basic authentication
            if (authHeader.get(0).contains(BASIC_AUTHORIZATION_HEADER_PREFIX)) {
                
                String authToken = authHeader.get(0);
                authToken = authToken.replaceFirst(BASIC_AUTHORIZATION_HEADER_PREFIX, "");
                String decodedString = new String(Base64.getDecoder().decode(authToken));
                StringTokenizer tokenizer = new StringTokenizer(decodedString,":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();
                
                if (userService.userCredentialExists(username, password)) {
                    user = userService.getUserByUsername(username);
                    String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
                    requestContext.setSecurityContext(new MyCustomSecurityContext(user, scheme));
                }
            }
            
            // JWT authentication
            else if (authHeader.get(0).contains(JWT_AUTHORIZATION_HEADER_PREFIX)) {
                
                String authToken = authHeader.get(0);
                authToken = authToken.replaceFirst(JWT_AUTHORIZATION_HEADER_PREFIX, "");
                
                try {
                    
                    Jws<Claims> jws = Jwts.parser().requireIssuer("REST_WS").setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
                    
                    // Token is trusted
                    Claims claims = jws.getBody();
                    String username = claims.getSubject();
                    System.out.println("Token expires in: " + claims.getExpiration().toString());
                    user = userService.getUserByUsername(username);
                    String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
                    requestContext.setSecurityContext(new MyCustomSecurityContext(user, scheme));
                    
                }
                catch (JwtException e) {
                    Response response = Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Unauthenticated\"}").build();
                    requestContext.abortWith(response);
                    return;
                }
                
            }

            
        }
       
        
        Method resMethod = resourceInfo.getResourceMethod();
        Class<?> resClass = resourceInfo.getResourceClass();
        
        if (resMethod.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        
        if (resMethod.isAnnotationPresent(DenyAll.class)) {
            
            Response response = Response.status(Response.Status.FORBIDDEN).entity("Access blocked for all users").build();
            requestContext.abortWith(response);
        }
        if (resMethod.isAnnotationPresent(RolesAllowed.class)) {
            
            if (rolesMatched(user, resMethod.getAnnotation(RolesAllowed.class))){
                return;
            }
            
            Response response = Response.status(Response.Status.FORBIDDEN).entity("{\"error\": \"User unauthorized\"}").build();
            requestContext.abortWith(response);
        }
        
        if (resClass.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        
        if (resClass.isAnnotationPresent(DenyAll.class)) {
            
            Response response = Response.status(Response.Status.FORBIDDEN).entity("Access blocked for all users").build();
            requestContext.abortWith(response);
        }
        if (resClass.isAnnotationPresent(RolesAllowed.class)) {
            
            if (rolesMatched(user, resMethod.getAnnotation(RolesAllowed.class))){
                return;
            }
            
            Response response = Response.status(Response.Status.FORBIDDEN).entity("User unauthorized").build();
            requestContext.abortWith(response);
        }

    }


    private boolean rolesMatched(User user, RolesAllowed annotation) {
        
        if (user == null) return false;
        
        for (String role : annotation.value()) {
            
            if (!user.getRoles().contains(role)) {
                return false;
            }
        }
        return true;
    }

}
