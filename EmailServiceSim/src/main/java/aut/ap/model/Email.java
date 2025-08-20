package aut.ap.model;

import java.security.SecureRandom;
import java.time.LocalDate;

import aut.ap.framework.EServiceEntity;
import jakarta.persistence.*;

enum Type {
    NORMAL,
    REPLY,
    FORWARD
}

@Entity
@Table(name = "emails")
public class Email extends EServiceEntity {

    @Basic(optional = false)
    @Column(name = "code")
    private String idCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private Person sender;

    @Basic(optional = false)
    private String subject;

    @Basic(optional = false)
    private String body;

    @Basic(optional = false)
    @Column(name = "sent_at")
    private LocalDate sentAt;

    @OneToOne(optional = true)
    @JoinColumn(name = "parent_email_id")
    private Email parentEmail;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    public Email() {
    }

    public Email(Person sender, String subject, String body, Email parentEmail, String type) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null.");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty.");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Body cannot be null or empty.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty.");
        }
        
        try {
            this.type = Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid email type: " + type);
        }

        idCode = codeGenerator(6);
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        sentAt = LocalDate.now();
        this.parentEmail = parentEmail;
    }

    public String getCode() {
        return idCode;
    }

    public Person getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDate getSentAt() {
        return sentAt;
    }

    public Email getParentEmail() {
        return parentEmail;
    }

    public Type getType() {
        return type;
    }


    public static String codeGenerator(int idCodeLength) {
        SecureRandom secureRandom = new SecureRandom();
        char[] validChars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z' };
        StringBuilder generatedCode = new StringBuilder(idCodeLength);
        for (int i = 0; i < idCodeLength; i++) {
            int index = secureRandom.nextInt(validChars.length);
            generatedCode.append(validChars[index]);
        }
        return generatedCode.toString();
    }


    @Override
    public String toString() {
        return "Email{" +
                "idCode='" + idCode + '\'' +
                ", sender=" + sender.getName() + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", sentAt=" + sentAt +
                ", parentEmail=" + (parentEmail != null ? parentEmail.getCode() : "null") +
                ", type=" + type +
                '}';
    }
}