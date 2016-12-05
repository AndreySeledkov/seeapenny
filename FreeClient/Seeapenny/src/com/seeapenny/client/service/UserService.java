package com.seeapenny.client.service;

import android.content.Context;
import com.seeapenny.client.dao.UserDAO;
import com.seeapenny.client.server.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 14.05.13
 * Time: 0:23
 * To change this template use File | Settings | File Templates.
 */
public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public void save(User user) {
        userDAO.insert(user);
    }

    public User getUser(long userID) {
        return userDAO.getUser(userID);
    }

    public List<User> getShareUsers(long listId) {
        return userDAO.getShareUsers(listId);
    }

    public boolean removeAll(){
        return userDAO.removeAll();
    }
}
