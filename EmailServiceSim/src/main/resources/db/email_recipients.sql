CREATE TABLE IF NOT EXISTS email_recipients(
    id INT PRIMARY KEY AUTO_INCREMENT,
    email_id INT NOT NULL,
    recipient_id INT NOT NULL,

    FOREIGN KEY (email_id) REFERENCES emails(id),
    FOREIGN KEY (recipient_id) REFERENCES people(id)
);