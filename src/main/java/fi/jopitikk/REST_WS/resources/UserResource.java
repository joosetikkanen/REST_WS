package fi.jopitikk.REST_WS.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONException;
import org.json.JSONObject;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.services.UserService;

@Path("/users")
public class UserResource {
    
    
    UserService userService = new UserService();
    
    @Context
    private SecurityContext securityContext;
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public List<User> getUsers(){
        
        return userService.getAllUsers(); 
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(String user, @Context UriInfo uriInfo) {
        
        
        JSONObject json;
        User newUser = new User();
        
        try {
            json = new JSONObject(user);
            newUser.setUsername(json.get("username").toString()); 
            newUser.setPassword(json.get("password").toString()); 
            newUser.setFirstName(json.get("firstName").toString()); 
            newUser.setLastName(json.get("lastName").toString());
            newUser.setEmail(json.get("email").toString());
        }
        catch (JSONException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity(e.getMessage())
                                        .build();
            throw new BadRequestException(response);
        }
        
        User userCheck = userService.getUserByUsername(newUser.getUsername());
        if (userCheck != null) {
            Response response = Response.status(Status.METHOD_NOT_ALLOWED)
                    .entity("Username already taken")
                    .build();
            throw new NotAllowedException(response);
        }
        
        if (newUser.getUsername().equals("admin") && newUser.getPassword().equals("admin")) {
            newUser.addRole("admin");
        }
        
        newUser.addRole("user");
        
        newUser = userService.addUser(newUser);
        
        String uri = uriInfo.getAbsolutePathBuilder()
                .path(newUser.getId()+"")
                .build()
                .toString();
        newUser.addLink(uri, "self");
        
        uri = uriInfo.getAbsolutePathBuilder()
                .path(UserResource.class, "getStatusMessageResource")
                .resolveTemplate("userId", newUser.getId())
                .build()
                .toString();
        newUser.addLink(uri, "status_messages");
        
        return Response.created(uriInfo.getAbsolutePathBuilder().path(newUser.getId()+"").build()).entity(newUser).build();
        
    }
    
    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public User updateUser(@PathParam("userId") long id, String user, @Context UriInfo uriInfo) {
        
        User requestingUser = (User) securityContext.getUserPrincipal();
        
        if (id != requestingUser.getId()) {
            
            Response response = Response.status(Status.FORBIDDEN)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
            throw new ForbiddenException(response);
        }
        
        User userToUpdate = userService.getUserById(id);
        if (userToUpdate == null) {
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
            throw new NotFoundException(response);
        }

        JSONObject json;
        try {
            json = new JSONObject(user);
        }
        catch (JSONException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity(e.getMessage())
                                        .build();
            throw new BadRequestException(response);
        }
        
        
        
        try {
            String username = json.get("username").toString();
            
            User userCheck = userService.getUserByUsername(username);
            if (userCheck != null) {
                Response response = Response.status(Status.METHOD_NOT_ALLOWED)
                        .entity("Username already taken")
                        .build();
                throw new NotAllowedException(response);
            }
            
            userToUpdate.setUsername(username);
            
        }
        catch (JSONException e) {
            //
        }
        
        try {
            userToUpdate.setPassword(json.get("password").toString()); 
        }
        catch (JSONException e) {
            //
        }
        
        userToUpdate.setId(id);
        
        return userService.updateUser(userToUpdate);
    }
    
    @DELETE
    @Path("/{userId}")
    @RolesAllowed("user")
    public void deleteUser (@PathParam("userId") long id) {
        
        User user = (User) securityContext.getUserPrincipal();
        
        if (id != user.getId()) {
            
            Response response = Response.status(Status.FORBIDDEN)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
            throw new ForbiddenException(response);
        }
        
        user = userService.getUserById(id);
        
        if (user == null) {
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
            throw new NotFoundException(response);
        }
        
        userService.removeUser(id);
    }
    
    @Path("{userId}/status_messages")
    public StatusMessageResource getStatusMessageResource() {
        return new StatusMessageResource();
    }

}

