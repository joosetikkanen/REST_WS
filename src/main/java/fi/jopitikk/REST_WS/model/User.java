package fi.jopitikk.REST_WS.model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.jopitikk.REST_WS.util.Link;

@XmlRootElement
public class User implements Principal {
    
    private long id;
    private String username, password, firstName, lastName, email;
    //private String password;
    private Map<Long, StatusMessage> statusMessages = new HashMap<>();
    private List<Link> links = new ArrayList<>();
    private List<String> roles = new ArrayList<String>();
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private int statusMessageIdCounter;
    
    public User() {}
    
    public User(String username, String password, String firstName,
            String lastName, String email) {
        
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.roles = new ArrayList<String>();
    }


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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<Long, StatusMessage> getStatusMessages() {
        return statusMessages;
    }

    public void setStatusMessages(Map<Long, StatusMessage> messages) {
        this.statusMessages = messages;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public int getStatusMessageIdCounter() {
        return statusMessageIdCounter;
    }

    public void setStatusMessageIdCounter(int statusMessageIdCounter) {
        this.statusMessageIdCounter = statusMessageIdCounter;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


    @Override
    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    
    

}
