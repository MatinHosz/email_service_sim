CREATE TABLE IF NOT EXISTS eamil_reads(
    id INT PRIMARY KEY AUTO_INCREMENT,
    email_id INT NOT NULL,
    user_id INT NOT NULL,
    read_at DATE NOT NULL,

    FOREIGN KEY (email_id) REFERENCES emails(id),
    FOREIGN KEY (user_id) REFERENCES people(id)
);