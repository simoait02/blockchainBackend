package com.aseds.aithssainesbaiti.services;

import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.domain.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class UserService {
    public static Map<Integer, User> users=new HashMap<Integer,User>();
    public static Map<Integer, List<Transaction>> history=new HashMap<>();
    public User getUser(int id) {
        return users.get(id);
    }
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
    public static double getSold(int id) {
        return users.get(id).getSold();
    }
    public static void updateSolde(int id, double sold) {
        users.get(id).setSold(sold);
    }
}
