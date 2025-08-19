package aut.ap.repository;

import aut.ap.framework.ServiceBase;
import aut.ap.model.Person;
import java.util.List;

public class PersonRepository extends ServiceBase<Person> {

    public PersonRepository(Class<Person> personClass) {
        super(personClass);
    }

    public void persistPerson(Person person) {
        persist(person);
    }

    public Person fetchByEmail(String email) {
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
        return getSessionFactory().fromTransaction(session -> {
            return session.createQuery("from Person p where p.email in :emailsList", Person.class)
                    .setParameter("emailsList", emails)
                    .getResultList();
        });
    }
}
