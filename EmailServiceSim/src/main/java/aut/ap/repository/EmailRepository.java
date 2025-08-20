package aut.ap.repository;

import aut.ap.framework.ServiceBase;
import aut.ap.model.Email;
import aut.ap.model.Person;
import aut.ap.repository.EmailRepository;
import aut.ap.exception.exceptions.InvalidEmailException;
import aut.ap.exception.exceptions.EmailNotFoundException;
import aut.ap.exception.InvalidEntityException;

import java.util.List;

public class EmailRepository extends ServiceBase<Email> {

    public EmailRepository(Class<Email> emailClass) {
        super(emailClass);
    }

    public void persistEmail(Email email) {
        if (email == null) {
            throw new InvalidEmailException("Email cannot be null.");
        }
        persist(email);
    }

    public void send(List<Person> recipients, Integer EmailId) {
        if (recipients == null || recipients.isEmpty()) {
            throw new InvalidEntityException("Recipients list cannot be null or empty.");
        }
        if (EmailId == null) {
            throw new InvalidEmailException("Email ID cannot be null.");
        }
        getSessionFactory().inTransaction(session -> {
            for (Person recipient : recipients) {
                if (recipient == null) {
                    throw new InvalidEntityException("Recipient cannot be null.");
                }
                session.createNativeMutationQuery(
                        "insert into email_recipients(email_id, recipient_id) values(:email_id, :recipient_id)")
                        .setParameter("email_id", EmailId)
                        .setParameter("recipient_id", recipient.getId())
                        .executeUpdate();
            }
        });
    }

    public Email fetchByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidEmailException("Email code cannot be null or empty.");
        }
        return getSessionFactory().fromTransaction(session -> {
            Email email = session.createSelectionQuery(
                    "from Email where code = :code",
                    Email.class)
                    .setParameter("code", code)
                    .getSingleResult();
            if (email == null) {
                throw new EmailNotFoundException("No email found with the given code.");
            }
            return email;
        });
    }

    public List<Email> fetchAllReceived(Integer recipientId) {
        if (recipientId == null) {
            throw new InvalidEntityException("Recipient ID cannot be null.");
        }
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select e.* from emails e join email_recipients r on e.id = r.email_id where r.recipient_id = :recipient_id",
                    Email.class)
                    .setParameter("recipient_id", recipientId)
                    .getResultList();
        });
    }

    public List<Email> fetchAllUnread(Integer recipientId) {
        if (recipientId == null) {
            throw new InvalidEntityException("Recipient ID cannot be null.");
        }
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select e.* from emails e join email_recipients r on e.id = r.email_id where r.recipient_id = :recipient_id and r.read_at is NULL",
                    Email.class)
                    .setParameter("recipient_id", recipientId)
                    .getResultList();
        });
    }

    public List<Email> fetchAllSent(Integer senderId) {
        if (senderId == null) {
            throw new InvalidEntityException("Sender ID cannot be null.");
        }
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select * from emails e where e.sender_id = :sender_id",
                    Email.class)
                    .setParameter("sender_id", senderId)
                    .getResultList();
        });
    }

    public void makeRead(String code, Integer recipientId) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidEmailException("Email code cannot be null or empty.");
        }
        if (recipientId == null) {
            throw new InvalidEntityException("Recipient ID cannot be null.");
        }
        getSessionFactory().inTransaction(session -> {
            session.createNativeMutationQuery(
                    "update email_recipients set read_at = CURRENT_DATE where email_id = (select id from emails where code = :code) and recipient_id = :recipient_id")
                    .setParameter("code", code)
                    .setParameter("recipient_id", recipientId)
                    .executeUpdate();
        });
    }

    public List<Person> getRecipients(Integer emailId) {
        if (emailId == null) {
            throw new InvalidEmailException("Email ID cannot be null.");
        }
        return getSessionFactory().fromTransaction(session -> {
            return session.createNativeQuery(
                    "select p.* from email_recipients r join persons p on r.recipient_id = p.id where r.email_id = :emailId ",
                    Person.class)
                    .setParameter("emailId", emailId)
                    .getResultList();
        });
    }
}
