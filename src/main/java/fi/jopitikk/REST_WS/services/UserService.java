package fi.jopitikk.REST_WS.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fi.jopitikk.REST_WS.model.User;

public class UserService {
    
    private static Map<Long, User> userData = new HashMap<>();
    private static int idCounter = 0;
    
    public List<User> getAllUsers(){
        List<User> users = new ArrayList<User>(); 
                
        userData.forEach((id, user) -> {
            users.add(user);
        });
        
        return users;
    }
    
    public User getUserById(long id) {
        return userData.get(id);
    }

    public User addUser(User user) {
        //User userToAdd = new User();
        idCounter++;
        user.setId(idCounter);
        userData.put(user.getId(), user);
        return user;
        
    }

    public User updateUser(User user) {
        userData.put(user.getId(), user);
        return user;
        
    }

    public void removeUser(long id) {
        userData.remove(id);
    }

    public boolean userCredentialExists(String username, String password) {
        
        User user = getUserByUsername(username);
        
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        
        for (User user : userData.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        
        return null;
    }


}