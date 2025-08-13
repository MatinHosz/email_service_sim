CREATE TABLE IF NOT EXISTS emails (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code NVARCHAR(6) NOT NULL UNIQUE,
    sender_id INT NOT NULL,
    subject NVARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    sent_at DATE NOT NULL,
    parent_email_id INT NOT NULL,
    type ENUM ('normal', 'reply', 'forward'),

    FOREIGN KEY (sender_id) REFERENCES people(id),
    FOREIGN KEY (parent_email_id) REFERENCES emails(id)
);
