package aut.ap.service;

import aut.ap.framework.ServiceBase;
import aut.ap.model.Person;

public class PersonService extends ServiceBase<Person> {

    public PersonService() {
        super(Person.class);
    }

    public Person persist(String name, String email, String password) {
        Person person = new Person(name, email, password);
        persist(person);

        return person;
    }

    public Person fetchByEmail(String email) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createSelectionQuery("from Person where email like :email1 or email like :email2", Person.class)
                    .setParameter("emailParametr1", email)
                    .setParameter("emailParametr2", email + "@milou.com")
                    .getSingleResult();
                    
                    // TODO: getSingleResult will throw exception -> handle it and others
        });
    }
}
