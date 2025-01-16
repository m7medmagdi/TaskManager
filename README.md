# Task Manager

Task Management system with register and login and create and update and delete tasks
## Table of Contents
1. [Technologies Used](#technologies-used)
2. [Prerequisites](#prerequisites)
3. [Setup and Installation](#setup-and-installation)
4. [Running the Application](#running-the-application)
5. [API Endpoints](#api-endpoints)


---

## Technologies Used
- **Java**: 17
- **Jakarta EE**: 10
- **Maven**: 3.8.6
- **Tomcat Server**: 10
- **MySQL**: 8.0
- **Session Management**: Custom session handling using `SessionManager`
- **Password Hashing**: BCrypt for secure password storage

---

## Prerequisites
Before running the project, ensure you have the following installed:
- Java Development Kit (JDK) 17 or higher
- Maven 3.8.6 or Gradle 7.5
- Tomcat 10
- MySQL 8.0

---

## Setup and Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/m7medmagdi/TaskManager
   cd your-project
  
   
2. Edit:  edite persistence.xml yo your Database Info
   ```bash
    <!-- Database connection properties -->
    <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/yourDBname"/>
    <property name="jakarta.persistence.jdbc.user" value="root"/>
    <property name="jakarta.persistence.jdbc.password" value="password"/>

3. Build:
   ```bash
   mvn clean install
4. ## Running the Application
   ```bash
    Copy the generated .war file (located in the target folder) to the webapps directory of your Apache Tomcat installation.
    and you can test register and login and index frontend

    Alternatively, use your IDE (e.g., IntelliJ IDEA, Eclipse) to deploy the project directly to Tomcat

5. ## API Endpoints
Examples API and login
    Register
   ```bash
   curl -X POST http://localhost:8080/your-project/auth/register \
    -H "Content-Type: application/json" \
    -d '{
    "email": "user@example.com",
    "password": "securepassword"
    }'
   
```bash
    curl -X POST http://localhost:8080/your-project/auth/login \
    -H "Content-Type: application/json" \
    -d '{
    "email": "user@example.com",
    "password": "securepassword"
    }'
```

All Requests need header session 
- Get All Tasks
Endpoint: GET /tasks

Description: Retrieves all tasks for the logged-in user.
- Requires a valid session ID in the X-Session-ID header.

- Headers:
```bash
     X-Session-ID: Valid session ID
```
Create a Task
Endpoint: POST /tasks

```bash
    {
       "title": "New Task",
       "description": "Description of the new task",
       "completed": false
    }
```
