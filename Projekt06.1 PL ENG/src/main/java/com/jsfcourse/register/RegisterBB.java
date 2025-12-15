package com.jsfcourse.register;

import com.jsfcourse.dao.UserDAO;
import com.jsfcourse.entities.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.util.Date;

@Named
@RequestScoped
public class RegisterBB {

    private String username;
    private String email;
    private String password;

    @EJB
    private UserDAO userDAO;

    public String register() {

        if (username == null || username.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Wypełnij wszystkie pola!", null));
            return null;
        }

        if (userDAO.findByUsername(username) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Taki login już istnieje", null));
            return null;
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);   // без hashowania
        u.setCreatedAt(new Date());

        userDAO.create(u);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Rejestracja udana. Możesz się zalogować."));

        return "/pages/login?faces-redirect=true";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

