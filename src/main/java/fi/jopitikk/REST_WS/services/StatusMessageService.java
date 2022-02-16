package fi.jopitikk.REST_WS.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.jopitikk.REST_WS.model.User;
import fi.jopitikk.REST_WS.model.StatusMessage;

public class StatusMessageService {
    
    UserService userService = new UserService();

    public List<StatusMessage> getAllStatusMessages(long userId) {
        
        User user = userService.getUserById(userId);
        Map<Long, StatusMessage> statusMessages = user.getStatusMessages();
        List<StatusMessage> result = new ArrayList<>();
        
        statusMessages.forEach((id, statusMessage) -> {
            result.add(statusMessage);
        });
        return result;
        
    }

    public StatusMessage getStatusMessageById(long userId, long statusMessageId) {
        
        User user = userService.getUserById(userId);
        Map<Long, StatusMessage> statusMessages = user.getStatusMessages();
        return statusMessages.get(statusMessageId);
    }

    public StatusMessage addStatusMessage(long userId, StatusMessage statusMessage) {
        
        User user = userService.getUserById(userId);
        Map<Long, StatusMessage> statusMessages = user.getStatusMessages();
        long statusMessageId = user.getStatusMessageIdCounter();
        user.setStatusMessageIdCounter((int) ++statusMessageId);
        statusMessage.setId(user.getStatusMessageIdCounter());
        statusMessages.put((long) user.getStatusMessageIdCounter(), statusMessage);
        user.setStatusMessages(statusMessages);
        //user.setStatusMessageIdCounter((int) ++statusMessageId);
        return statusMessage;
    }

    public StatusMessage updateStatusMessage(long userId, long statusMessageId, StatusMessage statusMessage) {
        
        User user = userService.getUserById(userId);
        Map<Long, StatusMessage> statusMessages = user.getStatusMessages();
        statusMessages.put(statusMessageId, statusMessage);
        user.setStatusMessages(statusMessages);
        return statusMessage;
    }

    public void removeStatusMessage(long userId, long statusMessageId) {
        
        User user = userService.getUserById(userId);
        Map<Long, StatusMessage> statusMessages = user.getStatusMessages();
        statusMessages.remove(statusMessageId);
        user.setStatusMessages(statusMessages);
        
    }

}
