# Deployment Guide — Smart Bank Management System

## Table of Contents

- [Local Development](#local-development)
- [Render Deployment](#render-deployment)
- [Production Verification](#production-verification)
- [Rollback](#rollback)

---

## Local Development

### Prerequisites

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.9+** or use the bundled wrapper (`./mvnw`)
- **MySQL 8+** running on `localhost:3306`
- **Git**

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd ledger-core
   ```

2. **Create the database:**
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_bank;
   ```

3. **(Optional) Configure environment variables:**
   ```bash
   cp SmartBank-Management-System/.env.example SmartBank-Management-System/.env
   ```
   Edit `SmartBank-Management-System/.env` with your local settings. The app works without this file — all variables have sensible defaults.

4. **Build and run:**
   ```bash
   cd SmartBank-Management-System
   ./mvnw clean package -DskipTests
   ./mvnw spring-boot:run
   ```

5. **Verify:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```
   Expected response: `{"status":"UP"}`

6. **Default admin credentials (auto-seeded):**
   - Email: `admin@bank.com`
   - Password: `Admin@123`

---

## Render Deployment

### Prerequisites

- A [Render](https://render.com/) account (free tier works)
- Your project pushed to a GitHub repository

### Step 1: Create a Render Web Service

1. Log in to [Render Dashboard](https://dashboard.render.com/)
2. Click **New +** → **Web Service**
3. Connect your GitHub account and select the `ledger-core` repository
4. Render will auto-detect the `Dockerfile` at the project root

### Step 2: Configure the Service

Fill in the following:

| Setting | Value |
|---------|-------|
| **Name** | `smart-bank` |
| **Environment** | `Docker` |
| **Region** | Choose the closest to your users |
| **Branch** | `main` (or your default branch) |
| **Health Check Path** | `/actuator/health` |
| **Auto-Deploy** | `Yes` (recommended) |

### Step 3: Add a MySQL Database

1. In your Render dashboard, go to **Dashboard** → **New +** → **MySQL**
2. Choose a name (e.g., `smart-bank-db`)
3. Select the free tier (if available) or a paid plan
4. Click **Create Database**
5. Once provisioned, go to the MySQL dashboard to find the connection details

### Step 4: Configure Environment Variables

Go to your Web Service **Environment** tab and add:

| Variable | Required | How to Get Value |
|----------|----------|------------------|
| `DB_URL` | Yes | Convert Render MySQL connection string to JDBC format (see below) |
| `DB_USERNAME` | Yes | From your Render MySQL service dashboard |
| `DB_PASSWORD` | Yes | From your Render MySQL service dashboard |
| `JWT_SECRET` | Yes | Generate: `openssl rand -base64 48` (must be >=32 chars) |
| `JWT_EXPIRATION_MS` | No | Default: `86400000` (24 hours) |
| `SMTP_HOST` | No | Your SMTP provider (e.g., `smtp.gmail.com`) |
| `SMTP_PORT` | No | Default: `587` |
| `SMTP_USERNAME` | No | Required for email features |
| `SMTP_PASSWORD` | No | Required for email features |
| `APP_CORS_ALLOWED_ORIGINS` | Yes | Your frontend URL (e.g., `https://bank-frontend.vercel.app`) |

**Converting MySQL connection string to JDBC URL:**

Render provides a connection string like:
```
mysql://user:password@host:port/database
```

Convert it to JDBC format:
```
jdbc:mysql://host:port/database?sslMode=REQUIRED
```

For example, if Render gives you:
```
mysql://smart_bank_user:abc123@mysql-db.render.com:3306/smart_bank
```

Set `DB_URL` to:
```
jdbc:mysql://mysql-db.render.com:3306/smart_bank?sslMode=REQUIRED
```

### Step 5: Deploy

1. Click **Create Web Service** — Render will clone your repo, build the Docker image, and deploy
2. Monitor the build logs in the **Events** tab
3. Once deployed, your service URL will be: `https://smart-bank.onrender.com`

### Auto-Deploy

Render auto-deploys whenever you push to the connected branch. To trigger a manual deploy:

1. Go to your Web Service dashboard
2. Click **Manual Deploy** → **Deploy latest commit**

### Common Deployment Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `Connection refused` to database | Wrong host or port | Verify `DB_URL` points to Render MySQL host |
| `SSL connection required` | MySQL SSL mismatch | Add `?sslMode=REQUIRED` to `DB_URL` |
| `Hibernate ddl-auto` fails | Schema mismatch | Database is empty — `ddl-auto: update` will auto-create tables |
| `java.lang.OutOfMemoryError` | Insufficient memory | Upgrade Render plan or add JVM flags in Dockerfile |
| `Port already in use` | PORT collision | Render sets `PORT` automatically — do NOT hardcode it |
| CORS errors | Wrong frontend origin | Set `APP_CORS_ALLOWED_ORIGINS` to your frontend URL exactly |
| Email not sending | Invalid SMTP | Verify `SMTP_USERNAME` and `SMTP_PASSWORD` |
| 403 on health check | Security filter blocking | Ensure `/actuator/health` is permitted (it is by default) |
| Build timeout (60+ min) | Large build | Free tier has build limits; upgrade if needed |
| Container crash on start | Missing env vars | Verify all required env vars are set |

### Troubleshooting

**Enable debug logs temporarily:**
```bash
# In Render Environment Variables, add:
LOGGING_LEVEL_COM_BANK_SMARTBANK=DEBUG
```

**Check startup logs:**
1. Go to your Web Service dashboard
2. Click **Logs** tab
3. Filter by **Start** or **Build**

**Access the shell (Render Pro):**
1. Go to your Web Service dashboard
2. Click **Shell** tab
3. Run commands inside the running container

**Test health endpoint:**
```bash
curl https://smart-bank.onrender.com/actuator/health
```

**Cold start delay:**
Render's free tier spins down after 15 minutes of inactivity. The first request after idle will take 30–60 seconds while the service starts up. This is normal.

---

## Production Verification

After Render finishes deploying, verify the following:

### 1. Health Check
```bash
curl https://smart-bank.onrender.com/actuator/health
```
Expected: `{"status":"UP"}`

### 2. API Response
```bash
curl -X POST https://smart-bank.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bank.com","password":"Admin@123"}'
```
Expected: A JSON response with a JWT token.

### 3. Database Connection
- Create a new user via `POST /api/auth/register`
- Verify the user is persisted by logging in

### 4. CORS Check (from frontend)
```javascript
fetch('https://smart-bank.onrender.com/api/accounts', {
  headers: { 'Authorization': 'Bearer <token>' }
})
```
Should not throw CORS errors.

### 5. Application Info
```bash
curl https://smart-bank.onrender.com/actuator/info
```
Expected: Application info JSON.

---

## Rollback

### Option 1: Render Dashboard (Recommended)

1. Go to your Web Service dashboard
2. Click **Events** tab
3. Find the last known-good deployment
4. Click **...** → **Deploy**

### Option 2: Git Revert

```bash
# Revert to a previous commit
git revert HEAD~1  # Revert last commit
git push origin main
```

Render will automatically redeploy the reverted code.

### Option 3: Environment Variable Rollback

If the issue is caused by misconfigured variables:

1. Go to **Environment** tab
2. Edit or remove the problematic variable
3. Render will restart the service automatically

---

## Database Note: Schema Management

This project uses `ddl-auto: update` — Hibernate auto-creates and updates database tables based on JPA entity definitions. This is the simplest approach for a portfolio project.

**Why `update` instead of `validate` or `none`?**

- The project does not use Flyway or Liquibase for migrations
- Tables must exist for the application to function
- `update` ensures the schema is always in sync with entities on first deploy
- When deploying to a fresh Render MySQL instance, schema auto-creation is essential

**When to use `validate`:**
Only if you add Flyway/Liquibase and manage schema manually.
