package com.jsfcourse.dao;

import com.jsfcourse.entities.User;
import com.jsfcourse.entities.Role;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class UserDAO {

    private final static String UNIT_NAME = "jsfcourse-simplePU";

    @PersistenceContext(unitName = UNIT_NAME)
    protected EntityManager em;

    public void create(User user) {
        em.persist(user);
    }

    public User merge(User user) {
        return em.merge(user);
    }

    public User find(Integer id) {
        return em.find(User.class, id);
    }

    public User findByUsername(String username) {
        TypedQuery<User> query =
                em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean exists(String username) {
        return findByUsername(username) != null;
    }

    public List<Role> getRoles(User user) {
        User u = em.find(User.class, user.getId());
        return u != null ? u.getRoles() : new ArrayList<>();
    }

    // Методы для обратной совместимости с существующим кодом
    public User getUserFromDatabase(String login, String pass) {
        User user = findByUsername(login);
        if (user != null && user.getPassword().equals(pass)) {
            return user;
        }
        return null;
    }

    public List<String> getUserRolesFromDatabase(User user) {
        List<String> roleNames = new ArrayList<>();
        if (user != null && user.getId() != null) {
            List<Role> roles = getRoles(user);
            for (Role role : roles) {
                roleNames.add(role.getRoleName());
            }
        }
        return roleNames;
    }
}

