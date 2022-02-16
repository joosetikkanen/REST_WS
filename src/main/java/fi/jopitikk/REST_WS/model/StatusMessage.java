package fi.jopitikk.REST_WS.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import fi.jopitikk.REST_WS.util.Link;

@XmlRootElement
public class StatusMessage {
    
    private long id;
    private String message;
    private Date posted;
    private Date lastEdited;
    private List<Link> links = new ArrayList<>();
    
    public StatusMessage() {}
    
    public void addLink(String url, String rel) {
        Link link = new Link();
        link.setLink(url);
        link.setRel(rel);
        links .add(link);   
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
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
