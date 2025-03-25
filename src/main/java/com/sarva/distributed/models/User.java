package com.sarva.distributed.models;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String name; 
    private String password;
    private String email;
    private String sportsPreferences; // Stores multiple sports as "Football,Basketball"
    public User() {}
    public User(String name, String password, String email, String sportsPreferences) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.sportsPreferences = sportsPreferences;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSportsPreferences() {
        return sportsPreferences;
    }

    public void setSportsPreferences(String sportsPreferences) {
        this.sportsPreferences = sportsPreferences;
    }
}
