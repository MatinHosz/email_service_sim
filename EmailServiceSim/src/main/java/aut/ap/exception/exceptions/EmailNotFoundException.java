package aut.ap.exception.exceptions;

import aut.ap.exception.EntityNotFoundException;

public class EmailNotFoundException extends EntityNotFoundException {
    public EmailNotFoundException() {
        super("Email not found.");
    }

    public EmailNotFoundException(String message) {
        super(message);
    }
}
