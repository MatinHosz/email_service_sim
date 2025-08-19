package aut.ap.exception;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String massage) {
        super(massage);
    }
}
