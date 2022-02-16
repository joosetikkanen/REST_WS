package fi.jopitikk.REST_WS.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONException;
import org.json.JSONObject;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.model.StatusMessage;
import fi.jopitikk.REST_WS.services.UserService;
import fi.jopitikk.REST_WS.services.StatusMessageService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatusMessageResource {
    
    private UserService userService = new UserService();
    private StatusMessageService statusMessageService = new StatusMessageService();
    
    //@Context
    //private SecurityContext securityContext; 

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<StatusMessage> getStatusMessages(@PathParam("userId") long userId){
        
        User user = userService.getUserById(userId);
        if (user == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested user not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        return statusMessageService.getAllStatusMessages(userId);
    }
    
    @GET
    @Path("/{statusMessageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusMessage getStatusMessage(@PathParam("userId") long userId, @PathParam("statusMessageId") long statusMessageId) {
        
        User user = userService.getUserById(userId);
        if (user == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested user not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        StatusMessage statusMessage = statusMessageService.getStatusMessageById(userId, statusMessageId);
        
        if (statusMessage == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested status message not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        return statusMessage;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response addStatusMessage(@PathParam("userId") long userId, String statusMessage, @Context UriInfo uriInfo, @Context ContainerRequestContext context) {
        
        User user = (User) context.getSecurityContext().getUserPrincipal();
        
        //User user = (User) securityContext.getUserPrincipal();
        
        if (userId != user.getId()) {
            
            Response response = Response.status(Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
            throw new NotAuthorizedException(response);
        }
        
        JSONObject json;
        StatusMessage newStatusMessage = new StatusMessage();
        
        user = userService.getUserById(userId);
        if (user == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested user not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        try {
            json = new JSONObject(statusMessage);
            newStatusMessage.setMessage(json.get("message").toString()); 
            //Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("posted").toString() + " UTC");
            newStatusMessage.setPosted(new Date()); 
        }
        catch (JSONException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity("{\"error\": \"malformed request\"}")
                                        .build();
            throw new BadRequestException(response);
        }
        
        newStatusMessage = statusMessageService.addStatusMessage(userId, newStatusMessage);
        
        String uri = uriInfo.getAbsolutePathBuilder()
                .path(newStatusMessage.getId()+"")
                .build()
                .toString();
        newStatusMessage.addLink(uri, "self");
        
        uri = uriInfo.getBaseUriBuilder()
                .path(UserResource.class)
                .path(userId+"")
                .build()
                .toString();
        newStatusMessage.addLink(uri, "user");
        
        return Response.created(uriInfo.getAbsolutePathBuilder().path(newStatusMessage.getId()+"").build()).entity(newStatusMessage).build();
    }
    
    @PUT
    @Path("/{statusMessageId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public StatusMessage updateStatusMessage(@PathParam("userId") long userId, @PathParam("statusMessageId") long statusMessageId, String statusMessage, @Context ContainerRequestContext context) {
        
        //User user = (User) securityContext.getUserPrincipal();
        User user = (User) context.getSecurityContext().getUserPrincipal();
        
        if (userId != user.getId()) {
            
            Response response = Response.status(Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
            throw new NotAuthorizedException(response);
        }
        
        user = userService.getUserById(userId);
        if (user == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested user not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        StatusMessage statusMessageToUpdate = statusMessageService.getStatusMessageById(userId, statusMessageId);
        if (statusMessageToUpdate == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested status message not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        JSONObject json;
        try {
            json = new JSONObject(statusMessage);
            statusMessageToUpdate.setMessage(json.get("message").toString()); 
            //Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("date").toString() + " UTC");
            statusMessageToUpdate.setLastEdited(new Date()); 
        }
        catch (JSONException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity("{\"error\": \"malformed request\"}")
                                        .build();
            throw new BadRequestException(response);
        }
        
        statusMessageToUpdate.setId(statusMessageId);
        return statusMessageService.updateStatusMessage(userId, statusMessageId, statusMessageToUpdate);
    }
    
    @DELETE
    @Path("/{statusMessageId}")
    @RolesAllowed("user")
    public void deleteStatusMessage (@PathParam("userId") long userId, @PathParam("statusMessageId") long statusMessageId, @Context ContainerRequestContext context) {
        
        //User user = (User) securityContext.getUserPrincipal();
        User user = (User) context.getSecurityContext().getUserPrincipal();
        
        if (userId != user.getId()) {
            
            Response response = Response.status(Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
            throw new NotAuthorizedException(response);
        }
        
        user = userService.getUserById(userId);
        if (user == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested user not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        StatusMessage statusMessageToUpdate = statusMessageService.getStatusMessageById(userId, statusMessageId);
        if (statusMessageToUpdate == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested status message not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        statusMessageService.removeStatusMessage(userId, statusMessageId);
    }
    
}
