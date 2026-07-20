# Smart Bank — Setup Guide

## Required Environment Variables

### Database

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_URL` | No | `jdbc:mysql://localhost:3306/smart_bank` | JDBC connection URL |
| `DB_USERNAME` | No | `root` | Database user |
| `DB_PASSWORD` | Yes (prod), No (dev) | `1234` | Database password |

### Mail (SMTP)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SMTP_HOST` | No | `smtp.gmail.com` | SMTP server host |
| `SMTP_PORT` | No | `587` | SMTP server port |
| `SMTP_USERNAME` | Yes (prod), No (dev) | `dev@smartbank.com` | SMTP username |
| `SMTP_PASSWORD` | Yes (prod), No (dev) | `dev` | SMTP password or app password |

### JWT

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `JWT_SECRET` | Yes (prod), No (dev) | `SmartBankDevSecretKey2024ForJWTTokenGeneration` | HMAC-SHA key (must be ≥256 bits / 32 characters) |
| `JWT_EXPIRATION_MS` | No | `86400000` | Token expiry in milliseconds (default: 24h) |

### Server

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PORT` | No | `8080` | Server port |

---

## Local Development Setup

### Prerequisites

- Java 17+
- MySQL 8+ running on localhost:3306
- Maven (or use the bundled `mvnw` wrapper)

### Steps

1. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_bank;
   ```

2. (Optional) Set environment variables for non-default credentials:
   ```powershell
   # PowerShell
   $env:DB_PASSWORD = "your_password"
   $env:SMTP_USERNAME = "your_email@gmail.com"
   $env:SMTP_PASSWORD = "your_app_password"
   $env:JWT_SECRET = "Your256BitPlusLongSecretKeyForJWTHS256"
   ```

3. Build and run:
   ```bash
   ./mvnw clean compile
   ./mvnw spring-boot:run
   ```

   Without any env vars set, the application uses defaults:
   - Database: `root:1234@localhost:3306/smart_bank`
   - SMTP: not functional (uses dummy credentials — email sending will fail silently)
   - JWT: dev secret (change for any non-local use)

4. Admin credentials (auto-seeded):
   - Email: `admin@bank.com`
   - Password: `Admin@123`

### Default Behavior Without Environment Variables

The application starts immediately with no env vars. Email sending will fail silently (logged but not blocking). This is acceptable for local development and testing.

---

## Production Setup

### Required Environment Variables

```bash
# Database — all required
export DB_URL=jdbc:mysql://<host>:3306/smart_bank
export DB_USERNAME=<production_user>
export DB_PASSWORD=<production_password>

# SMTP — all required
export SMTP_HOST=smtp.yourprovider.com
export SMTP_PORT=587
export SMTP_USERNAME=<production_email>
export SMTP_PASSWORD=<production_password_or_app_password>

# JWT — required (must be ≥256 bits / 32+ characters)
export JWT_SECRET=<a_long_random_secret_key>

# CORS — required for frontend access
export APP_CORS_ALLOWED_ORIGINS=<your_frontend_url>

# Server (optional)
export PORT=8080
```

### Build and Run

```bash
cd SmartBank-Management-System
./mvnw clean package -DskipTests
java -jar target/SmartBank-Management-System-*.jar
```

The application uses a single `application.yml` with environment variable overrides. All production settings are configured via environment variables — no separate profile is needed.

HikariCP connection pooling is always active. Schema auto-management (`ddl-auto: update`) ensures tables are created on first deploy.

### First-time Database Setup

The application auto-creates the schema on first startup via `ddl-auto: update`. Just ensure the database exists:
```sql
CREATE DATABASE IF NOT EXISTS smart_bank;
```
