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

import org.json.JSONException;
import org.json.JSONObject;

import fi.jopitikk.REST_WS.model.Book;
import fi.jopitikk.REST_WS.model.Review;
import fi.jopitikk.REST_WS.services.BookService;
import fi.jopitikk.REST_WS.services.ReviewService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {
    
    private BookService bookService = new BookService();
    private ReviewService reviewService = new ReviewService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Review> getReviews(@PathParam("bookId") long bookId){
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested book not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        return reviewService.getAllReviews(bookId);
    }
    
    @GET
    @Path("/{reviewId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Review getReview(@PathParam("bookId") long bookId, @PathParam("reviewId") long reviewId) {
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested book not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        Review review = reviewService.getReviewById(bookId, reviewId);
        
        if (review == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested review not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        return review;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response addReview(@PathParam("bookId") long bookId, String review, @Context UriInfo uriInfo) {
        
        JSONObject json;
        Review newReview = new Review();
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested book not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        try {
            json = new JSONObject(review);
            newReview.setText(json.get("text").toString()); 
            newReview.setRating(Integer.valueOf(json.get("rating").toString()));
            //Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("date").toString() + " UTC");
            newReview.setDate(new Date()); 
        }
        catch (JSONException | NumberFormatException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity("{\"error\": \"malformed request\"}")
                                        .build();
            throw new BadRequestException(response);
        }
        
        newReview = reviewService.addReview(bookId, newReview);
        
        String uri = uriInfo.getAbsolutePathBuilder()
                .path(newReview.getId()+"")
                .build()
                .toString();
        newReview.addLink(uri, "self");
        
        uri = uriInfo.getBaseUriBuilder()
                .path(BookResource.class)
                .path(bookId+"")
                .build()
                .toString();
        newReview.addLink(uri, "book");
        
        return Response.created(uriInfo.getAbsolutePathBuilder().path(newReview.getId()+"").build()).entity(newReview).build();
    }
    
    @PUT
    @Path("/{reviewId}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Review updateReview(@PathParam("bookId") long bookId, @PathParam("reviewId") long reviewId, String review) {
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested book not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        Review reviewToUpdate = reviewService.getReviewById(bookId, reviewId);
        if (reviewToUpdate == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested review not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        JSONObject json;
        try {
            json = new JSONObject(review);
            reviewToUpdate.setText(json.get("text").toString()); 
            reviewToUpdate.setRating(Integer.valueOf(json.get("rating").toString()));
            //Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("date").toString() + " UTC");
            reviewToUpdate.setLastEdited(new Date()); 
        }
        catch (JSONException | NumberFormatException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity("{\"error\": \"malformed request\"}")
                                        .build();
            throw new BadRequestException(response);
        }
        
        reviewToUpdate.setId(reviewId);
        return reviewService.updateReview(bookId, reviewId, reviewToUpdate);
    }
    
    @DELETE
    @RolesAllowed("admin")
    @Path("/{reviewId}")
    public void deleteReview (@PathParam("bookId") long bookId, @PathParam("reviewId") long reviewId) {
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested book not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        Review reviewToUpdate = reviewService.getReviewById(bookId, reviewId);
        if (reviewToUpdate == null) {
            
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"Requested review not found\"}")
                    .build();
            throw new NotFoundException(response);
        }
        
        reviewService.removeReview(bookId, reviewId);
    }
    
}
