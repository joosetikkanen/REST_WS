package fi.jopitikk.REST_WS.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.jopitikk.REST_WS.util.Link;

@XmlRootElement
public class Book {

    private long id;
    private String title;
    private Date published;
    private String mainAuthor;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    @XmlTransient
    private Map<Long, Review> reviews = new HashMap<>();
    
    @JsonProperty(access = Access.WRITE_ONLY)
    @XmlTransient
    private int reviewIdCounter;
    private List<Link> links = new ArrayList<>();
    
    
    public Book() {}
    
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getMainAuthor() {
        return mainAuthor;
    }

    public void setMainAuthor(String mainAuthor) {
        this.mainAuthor = mainAuthor;
    }

    @JsonIgnore
    @XmlTransient
    public Map<Long, Review> getReviews() {
        return reviews;
    }

    public void setReviews(Map<Long, Review> reviews) {
        this.reviews = reviews;
    }

    @JsonIgnore
    @XmlTransient
    public int getReviewIdCounter() {
        return reviewIdCounter;
    }

    public void setReviewIdCounter(int reviewIdCounter) {
        this.reviewIdCounter = reviewIdCounter;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    
    
    
    
}
