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
export DB_URL=jdbc:mysql://<host>:3306/smart_bank_prod
export DB_USERNAME=<production_user>
export DB_PASSWORD=<production_password>

# SMTP — all required
export SMTP_HOST=smtp.yourprovider.com
export SMTP_PORT=587
export SMTP_USERNAME=<production_email>
export SMTP_PASSWORD=<production_password_or_app_password>

# JWT — required (must be ≥256 bits / 32+ characters)
export JWT_SECRET=<a_long_random_secret_key>

# Server (optional)
export PORT=8080
```

### Run with Production Profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Or build and run the JAR:
```bash
./mvnw clean package -DskipTests
java -jar target/SmartBank-Management-System-*.jar --spring.profiles.active=prod
```

The production profile:
- Validates schema only (`ddl-auto: validate`) — never auto-creates tables
- Disables SQL logging
- Uses HikariCP connection pooling
- Logs to `/var/log/smart-bank/application.log`

### First-time Database Setup

When deploying to a new database, temporarily switch to `ddl-auto: update` or run the schema manually:
```sql
CREATE DATABASE IF NOT EXISTS smart_bank_prod;
```

After first run, switch back to `ddl-auto: validate` to prevent accidental schema changes.

---

## Migration from Previous Configuration

If you were running with the old hardcoded configuration:

1. **Before deploying this version**, set the required environment variables (see above).
2. The application will fail to start if `DB_PASSWORD`, `SMTP_USERNAME`, `SMTP_PASSWORD`, or `JWT_SECRET` are not set in production.
3. The JWT secret has changed from the old hardcoded value. **All existing JWT tokens will be invalidated** after deploying this update. Users must log in again.
4. No database migration is required — the schema is unchanged.
5. The `paid_amount` column on the `loans` table (added in a previous update) is auto-managed via `ddl-auto: update`.
