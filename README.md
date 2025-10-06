u can check backend code there --> [ScalesAPI](https://github.com/eminjafarli/ScalesAPI)


## SCALES

⚖️ Scales is a system developed for an agricultural company to manage and complete purchases directly at the factory. The application integrates hardware scales with a digital system to automate the weighing process, record transactions, and generate purchase receipts.

Currently, the app supports only Azerbaijani, but English support will be added in the future.

---

## 📌 Features

### Automated Weighing Process

• First measurement records the gross weight (container full).

• Second measurement records the tare weight (empty container).

• Final net weight is calculated automatically (gross – tare).

### Data Management

• All transactions are stored in MS SQL Server.

• Integrated form to enter and review purchase details.

• Receipt Generation

• A digital check/receipt is generated and provided to the buyer after weighing.

---

## 🛠 Tech Stack

Frontend (Desktop): JavaFX

Backend: Spring Boot, Spring Data JPA, Spring Security, REST API

Database: Microsoft SQL Server (MS SQL)

UI Framework: Bootstrap

---

## 🔐 Security

User login system is built with Spring Security.

JWT (JSON Web Tokens) are used for authentication/authorization.

Passwords are securely encrypted before being stored.

---

## 📂 Project Structure

Scales → JavaFX application (user interface for operators).

ScalesAPI → Spring Boot REST API (handles authentication, data storage, and business logic).

---

## 📸 Example Workflow

Video here
