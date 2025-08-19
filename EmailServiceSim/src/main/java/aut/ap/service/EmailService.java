package aut.ap.service;

import java.util.List;
import java.util.regex.Pattern;

import aut.ap.model.Email;
import aut.ap.model.Person;
import aut.ap.repository.EmailRepository;

public class EmailService {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^((\\[(Re|Fw)\\]\\s)+)");
    public EmailRepository repository;

    public EmailService() {
        repository = new EmailRepository(Email.class);
    }

    public void reply(Email orginalEmail, Person replyingSender, String body) {
        List<Person> recipients = repository.getRecipients(orginalEmail.getId());
        recipients.remove(replyingSender);
        recipients.add(orginalEmail.getSender());

        String subject = "[Re] " + getCleanSubject(orginalEmail);
        Email repliedEmail = new Email(replyingSender, subject, body, orginalEmail, "reply");

        repository.persistEmail(repliedEmail);
        repository.send(recipients, repliedEmail.getId());
    }

    public void forward(List<Person> recipients, Email orginalEmail, Person sender) {

        String subject = "[Fw] " + EmailService.getCleanSubject(orginalEmail);
        Email forwardedEmail = new Email(sender, subject, orginalEmail.getBody(), orginalEmail, "forward");

        repository.persistEmail(forwardedEmail);
        repository.send(recipients, forwardedEmail.getId());
    }

    public static String getCleanSubject(Email email) {
        String subject = email.getSubject();

        // Find the pattern at the start of the subject and replace it with ""
        return PREFIX_PATTERN.matcher(subject).replaceFirst("");
    }
}
