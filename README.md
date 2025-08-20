# Milou: An Email Service Simulator

Milou is a command-line based email client that simulates the core functionalities of an email service. Users can sign up, log in, and manage their emails directly from the terminal. The project is built with Java and Hibernate, demonstrating a clean, layered architecture, as final ap project.

---

### Core Features

* **User Authentication**: Secure sign-up and login functionality.
* **Email Management**:
    * **Send Emails**: Compose and send emails to one or multiple recipients.
    * **View Emails**: View all, unread, or sent emails.
    * **Read by Code**: Fetch and display a specific email using its unique 6-character code.
    * **Reply and Forward**: Easily reply to or forward existing emails.

---

### Tech Stack

* **Language**: Java
* **Database**: MySQL
* **ORM**: Hibernate

---

### Project Architecture

The application follows a layered architecture to ensure a clear separation of concerns:

* **CLI (`aut.ap.cli`)**: The presentation layer that handles all user interactions through the command line.
* **Service (`aut.ap.service`)**: Contains the business logic for handling operations like user registration, sending emails, etc.
* **Repository (`aut.ap.repository`)**: The data access layer that communicates with the database using Hibernate.
* **Model (`aut.ap.model`)**: Defines the JPA entity classes (`Person`, `Email`).
