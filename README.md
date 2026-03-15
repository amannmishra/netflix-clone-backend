# Netflix Clone Backend

A Spring Boot backend for Netflix Clone application. Handles user authentication, video management, subscriptions, and email notifications.

---

## 🛠 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- JWT Authentication
- Spring Mail
- REST API
- File Upload (Videos & Images)

---

## 🚀 Features

- User registration & login (JWT secured)
- CRUD operations for videos & categories
- File uploads for videos and images
- Email notifications (SMTP)
- User subscription management

---

## 📂 Project Structure


src/
├─ main/java/com/netflixclone
│ ├─ controller
│ ├─ service
│ ├─ repository
│ └─ model
└─ resources
├─ application.properties
└─ static/


---

## ⚙️ Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/your-username/netflix-clone-backend.git
cd netflix-clone-backend
2. Configure MySQL
CREATE DATABASE pulsescreen;

Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/pulsescreen
spring.datasource.username=root
spring.datasource.password=yourpassword
3. Build & Run
mvn clean install
mvn spring-boot:run

Backend will run at http://localhost:8080

4. API Endpoints

/api/auth/register – Register user

/api/auth/login – Login user

/api/videos – CRUD videos

/api/subscription – Manage subscriptions

💾 File Upload Paths

Video uploads: uploads/videos

Image uploads: uploads/images

Maximum file size: 2GB

📧 Email Setup

Uses Gmail SMTP

Configured in application.properties

Required for notifications and subscription emails

📌 Notes

Java 17 and MySQL required

Backend must be running before connecting frontend
