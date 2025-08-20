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
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("L") || command.equalsIgnoreCase("Login"))
                handleLogin();
            else if (command.equalsIgnoreCase("S") || command.equalsIgnoreCase("Sign up"))
                handleSignUp();
            else if (command.equalsIgnoreCase("E") || command.equalsIgnoreCase("Exit")) {
                System.out.println("Goodbye!");
                SingletonSessionFactory.shutdown(); // Gracefully shutdown hibernate
                return;
            } else
                System.out.println("Invalid command.");
        }
    }

    private void handleSignUp() {
        System.out.println("Name:");
        String name = scanner.nextLine();
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();

        try {
            personService.register(name, email, password);
            System.out.println("Your new account is created.\nGo ahead and login!");
        } catch (InvalidEmailException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.println("Email:");
        String email = scanner.nextLine();
        if (!email.contains("@")) {
            email += "@milou.com";
        }
        System.out.println("Password:");
        String password = scanner.nextLine();

        try {
            currentUser = personService.login(email, password);
            System.out.println("Welcome back, " + currentUser.getName() + "!");
            showUnreadEmails();
            loggedInMenu();
        } catch (InvalidEmailException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showUnreadEmails() {
        List<Email> unreadEmails = emailService.repository.fetchAllUnread(currentUser.getId());
        if (!unreadEmails.isEmpty()) {
            System.out.println("Unread Emails:");
            System.out.println(unreadEmails.size() + " unread emails:");
            for (Email email : unreadEmails) {
                System.out.println(
                        "+ " + email.getSender().getEmail() + "\t" + email.getSubject() + " (" + email.getCode() + ")");
            }
        }
    }

    private void loggedInMenu() {
        while (currentUser != null) {
            System.out.println("[S]end, [V]iew, [R]eply, [F]orward, [L]ogout:");
            String command = scanner.nextLine();
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
        String command = scanner.nextLine();
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
            Collections.reverse(receivedEmails); // Show newest first
            System.out.println("All Emails:");
            for (Email email : receivedEmails) {
                System.out.println(
                        "+ " + email.getSender().getEmail() + "\t" + email.getSubject() + " (" + email.getCode() + ")");
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch emails: " + e.getMessage());
        }
    }

    private void viewUnreadEmails() {
        try {
            List<Email> unreadEmails = emailService.repository.fetchAllUnread(currentUser.getId());
            Collections.reverse(unreadEmails); // Show newest first
            System.out.println("Unread Emails:");
            for (Email email : unreadEmails) {
                System.out.println(
                        "+ " + email.getSender().getEmail() + "\t" + email.getSubject() + " (" + email.getCode() + ")");
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
                List<String> recipientEmails = new ArrayList<>();
                for (Person recipient : recipients) {
                    recipientEmails.add(recipient.getEmail());
                }
                System.out.println("+ " + String.join(", ", recipientEmails) + "\t" + email.getSubject() + " ("
                        + email.getCode() + ")");
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch sent emails: " + e.getMessage());
        }
    }

    private void viewEmailByCode() {
        System.out.println("Code:");
        String code = scanner.nextLine();
        try {
            Email email = emailService.repository.fetchByCode(code);
            List<Person> recipients = emailService.repository.getRecipients(email.getId());
            boolean isRecipient = recipients.stream().anyMatch(p -> p.getId().equals(currentUser.getId()));

            // Check if the user is authorized to read the email



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
            } else {
                System.out.println("You cannot read this email.");
            }
        } catch (Exception e) {
            System.out.println("Failed to view email: " + e.getMessage());
        }
    }

    private void handleReplyToEmail() {
        System.out.println("Code:");
        String code = scanner.nextLine();
        System.out.println("Body:");
        String body = scanner.nextLine();

        try {
            Email originalEmail = emailService.repository.fetchByCode(code);
            emailService.reply(originalEmail, currentUser, body);
            System.out.println("Successfully sent your reply to email " + code + ".");
        } catch (Exception e) {
            System.out.println("Failed to reply to email: " + e.getMessage());
        }
    }

    private void handleForwardEmail() {
        System.out.println("Code:");
        String code = scanner.nextLine();
        try {
            List<Person> recipients = getRecipientsFromInput();
            if (recipients.isEmpty()) {
                System.out.println("No valid recipients found.");
                return;
            }
            Email originalEmail = emailService.repository.fetchByCode(code);
            emailService.forward(recipients, originalEmail, currentUser);
            System.out.println("Successfully forwarded your email.");
        } catch (Exception e) {
            System.out.println("Failed to forward email: " + e.getMessage());
        }
    }

    private List<Person> getRecipientsFromInput() {
        try {
            System.out.println("Recipient(s):");
            String recipientsLine = scanner.nextLine();
            String[] recipientEmails = recipientsLine.split(",\\s*");
            List<String> recipientEmailList = new ArrayList<>();
            for (String email : recipientEmails) {
                if (!email.contains("@")) {
                    email += "@milou.com";
                }
                recipientEmailList.add(email);
            }
            return personService.repository.fetchByEmail(recipientEmailList);
        } catch (Exception e) {
            System.out.println("Failed to get recipients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Print details of an email in a formatted way
    private void printEmailDetails(Email email, List<String> recipientEmails) {
        System.out.println("Recipient(s): " + String.join(", ", recipientEmails));
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + email.getSentAt());
        System.out.println(email.getBody());
    }
}