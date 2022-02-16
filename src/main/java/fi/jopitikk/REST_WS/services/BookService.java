package fi.jopitikk.REST_WS.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import fi.jopitikk.REST_WS.model.Book;

public class BookService {
    
    private static Map<Long, Book> bookData = new HashMap<>();
    private static int idCounter = 0;
    
    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<Book>();         
        
        bookData.forEach((id, book) -> {
            books.add(book);
        });
        
        return books;
    }
    
    public Book getBookById(long id) {
        return bookData.get(id);
    }

    public Book addBook(Book book) {
        //Book bookToAdd = new Book();
        idCounter++;
        book.setId(idCounter);
        bookData.put(book.getId(), book);
        return book;
        
    }

    public Book updateBook(Book book) {
        bookData.put(book.getId(), book);
        return book;
        
    }

    public void removeBook(long id) {
        bookData.remove(id);
    }

    public List<Book> getBooksByAuthor(String author) {
        
        List<Book> books = new ArrayList<>();
        bookData.forEach((id, book)->{
            if (book.getMainAuthor().equals(author)) {
                books.add(book);
            }
        });
        return books;
        
    }

    public List<Book> getBooksByYear(int year) {
        
        List<Book> books = new ArrayList<>();
        bookData.forEach((id, book)->{
            Calendar c = Calendar.getInstance();
            c.setTime(book.getPublished());
            if (c.get(Calendar.YEAR) == year) {
                books.add(book);
            }
        });
        return books;
        
    }
    
    public List<Book> getBooksByTitle(String title) {
        
        List<Book> books = new ArrayList<>();
        bookData.forEach((id, book)->{
            if (book.getTitle().equals(title)) {
                books.add(book);
            }
        });
        return books;
        
    }
    
    public List<Book> merge(List<Book> booksByAuthor, List<Book> booksByYear,
            List<Book> booksByTitle) {
        
        List<Book> result = new ArrayList<>();
        result = booksByAuthor.stream().distinct().filter(booksByYear::contains).collect(Collectors.toList()); //merge list 1 and 2
        result = result.stream().distinct().filter(booksByTitle::contains).collect(Collectors.toList()); //merge previous result with list 3
        return result;
    }
    
    public List<Book> merge(List<Book> list1, List<Book> list2) {
        
        List<Book> result = new ArrayList<>();
        result = list1.stream().distinct().filter(list2::contains).collect(Collectors.toList());
        return result;
        
    }

}
