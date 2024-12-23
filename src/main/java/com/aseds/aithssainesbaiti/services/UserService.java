package com.aseds.aithssainesbaiti.services;

import com.aseds.aithssainesbaiti.domain.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class UserService {
    public static Map<Integer, User> users=new HashMap<Integer,User>();
    public User getUser(int id) {
        return users.get(id);
    }
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
    public double getSold(int id) {
        return users.get(id).getSold();
    }
}
