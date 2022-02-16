package fi.jopitikk.REST_WS.resources;

import java.time.Instant;
import java.util.Date;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import static fi.jopitikk.REST_WS.security.SecretService.*;

@Path("/authentication")
public class AuthenticationEndpoint {
    
    UserService userService = new UserService(); 

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(String user) {
        
        JSONObject json;
        //User newUser = new User();
        String username;
        String password;
        
        try {
            json = new JSONObject(user);
            username = json.get("username").toString(); 
            password = json.get("password").toString(); 
        }
        catch (JSONException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity(e.getMessage())
                                        .build();
            throw new BadRequestException(response);
        }   

            // Authenticate the user using the credentials provided
        if (!userService.userCredentialExists(username, password)) {
            
            
             Response response = Response.status(Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Unauthenticated\"}")
                        .build();
                
             throw new NotAuthorizedException(response);        
                
        }

            Date expiresIn = Date.from(Instant.ofEpochSecond(new Date().toInstant().getEpochSecond() + 31556926L)); //1 year from now
        
            // Issue a token for the user
            String token = issueToken(username, expiresIn);
            JSONObject responseBody = new JSONObject();
            responseBody.put("token_type", "JWT");
            responseBody.put("access_token", token);
            responseBody.put("expires_in", expiresIn);
            
            // Return the token on the response
            return Response.ok(responseBody.toString()).build();

           
    }


    private String issueToken(String username, Date expiresIn) {
        
        User user = userService.getUserByUsername(username);
        //String roles = String.join(",", user.getRoles());
        String jws = Jwts.builder()
                .setIssuer("REST_WS")
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiresIn)
                .claim("roles", user.getRoles())
                .signWith(SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode(SECRET_KEY)
                 )
                .compact();
        return jws;
        
    }
}
