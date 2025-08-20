package aut.ap.exception.exceptions;

import aut.ap.exception.InvalidEntityException;

public class InvalidPersonException extends InvalidEntityException {
    public InvalidPersonException(String message) {
        super(message);
    }
}
