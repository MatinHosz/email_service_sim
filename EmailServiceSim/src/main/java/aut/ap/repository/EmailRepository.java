package aut.ap.repository;

import aut.ap.framework.ServiceBase;
import aut.ap.model.Email;
import aut.ap.model.Person;
import aut.ap.repository.EmailRepository;

import java.util.List;

public class EmailRepository extends ServiceBase<Email> {

    public EmailRepository(Class<Email> emailClass) {
        super(emailClass);
    }

    public void persistEmail(Email email) {
        persist(email);
    }

    public void send(List<Person> recipients, Integer EmailId) {
        getSessionFactory().inTransaction(session -> {
            for (int i = 0; i < recipients.size(); i++)
                session.createNativeMutationQuery(
                        "insert into email_recipients(email_id, recipient_id) values(:email_id, :recipient_id)")
                        .setParameter("email_id", EmailId)
                        .setParameter("recipient_id", recipients.get(i).getId())
                        .executeUpdate();
        });
    }

    public Email fetchByCode(String code) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createSelectionQuery(
                    "from Email where code = :code",
                    Email.class)
                    .setParameter("code", code)
                    .getSingleResult();
        });
    }

    public List<Email> fetchAllReceived(Integer recipientId) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select e.* from emails e join email_recipients r on e.id = r.email_id where r.recipient_id = :recipient_id",
                    Email.class)
                    .setParameter("recipient_id", recipientId)
                    .getResultList();
        });
    }

    public List<Email> fetchAllUnread(Integer recipientId) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select e.* from emails e join email_recipients r on e.id = r.email_id where r.recipient_id = :recipient_id and r.read_at is NULL",
                    Email.class)
                    .setParameter("recipient_id", recipientId)
                    .getResultList();
        });
    }

    public List<Email> fetchAllSent(Integer senderId) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select * from emails e where e.sender_id = :sender_id",
                    Email.class)
                    .setParameter("sender_id", senderId)
                    .getResultList();
        });
    }

    public void makeRead(String code, Integer recipientId) {
        getSessionFactory().inTransaction(session -> {
            session.createNativeMutationQuery(
                    "update email_recipients set read_at = CURRENT_DATE where email_id = (select id from emails where code = :code) and recipient_id = :recipient_id")
                    .setParameter("code", code)
                    .setParameter("recipient_id", recipientId)
                    .executeUpdate();
        });
    }

    public List<Person> getRecipients(Integer emailId) {
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select p.* from email_recipients r join persons p on r.recipient_id = p.id where r.email_id = :emailId ",
                    Person.class)
                    .setParameter("emailId", emailId)
                    .getResultList();
        });
    }
}
