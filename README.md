# Event Management System

This project is an Event Management System developed in Java using Spring Boot. It provides features for user registration, event creation, RSVP management, and admin capabilities, making it easy to organize events and track attendance. 

## Features

- **User Authentication**: Secure login and registration.
- **Role-based Access**: Different roles (Admin, Organizer, Participant) with appropriate permissions.
- **Event Management**: Organizers can create, update, view, and delete events.
- **RSVP Management**: Participants can RSVP to events with statuses: ACCEPTED, PENDING, and DECLINED.
- **Admin Controls**: Admins can view and manage all users and events.

## Tech Stack

- **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA
- **Database**: MySQL 
- **Environment Management**: Dotenv for managing sensitive credentials in `.env`

## Setup Instructions for Resume reviewer

1. Prerequisites

- **Java 23**
- **Maven**
- **MySQL**
- **Redis**

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/PhenomenalSid/EventManagement
   cd event-management-system
   
3. **Configure .env and application.properties files for database settings.**

4. **Run the Application:**
 ```bash
 ./mvnw spring-boot:run
```

## API Documentation
https://api.postman.com/collections/23891289-a97d7bd7-902c-42ba-9892-4a9ea2f674ab?access_key=PMAT-01JB86S7F1AHXR3ZDTA44T0ZNW
