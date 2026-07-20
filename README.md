# 🏦 Ledger-Core — Enterprise Banking System

![Banner](./SmartBank/screenshots/banner.png)

Enterprise Banking REST API built using Spring Boot and React.js with JWT authentication, deposit/withdrawal operations, secure fund transfers, complete loan lifecycle (apply, approve, disburse, repay), OTP verification, role-based access control, and transaction tracking.

---

## Tech Stack

| Category        | Technologies               |
| --------------- | -------------------------- |
| Backend         | Spring Boot 3.x            |
| Language        | Java 17                    |
| Security        | Spring Security, JWT       |
| Database        | MySQL                      |
| ORM             | Spring Data JPA, Hibernate |
| Frontend        | React.js, Axios            |
| Build Tool      | Maven                      |
| Testing         | JUnit 5, Mockito           |
| API Testing     | Postman                    |
| Version Control | Git, GitHub                |

---

## Repositories

| Layer    | Repository                                                                  |
| -------- | --------------------------------------------------------------------------- |
| Backend  | |
| Frontend | |

---

## Live Deployment

Frontend: 
Backend: 

---

## Features

### Authentication & Security

* JWT-based authentication
* Role-based authorization (CUSTOMER, ADMIN)
* BCrypt password encryption
* OTP email verification (enforced at login)
* All secrets configurable via environment variables / `.env` file
* Ownership verification on all sensitive endpoints
* Protected REST APIs

### Banking Operations

* Multi-account management (SAVINGS, CURRENT, FIXED_DEPOSIT)
* Deposit and withdrawal operations
* Secure fund transfers between accounts
* Real-time balance updates
* Full transaction history with date range filtering
* Account status management (ACTIVE, INACTIVE, FROZEN, CLOSED)

### Loan Management

* Full loan lifecycle: apply → review → approve → disburse → repay
* EMI calculation using standard formula
* Loan approval and rejection workflow
* Loan disbursement to customer's active account
* Partial or full repayment with paid amount tracking
* Automatic status transition to PAID when fully repaid

### Admin Module

* Review and mark loan applications as under review
* Approve or reject loans with remarks
* Disburse approved loans to customer accounts
* Filter and view loans by status
* Customer activity monitoring

---

## Application Flow

### Customer / User Flow

1. **Register** — User signs up via `POST /api/auth/register` with email, password, name, and phone.
2. **Verify Email** — An OTP is sent to the user's email. The user verifies via `POST /api/auth/verify-otp`. Login is blocked until verified.
3. **Login** — User logs in via `POST /api/auth/login`. Returns a JWT token. Unverified users are rejected.
4. **Create Account** — User creates one or more bank accounts (SAVINGS, CURRENT, FIXED_DEPOSIT) via `POST /api/accounts`.
5. **Deposit Funds** — User deposits money into their account via `POST /api/accounts/deposit`.
6. **View Accounts** — User views all their accounts via `GET /api/accounts`.
7. **Transfer Funds** — User transfers funds between any two accounts via `POST /api/transfer`. Both accounts must be ACTIVE.
8. **View Transactions** — User views transaction history for an account via `GET /api/transactions/{accountNumber}` or recent transactions via `GET /api/transactions/{accountNumber}/recent`.
9. **Apply for Loan** — User applies for a loan via `POST /api/loans`. Only one active loan allowed at a time. EMI is calculated automatically.
10. **Track Loans** — User views their loans via `GET /api/loans` or checks details of a specific loan.
11. **Repay Loan** — User repays their loan via `POST /api/loans/{loanId}/repay`. Partial payments are tracked. The loan status becomes PAID when fully repaid.
12. **Withdraw Funds** — User withdraws money from their account via `POST /api/accounts/withdraw`.

### Admin Flow

1. **Login** — Admin logs in with the seeded admin credentials (admin@bank.com / Admin@123).
2. **Review Pending Loans** — Admin views all pending and under-review loans via `GET /api/admin/loans/pending`.
3. **Mark Under Review** — Admin marks a pending loan as under review via `PUT /api/admin/loans/{loanId}/review`.
4. **Approve or Reject** — Admin approves or rejects a loan via `PUT /api/admin/loans/{loanId}/approve`. Approved loans remain in APPROVED status awaiting disbursement.
5. **Disburse Loan** — Admin disburses an approved loan via `PUT /api/admin/loans/{loanId}/disburse`. The loan amount is credited to the customer's first active account. Status becomes DISBURSED.
6. **View Loans by Status** — Admin filters loans by any status via `GET /api/admin/loans?status=PENDING`.
7. **View Any Loan** — Admin views any loan's full details via `GET /api/admin/loans/{loanId}`.

---

## Application Screenshots

### Authentication Module

| Signin Page                        | Registration Page                       |
| --------------------------------- | --------------------------------------- |
| ![Signin](./SmartBank/screenshots/signin.png) | ![Register](./SmartBank/screenshots/register.png) |

---

### Customer Dashboard

| Dashboard                                 | Accounts                                |
| ----------------------------------------- | --------------------------------------- |
| ![Dashboard](./SmartBank/screenshots/myprofile.png) | ![Accounts](./SmartBank/screenshots/accounts.png) |

---

### Fund Transfer Module

| Transfer Form                           | Transfer Success                                        |
| --------------------------------------- | ------------------------------------------------------- |
| ![Transfer](./SmartBank/screenshots/transfer.png) | ![Transfer Success](./SmartBank/screenshots/transfer-success.png) |

---

### Loan Management

| Loan Application                            | Loan Tracking                                     |
| ------------------------------------------- | ------------------------------------------------- |
| ![Loan Apply](./SmartBank/screenshots/loan-apply.png) | ![Loan Tracking](./SmartBank/screenshots/loan-tracking.png) |

---

### Admin Module

| Admin Dashboard                                       | Loan Review                                     |
| ----------------------------------------------------- | ----------------------------------------------- |
| ![Admin Dashboard](./SmartBank/screenshots/admin-dashboard.png) | ![Admin Review](./SmartBank/screenshots/admin-review.png) |

---

## 🏗️ Project Structure

```bash
smart-bank/
├── src/main/java/com/bank/smartbank/
│   ├── config/              # Configuration classes
│   │   └── DataSeeder.java
│   ├── controller/          # REST Controllers
│   │   ├── AuthController.java
│   │   ├── AccountController.java
│   │   ├── TransferController.java
│   │   ├── TransactionController.java
│   │   ├── LoanController.java
│   │   └── AdminController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── common/
│   │   ├── auth/
│   │   ├── account/
│   │   ├── transaction/
│   │   └── loan/
│   ├── entity/              # JPA Entities
│   │   ├── User.java
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   └── Loan.java
│   ├── exception/           # Custom Exceptions
│   ├── repository/          # Spring Data Repositories
│   ├── security/            # Security Configuration
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── SecurityConfig.java
│   ├── service/             # Business Logic
│   │   ├── AuthService.java
│   │   ├── AccountService.java
│   │   ├── TransactionService.java
│   │   ├── TransferService.java
│   │   └── LoanService.java
│   └── util/                # Utility Classes
│       ├── Constants.java
│       ├── OtpGenerator.java
│       ├── AccountNumberGenerator.java
│       └── EmailService.java
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

---

## Database Entities

* Users
* Accounts
* Transactions
* Loans

### Relationships

* One User → Multiple Accounts
* One Account → Multiple Transactions
* One User → Multiple Loans

---

## API Modules

| Module         | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| Authentication | Register, Login, OTP Verification, Resend OTP                |
| Accounts       | Account Creation, Retrieval, Deposit, Withdrawal             |
| Transfers      | Secure Fund Transfers between accounts                       |
| Transactions   | Transaction History, Recent Transactions, Date Range Filter  |
| Loans          | Loan Application, Customer Loan List, Loan Repayment         |
| Admin          | Loan Review, Approval/Rejection, Disbursement, Status Filter |

---

## EMI Formula

```text
EMI = [P × R × (1 + R)^N] / [(1 + R)^N − 1]
```

Where:

* P = Principal Amount
* R = Monthly Interest Rate
* N = Loan Tenure in Months

---

## Local Setup

### Clone Repository

```bash
git clone 
cd ledger-core
```

### Configure Environment

Copy the `.env` template and adjust values as needed:

```bash
cp SmartBank-Management-System/.env.example SmartBank-Management-System/.env
```

Or use the variables directly:

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/smart_bank
DB_USERNAME=root
DB_PASSWORD=1234

# SMTP (optional — email fails silently without valid credentials)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password

# JWT (must be ≥32 characters for HMAC-SHA256)
JWT_SECRET=YourSecretKeyHereAtLeast32CharsLong
JWT_EXPIRATION_MS=86400000

# Server
PORT=8080
```

The application also works without a `.env` file — all variables have sensible development defaults in `application.yml`.

### Create Database

```sql
CREATE DATABASE IF NOT EXISTS smart_bank;
```

### Run Application

```bash
cd SmartBank-Management-System
./mvnw spring-boot:run
```

Application URL:

```bash
http://localhost:8080
```

Default admin credentials (auto-seeded):

```
Email:    admin@bank.com
Password: Admin@123
```

---

## Testing

```bash
cd SmartBank-Management-System
./mvnw test
```

### Testing Tools

* JUnit 5
* Mockito
* Postman

---

## Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for complete deployment instructions.

| Layer    | Platform                   |
| -------- | -------------------------- |
| Frontend | Vercel                     |
| Backend  | Render                     |
| Database | Render MySQL               |

`render.yaml` is included for infrastructure-as-code setup. You can also configure everything manually via the [Render Dashboard](https://dashboard.render.com/).

---

## Engineering Concepts

* RESTful API Design
* Layered Architecture
* DTO Pattern
* Exception Handling
* JWT Authentication
* Transaction Management
* Role-Based Authorization
* BCrypt Password Encryption

---

## Future Improvements

* Redis Caching
* Kafka Event Streaming
* Microservices Migration

---

## 👨‍💻 Author

### Lankalapalli Guna

GitHub:

LinkedIn:

Email:
[lankalapalligunapersonal@gmail.com](mailto:lankalapalligunapersonal@gmail.com)

---

## Project Highlights

* Developed secure banking workflows using Spring Boot
* Implemented JWT authentication and role-based authorization
* Designed scalable layered backend architecture
* Integrated React frontend with REST APIs
* Built full loan lifecycle (apply, approve, disburse, repay)
* Implemented deposit, withdrawal, and fund transfer operations
* All secrets configurable via environment variables / `.env`
* Docker containerized for Render deployment

---
