package fi.jopitikk.REST_WS.resources;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.UriInfo;

import fi.jopitikk.REST_WS.model.Book;
import fi.jopitikk.REST_WS.services.BookService;

@Path("/books")
@PermitAll
public class BookResource {
    
    BookService bookService = new BookService();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getBooks(@QueryParam("author") String author,
                               @QueryParam("year") int year,
                               @QueryParam("title") String title){
        
        if (author != null && year > 0 && title != null) {
            return bookService.merge(bookService.getBooksByAuthor(author), bookService.getBooksByYear(year), bookService.getBooksByTitle(title));
        }
        if (author != null && year > 0) {
            return bookService.merge(bookService.getBooksByAuthor(author), bookService.getBooksByYear(year));
        }
        if (author != null && title != null) {
            return bookService.merge(bookService.getBooksByAuthor(author), bookService.getBooksByTitle(title));
        }
        if (year > 0 && title != null) {
            return bookService.merge(bookService.getBooksByYear(year), bookService.getBooksByTitle(title));
        }
        if (author != null) {
            return bookService.getBooksByAuthor(author);
        }
        if (year > 0) {
            return bookService.getBooksByYear(year);
        }
        if (title != null) {
            return bookService.getBooksByTitle(title);
        }
        
        return bookService.getAllBooks(); 
    }
    
    @POST
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBook(String book, @Context UriInfo uriInfo) {
        
        
        JSONObject json;
        Book newBook = new Book();
        
        try {
            json = new JSONObject(book);
            newBook.setTitle(json.get("title").toString()); 
            newBook.setMainAuthor(json.get("mainAuthor").toString());
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("published").toString() + " UTC");
            newBook.setPublished(date); 
        }
        catch (JSONException | ParseException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity(e.getMessage())
                                        .build();
            throw new BadRequestException(response);
        }
           
        newBook = bookService.addBook(newBook);
        
        String uri = uriInfo.getAbsolutePathBuilder()
                .path(newBook.getId()+"")
                .build()
                .toString();
        newBook.addLink(uri, "self");
        
        uri = uriInfo.getAbsolutePathBuilder()
                .path(BookResource.class, "getReviewResource")
                .resolveTemplate("bookId", newBook.getId())
                .build()
                .toString();
        newBook.addLink(uri, "reviews");
        
        return Response.created(uriInfo.getAbsolutePathBuilder().path(newBook.getId()+"").build()).entity(newBook).build();
        
    }
    
    @PUT
    @Path("/{bookId}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Book updateBook(@PathParam("bookId") long id, String book) {
        
        JSONObject json;
        Book bookToUpdate = bookService.getBookById(id);
        if (bookToUpdate == null) {
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("Resource not found")
                    .build();
            throw new NotFoundException(response);
        }
        
        try {
            json = new JSONObject(book);
            bookToUpdate.setTitle(json.get("title").toString()); 
            bookToUpdate.setMainAuthor(json.get("mainAuthor").toString());
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z").parse(json.get("published").toString() + " UTC");
            bookToUpdate.setPublished(date); 
        }
        catch (JSONException | ParseException e) {
            Response response = Response.status(Status.BAD_REQUEST)
                                        .entity(e.getMessage())
                                        .build();
            throw new BadRequestException(response);
        }
        
        bookToUpdate.setId(id);
        return bookService.updateBook(bookToUpdate);
    }
    
    @DELETE
    @Path("/{bookId}")
    @RolesAllowed("admin")
    public void deleteBook (@PathParam("bookId") long id) {
        
        Book book = bookService.getBookById(id);
        
        if (book == null) {
            Response response = Response.status(Status.NOT_FOUND)
                    .entity("Resource not found")
                    .build();
            throw new NotFoundException(response);
        }
        
        bookService.removeBook(id);
    }
    
    @Path("{bookId}/reviews")
    public ReviewResource getReviewResource() {
        return new ReviewResource();
    }

}
