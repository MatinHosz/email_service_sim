package aut.ap.service;

import aut.ap.exception.exceptions.InvalidEmailException;

import aut.ap.model.Person;
import aut.ap.repository.PersonRepository;
import aut.ap.exception.exceptions.InvalidPersonException;

public class PersonService {
    public PersonRepository repository;

    public PersonService() {
        repository = new PersonRepository(Person.class);
    }

    public void register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPersonException("Name cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty.");
        }
        if (password == null || password.length() < 8) {
            throw new InvalidPersonException("Password must be at least 8 characters long and not null.");
        }
        if (repository.fetchByEmail(email) != null) {
            throw new InvalidEmailException("An account already exists with email: " + email);
        }
        if (!email.contains("@")) {
            email = email + "@milou.com";
        }
        Person person = new Person(name, email, password);
        repository.persistPerson(person);
    }

    public Person login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPersonException("Password cannot be null or empty.");
        }
        Person person = repository.fetchByEmail(email);
        if (person == null) {
            throw new InvalidEmailException("No account found with email: " + email);
        }
        if (!person.getPassword().equals(password)) {
            throw new InvalidPersonException("Invalid password.");
        }
        return person;
    }
}
