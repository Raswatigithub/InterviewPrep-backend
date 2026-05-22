# InterviewPrep Backend

Production-ready Spring Boot backend for InterviewPrep.

## Stack

- Java 17
- Spring Boot 3
- Spring Security
- MongoDB
- JWT authentication
- Secure Gemini proxy integration

## Features

- `/api/auth` login and register APIs
- `/api/ai/generate` secure Gemini proxy
- request validation
- input sanitization
- CORS configuration
- request logging with request IDs
- in-memory rate limiting
- standardized success and error responses
- graceful shutdown
- compression
- health endpoint at `/health` and `/api/health`

## Local setup

Requirements:

- Java 17+
- Maven
- MongoDB running locally or a hosted MongoDB URI

Copy the environment template:

```bash
cp .env.example .env
```

Set environment variables in your shell or IDE run configuration.

Run locally:

```bash
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:5000
```

## Endpoints

```text
GET  /health
GET  /api/health
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
POST /api/ai/generate
```

## Example health response

```json
{
  "success": true,
  "message": "Backend is healthy.",
  "data": {
    "status": "ok"
  },
  "timestamp": "2026-05-21T00:00:00Z"
}
```

## Production env vars

See [backend/.env.example](/C:/Users/admin/Documents/Practics-website/backend/.env.example).

Important values:

- `MONGODB_URI`
- `JWT_SECRET`
- `GEMINI_API_KEY`
- `GEMINI_MODEL`
- `ALLOWED_ORIGINS`

## Render deployment

This repo includes:

- [render.yaml](/C:/Users/admin/Documents/Practics-website/render.yaml)
- [.github/workflows/backend-deploy.yml](/C:/Users/admin/Documents/Practics-website/.github/workflows/backend-deploy.yml)

Render is the recommended backend host for this project because it offers:

- low-cost deployment
- easy GitHub integration
- env var management
- health checks
- deploy history and manual rollback

## GitHub Actions secrets

Required GitHub secrets:

- `MONGODB_URI`
- `JWT_SECRET`
- `GEMINI_API_KEY`
- `RENDER_DEPLOY_HOOK_URL`

Recommended GitHub variables:

- `GEMINI_MODEL`
- `ALLOWED_ORIGINS`
- `BACKEND_HEALTHCHECK_URL`
