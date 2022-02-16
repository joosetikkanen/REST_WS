package fi.jopitikk.REST_WS.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import fi.jopitikk.REST_WS.util.Link;

@XmlRootElement
public class Review {
    
    private long id;
    private String text;
    private int rating;
    private Date date;
    private Date lastEdited;
    private List<Link> links = new ArrayList<>();
    
    public Review() {}
    
    public void addLink(String url, String rel) {
        Link link = new Link();
        link.setLink(url);
        link.setRel(rel);
        links.add(link);   
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date published) {
        this.date = published;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date edited) {
        this.lastEdited = edited;
    }
    
    
    
}
