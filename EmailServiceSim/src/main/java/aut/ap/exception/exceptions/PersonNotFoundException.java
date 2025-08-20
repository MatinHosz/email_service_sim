package aut.ap.exception.exceptions;

import aut.ap.exception.EntityNotFoundException;

public class PersonNotFoundException extends EntityNotFoundException {
	public PersonNotFoundException() {
		super("Person not found.");
	}

	public PersonNotFoundException(String message) {
		super(message);
	}
}
