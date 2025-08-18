package aut.ap.service;

import aut.ap.framework.ServiceBase;
import aut.ap.model.Email;
import aut.ap.model.Person;
import java.util.List;

public class EmailService extends ServiceBase<Email> {

    public EmailService() {
        super(Email.class);
    }

    public Email persist() {

    }

    public Email fetchByCode(String code) {
        return getSessionFactory().fromTransaction(session -> {

        });
    }

    // TODO: Complete this class after refactoring emails read status
}
