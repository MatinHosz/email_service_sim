package aut.ap.model;

import aut.ap.framework.EServiceEntity;
import jakarta.persistence.Basic;

public class Person extends EServiceEntity {

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String email;

    @Basic(optional = false)
    private String password;

    public Person() {
    }

    public Person(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name= '" + name + '\'' +
                ", email= '" + email + '\'' +
                '}';
    }
}
