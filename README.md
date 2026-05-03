🏦 Full Stack Banking Application

A production-ready banking system built with Spring Boot and React.js

## ✨ Features
- ✅ JWT Authentication + BCrypt password hashing
- ✅ Savings & Current account types
- ✅ Deposit, Withdraw, Transfer with PIN verification
- ✅ Account lockout after 3 failed PIN attempts
- ✅ Transaction history
- ✅ RESTful APIs with Spring Security

## 🛠️ Tech Stack
| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Security |
| Database | MySQL 8, Hibernate ORM, JPA |
| Auth | JWT, BCrypt |
| Frontend | React 18, Axios, React Router |

## 📡 API Endpoints
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register user |
| POST | /api/auth/login | Login & get JWT |
| GET | /api/accounts | Get all accounts |
| POST | /api/accounts | Create account |
| POST | /api/accounts/deposit | Deposit money |
| POST | /api/accounts/withdraw | Withdraw money |
| POST | /api/accounts/transfer | Transfer money |
