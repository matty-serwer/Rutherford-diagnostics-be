# Rutherford Diagnostics Backend - Environment Configuration Guide

This document explains how to properly configure the application with **zero secrets in version control** using separate configuration files and environment variables.

## 🔒 Security-First Configuration Approach

**Key Principle**: Configuration templates in git contain **NO SECRETS** - only structure and placeholders.

### Files in Version Control (Templates Only)
- `application.properties` - Base template with empty placeholders
- `application-dev.properties` - Development-specific settings (no secrets)
- `application-prod.properties` - Production-specific settings (no secrets)
- `application-local.properties.example` - Example file for developers

### Files NOT in Version Control (Actual Secrets)
- `application-local.properties` - Your real secrets for local development
- `application-secrets.properties` - Production testing secrets
- `.env*` files - Environment variable files

## Quick Start - New Developer Setup

### 1. Clone and Setup Local Configuration
```bash
# 1. Clone the repository
git clone your-repo-url
cd rutherford-diagnostics-be

# 2. Copy the example file to create your local secrets file
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties

# 3. Edit application-local.properties with your real database credentials
# (This file is gitignored and will never be committed)
```

### 2. Configure Your Local Secrets
Edit `src/main/resources/application-local.properties`:
```properties
# YOUR LOCAL SECRETS - This file is gitignored and safe to edit

# Database Configuration (replace with your actual values)
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/diagnostics_dashboard
JDBC_DATABASE_USERNAME=your_actual_username
JDBC_DATABASE_PASSWORD=your_actual_password

# CORS Configuration for your local development
DEV_CORS_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8080,http://localhost:10000

# Management endpoints for development
DEV_MANAGEMENT_ENDPOINTS=health,info,metrics,env,configprops,beans,mappings

# Optional: Override any other settings
SERVER_PORT=8080
JPA_DDL_AUTO=update
JPA_SHOW_SQL=true
```

### 3. Run the Application
```bash
# Run with development profile (loads dev + local config)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or via IDE: Set active profile to "dev"
```

## Configuration Loading Order

Spring Boot loads configuration in this order (later overrides earlier):

1. **`application.properties`** - Base template (no secrets)
2. **`application-dev.properties`** - Dev-specific settings (if profile=dev)
3. **`application-local.properties`** - Your local secrets ⚠️ GITIGNORED
4. **Environment Variables** - Highest priority (production)

## Environment Variables Reference

### Required for All Environments
```bash
# Database Configuration
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/diagnostics_dashboard
JDBC_DATABASE_USERNAME=your_db_user
JDBC_DATABASE_PASSWORD=your_db_password

# CORS Origins (environment-specific)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:10000  # Dev
# or
CORS_ALLOWED_ORIGINS=https://yourdomain.com                       # Prod
```

### Development-Specific Variables
```bash
# Development CORS settings
DEV_CORS_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8080,http://localhost:10000

# Development management endpoints
DEV_MANAGEMENT_ENDPOINTS=health,info,metrics,env,configprops,beans,mappings

# Database behavior
JPA_DDL_AUTO=update                 # Options: validate, update, create, create-drop
JPA_SHOW_SQL=true                   # Show SQL queries in development
```

### Production-Specific Variables
```bash
# Production CORS (restrict to your actual domains)
PROD_FRONTEND_URLS=https://yourdomain.com,https://admin.yourdomain.com

# Production database behavior
PROD_DDL_AUTO=validate              # Never use 'update' in production!

# Monitoring origins (for health checks)
MONITORING_ORIGINS=https://datadog.com,https://newrelic.com

# Security settings
HEALTH_SHOW_DETAILS=when_authorized # Options: never, when_authorized, always
```

## Deployment Scenarios

### Local Development
```bash
# 1. Use your application-local.properties file (created above)
# 2. Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# No environment variables needed - everything is in your local file
```

### Docker Development
```yaml
# docker-compose.yml
services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - JDBC_DATABASE_URL=jdbc:postgresql://db:5432/diagnostics_dashboard
      - JDBC_DATABASE_USERNAME=postgres
      - JDBC_DATABASE_PASSWORD=your_docker_password
      - DEV_CORS_ORIGINS=http://localhost:3000,http://localhost:10000
```

### Production (Heroku/Railway/Render)
```bash
# Set these environment variables in your deployment platform:
SPRING_PROFILES_ACTIVE=prod
JDBC_DATABASE_URL=postgresql://prod-server:5432/prod_db
JDBC_DATABASE_USERNAME=prod_user
JDBC_DATABASE_PASSWORD=super_secure_prod_password
PROD_FRONTEND_URLS=https://your-frontend.vercel.app
MONITORING_ORIGINS=https://your-monitoring-service.com
```

### AWS/Cloud Production
```bash
# Via AWS Parameter Store, Secrets Manager, or environment variables:
SPRING_PROFILES_ACTIVE=prod
JDBC_DATABASE_URL=jdbc:postgresql://your-rds.amazonaws.com:5432/prod_db
JDBC_DATABASE_USERNAME=prod_user
JDBC_DATABASE_PASSWORD=retrieved_from_secrets_manager
PROD_FRONTEND_URLS=https://your-cloudfront-domain.com
```

## Testing Your Configuration

### Verify CORS is Working

**With Postman:**
- ✅ Health endpoint: `GET http://localhost:8080/actuator/health`
- ✅ Business health: `GET http://localhost:8080/health/alerts`
- ✅ Patient data: `GET http://localhost:8080/patient`

**With Frontend (localhost:3000):**
```javascript
// Should work without CORS errors in dev mode
fetch('http://localhost:8080/patient')
  .then(response => response.json())
  .then(data => console.log('Success!', data))
  .catch(error => console.error('CORS Error:', error));
```

**With Different Port (localhost:10000):**
```javascript
// Should also work if configured in DEV_CORS_ORIGINS
fetch('http://localhost:8080/health/alerts')
  .then(response => response.json())
  .then(data => console.log('Health alerts:', data));
```

### Troubleshooting CORS Issues

1. **"Access to fetch blocked by CORS policy"**
   - Check your `DEV_CORS_ORIGINS` in `application-local.properties`
   - Ensure your frontend URL is included

2. **Application won't start - database connection error**
   - Verify your `JDBC_DATABASE_*` values in `application-local.properties`
   - Check if PostgreSQL is running

3. **Can't access actuator endpoints**
   - Check `DEV_MANAGEMENT_ENDPOINTS` setting
   - Verify security configuration allows the endpoints

## Security Best Practices

### ✅ What We Do Right
- **Zero secrets in git** - Templates only, no real values
- **Separate local files** - `application-local.properties` is gitignored
- **Environment-specific configs** - Dev vs. prod separation
- **Secure production** - All secrets via environment variables

### ⚠️ What to Never Do
- **Never commit real passwords** - Even in example files
- **Never use hardcoded secrets** - Always use placeholders
- **Never commit .env files** - They often contain real secrets
- **Never use 'update' DDL in production** - Use 'validate' only

### 🔒 Production Security
- Use your cloud platform's secret management (AWS Secrets Manager, etc.)
- Rotate database passwords regularly
- Restrict CORS to only your actual frontend domains
- Monitor access to health endpoints
- Use HTTPS everywhere in production

## Profile-Specific Behavior

### `dev` Profile
- Loads both `application-dev.properties` + `application-local.properties`
- Permissive CORS for all localhost ports
- Shows detailed error messages and stack traces
- Enables debug logging for CORS and security
- Exposes additional actuator endpoints for debugging
- Shows formatted SQL queries

### `prod` Profile  
- Uses only environment variables (no local files)
- Restricts CORS to specified production domains
- Hides all error details and stack traces
- Minimal logging for performance and security
- Validates database schema only (no auto-updates)
- Limited actuator endpoints (health, info, metrics only)
- Secure cookie settings and headers

### Default Profile (if none specified)
- Uses base `application.properties` template only
- Requires all values via environment variables
- Minimal configuration - suitable for CI/CD environments

## File Structure Summary

```
src/main/resources/
├── application.properties                    # ✅ COMMITTED - Base template, no secrets
├── application-dev.properties               # ✅ COMMITTED - Dev settings, no secrets  
├── application-prod.properties              # ✅ COMMITTED - Prod settings, no secrets
├── application-local.properties.example     # ✅ COMMITTED - Template for developers
├── application-local.properties             # ❌ GITIGNORED - Your real secrets
└── application-secrets.properties           # ❌ GITIGNORED - Production testing secrets
```

This approach ensures **zero chance of accidentally committing secrets** while still providing a smooth developer experience and clear configuration structure.


You can copy this content and replace the entire `ENVIRONMENT_CONFIG.md` file. This updated version properly reflects the security-first approach where:

1. **No secrets in git** - Templates only contain placeholders
2. **Clear separation** - Actual secrets go in gitignored local files  
3. **Easy setup** - Copy example file and fill in real values
4. **Production ready** - Environment variables for deployment
5. **Secure by design** - Zero chance of accidental secret commits