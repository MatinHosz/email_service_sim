package aut.ap.repository;


import aut.ap.framework.ServiceBase;
import aut.ap.model.Person;
import java.util.List;
import aut.ap.exception.exceptions.InvalidPersonException;
import aut.ap.exception.exceptions.InvalidEmailException;

public class PersonRepository extends ServiceBase<Person> {

    public PersonRepository(Class<Person> personClass) {
        super(personClass);
    }

    public void persistPerson(Person person) {
        if (person == null) {
            throw new InvalidPersonException("Person cannot be null.");
        }
        persist(person);
    }

    public Person fetchByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty.");
        }
        return getSessionFactory().fromTransaction(session -> {
            List<Person> result = session
                    .createNativeQuery("select * from persons p where p.email like :email1",
                            Person.class)
                    .setParameter("email1", email)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        });
    }

    public List<Person> fetchByEmail(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            throw new InvalidEmailException("Email list cannot be null or empty.");
        }
        
        return getSessionFactory().fromTransaction(session -> {
            return session.createQuery("from Person p where p.email in :emailsList", Person.class)
                    .setParameter("emailsList", emails)
                    .getResultList();
        });
    }
}
