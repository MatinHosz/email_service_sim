package aut.ap.model;

import aut.ap.freamwork.EServiceEntity;
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
