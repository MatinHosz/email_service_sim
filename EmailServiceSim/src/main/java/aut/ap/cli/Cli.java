package aut.ap.cli;

import aut.ap.exception.InvalidEmailException;
import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.Person;
import aut.ap.service.EmailService;
import aut.ap.service.PersonService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private Person currentUser;
    private final PersonService personService;
    private final EmailService emailService;
    private final Scanner scanner;

    public Cli() {
        this.personService = new PersonService();
        this.emailService = new EmailService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("[L]ogin, [S]ign up, [E]xit:");
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("L") || command.equalsIgnoreCase("Login"))
                handleLogin();
            else if (command.equalsIgnoreCase("S") || command.equalsIgnoreCase("Sign up"))
                handleSignUp();
            else if (command.equalsIgnoreCase("E") || command.equalsIgnoreCase("Exit")) {
                System.out.println("Goodbye!");
                SingletonSessionFactory.shutdown();
                return;
            } else
                System.out.println("Invalid command.");
        }
    }

    private void handleSignUp() {
        System.out.println("Name:");
        String name = scanner.nextLine().trim();
        System.out.println("Email:");
        String email;
        try {
            email = normalizeEmail(scanner.nextLine().trim());
        } catch (InvalidEmailException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Password:");
        String password = scanner.nextLine().trim();

        try {
            personService.register(name, email, password);
            System.out.println("Your new account is created.\nGo ahead and login!");
        } catch (IllegalArgumentException | InvalidEmailException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.println("Email:");
        String email;
        try {
            email = normalizeEmail(scanner.nextLine().trim());
        } catch (InvalidEmailException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Password:");
        String password = scanner.nextLine().trim();

        try {
            currentUser = personService.login(email, password);
            System.out.println("Welcome back, " + currentUser.getName() + "!");
            showUnreadEmails();
            loggedInMenu();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showUnreadEmails() {
        List<Email> unreadEmails = emailService.repository.fetchAllUnread(currentUser.getId());
        if (unreadEmails.isEmpty()) {
            System.out.println("You have no unread emails.");
            return;
        }
        System.out.println("Unread Emails:");
        System.out.println(unreadEmails.size() + " unread emails:");
        for (Email email : unreadEmails) {
            System.out.println(
                    "+ " + email.getSender().getEmail() + "\t" + email.getSubject() + " (" + email.getCode() + ")");
        }
    }

    private void loggedInMenu() {
        while (currentUser != null) {
            System.out.println("[S]end, [V]iew, [R]eply, [F]orward, [L]ogout:");
            String command = scanner.nextLine().trim();
            switch (command.toUpperCase()) {
                case "S":
                    handleSendEmail();
                    break;
                case "V":
                    handleViewEmails();
                    break;
                case "R":
                    handleReplyToEmail();
                    break;
                case "F":
                    handleForwardEmail();
                    break;
                case "L":
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private void handleSendEmail() {
        try {
            List<Person> recipients = getRecipientsFromInput();
            if (recipients.isEmpty()) {
                System.out.println("No valid recipients found.");
                return;
            }

            System.out.println("Subject:");
            String subject = scanner.nextLine();
            System.out.println("Body:");
            String body = scanner.nextLine();

            Email newEmail = new Email(currentUser, subject, body, null, "NORMAL");
            emailService.repository.persistEmail(newEmail);
            emailService.repository.send(recipients, newEmail.getId());

            System.out.println("Successfully sent your email.");
            System.out.println("Code: " + newEmail.getCode());
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

    private void handleViewEmails() {
        System.out.println("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode:");
        String command = scanner.nextLine().trim();
        switch (command.toUpperCase()) {
            case "A":
                viewAllEmails();
                break;
            case "U":
                viewUnreadEmails();
                break;
            case "S":
                viewSentEmails();
                break;
            case "C":
                viewEmailByCode();
                break;
            default:
                System.out.println("Invalid command.");
        }
    }

    private void viewAllEmails() {
        try {
            List<Email> receivedEmails = emailService.repository.fetchAllReceived(currentUser.getId());
            Collections.reverse(receivedEmails);
            System.out.println("All Emails:");
            for (Email email : receivedEmails) {
                List<Person> recipients = emailService.repository.getRecipients(email.getId());
                String recipientStr = "-";
                if (recipients != null && !recipients.isEmpty()) {
                    List<String> recipientEmails = new ArrayList<>();
                    for (Person recipient : recipients) {
                        recipientEmails.add(recipient.getEmail());
                    }
                    recipientStr = String.join(", ", recipientEmails);
                }
                System.out.println(
                    "+ " + email.getSender().getEmail() + "\t" + recipientStr + "\t" + email.getSubject() + " (" + email.getCode() + ")"
                );
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch emails: " + e.getMessage());
        }
    }

    private void viewUnreadEmails() {
        try {
            List<Email> unreadEmails = emailService.repository.fetchAllUnread(currentUser.getId());
            Collections.reverse(unreadEmails);
            System.out.println("Unread Emails:");
            for (Email email : unreadEmails) {
                List<Person> recipients = emailService.repository.getRecipients(email.getId());
                String recipientStr = "-";
                if (recipients != null && !recipients.isEmpty()) {
                    List<String> recipientEmails = new ArrayList<>();
                    for (Person recipient : recipients) {
                        recipientEmails.add(recipient.getEmail());
                    }
                    recipientStr = String.join(", ", recipientEmails);
                }
                System.out.println(
                    "+ " + email.getSender().getEmail() + "\t" + recipientStr + "\t" + email.getSubject() + " (" + email.getCode() + ")"
                );
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch unread emails: " + e.getMessage());
        }
    }

    private void viewSentEmails() {
        try {
            List<Email> sentEmails = emailService.repository.fetchAllSent(currentUser.getId());
            Collections.reverse(sentEmails); // Show newest first
            System.out.println("Sent Emails:");
            for (Email email : sentEmails) {
                List<Person> recipients = emailService.repository.getRecipients(email.getId());
                String recipientStr = "-";
                if (recipients != null && !recipients.isEmpty()) {
                    List<String> recipientEmails = new ArrayList<>();
                    for (Person recipient : recipients) {
                        recipientEmails.add(recipient.getEmail());
                    }
                    recipientStr = String.join(", ", recipientEmails);
                }
                System.out.println(
                    "+ " + recipientStr + "\t" + email.getSubject() + " (" + email.getCode() + ")"
                );
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch sent emails: " + e.getMessage());
        }
    }

    private void viewEmailByCode() {
        System.out.println("Code:");
        String code = scanner.nextLine().trim();
        try {
            Email email = emailService.repository.fetchByCode(code);
            if (email == null) {
                System.out.println("Email with code " + code + " not found.");
                return;
            }
            List<Person> recipients = emailService.repository.getRecipients(email.getId());
            boolean isRecipient = false;
            for (Person p : recipients) {
                if (p.getId().equals(currentUser.getId())) {
                    isRecipient = true;
                    break;
                }
            }

            if (email.getSender().getId().equals(currentUser.getId()) || isRecipient) {
                List<String> recipientEmails = new ArrayList<>();
                for (Person recipient : recipients) {
                    recipientEmails.add(recipient.getEmail());
                }
                printEmailDetails(email, recipientEmails);
                // Mark as read if the current user is a recipient
                if (isRecipient) {
                    emailService.repository.makeRead(code, currentUser.getId());
                }
            } else
                System.out.println("You cannot read this email.");
        } catch (Exception e) {
            System.out.println("Failed to view email: " + e.getMessage());
        }
    }

    private void handleReplyToEmail() {
        System.out.println("Code:");
        String code = scanner.nextLine().trim();
        System.out.println("Body:");
        String body = scanner.nextLine();

        try {
            Email originalEmail = emailService.repository.fetchByCode(code);
            if (originalEmail == null) {
                System.out.println("Email with code " + code + " not found.");
                return;
            }
            emailService.reply(originalEmail, currentUser, body);
            System.out.println("Successfully sent your reply to email " + code + ".");
        } catch (Exception e) {
            System.out.println("Failed to reply to email: " + e.getMessage());
        }
    }

    private void handleForwardEmail() {
        System.out.println("Code:");
        String code = scanner.nextLine().trim();
        try {
            Email originalEmail = emailService.repository.fetchByCode(code);
            if (originalEmail == null) {
                System.out.println("Email with code " + code + " not found.");
                return;
            }
            List<Person> recipients = getRecipientsFromInput();
            if (recipients.isEmpty()) {
                System.out.println("No valid recipients found.");
                return;
            }
            emailService.forward(recipients, originalEmail, currentUser);
            System.out.println("Successfully forwarded your email.");
        } catch (Exception e) {
            System.out.println("Failed to forward email: " + e.getMessage());
        }
    }

    private List<Person> getRecipientsFromInput() {
        System.out.println("Recipient(s):");
        String recipientsLine = scanner.nextLine().trim();
        String[] recipientInputs = recipientsLine.split(",\\s*");
        List<String> validEmails = new ArrayList<>();
        for (String input : recipientInputs) {
            try {
                validEmails.add(normalizeEmail(input));
            } catch (InvalidEmailException e) {
                System.out.println(e.getMessage());
                // If any recipient is invalid, stop and return an empty list
                return new ArrayList<>();
            }
        }

        if (validEmails.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Person> persons = personService.repository.fetchByEmail(validEmails);
            if (persons.size() != validEmails.size()) {
                System.out.println("One or more recipients not found.");
                return new ArrayList<>();
            }
            return persons;
        } catch (Exception e) {
            System.out.println("Failed to get recipients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isEmpty() || email.contains(" ")) {
            throw new InvalidEmailException("Email cannot be empty or contain spaces.");
        }
        if (!email.endsWith("@milou.com")) {
            if (email.contains("@")) {
                throw new InvalidEmailException("Only '@milou.com' emails are accepted.");
            }
            return email + "@milou.com";
        }
        return email;
    }

    private void printEmailDetails(Email email, List<String> recipientEmails) {
        System.out.println("Recipient(s): " + String.join(", ", recipientEmails));
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + email.getSentAt());
        System.out.println(email.getBody());
    }
}