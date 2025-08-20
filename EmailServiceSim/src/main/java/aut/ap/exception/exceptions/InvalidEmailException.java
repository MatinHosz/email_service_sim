package aut.ap.exception.exceptions;

import aut.ap.exception.InvalidEntityException;

public class InvalidEmailException extends InvalidEntityException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
