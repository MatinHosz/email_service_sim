package aut.ap.service;

import aut.ap.exception.InvalidEmailException;
import aut.ap.model.Person;
import aut.ap.repository.PersonRepository;

public class PersonService {
    public PersonRepository repository;

    public PersonService() {
        repository = new PersonRepository(Person.class);
    }

    public void register(String name, String email, String password) {
        if (repository.fetchByEmail(email) != null)
            throw new InvalidEmailException("An account already exists with email: " + email);
        if (password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        
        if (!email.contains("@"))
            email = email + "@milou.com";
        
        Person person = new Person(name, email, password);
        repository.persistPerson(person);
    }

    public Person login(String email, String password) {
        Person person = repository.fetchByEmail(email);
        if (person == null) {
            throw new InvalidEmailException("No account found with email: " + email);
        }
        if (!person.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
        return person;
    }
}
