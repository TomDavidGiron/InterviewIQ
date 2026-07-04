# InterviewIQ — AI Interview Coach

A backend-focused AI-powered technical interview simulator. Generates adaptive interview questions, evaluates answers using OpenAI, tracks weak skills over time, and produces personalised study plans.

Built as a portfolio project to demonstrate backend engineering, AI system architecture, and full-stack integration.

---

## Features

- **Adaptive interview engine** — 10-question sessions with difficulty that scales up/down based on your performance (consecutive pass/fail streaks)
- **AI evaluation** — answers scored by GPT-4o with strengths, missing concepts, and feedback; falls back to keyword scoring if no API key is set
- **RAG knowledge retrieval** — pgvector similarity search injects relevant context into the evaluation prompt
- **Skill graph** — per-browser persistent skill scores, exponentially weighted across sessions
- **AI feedback & study plan** — end-of-session diagnosis with a prioritised 3-step study plan
- **Job-specific interviews** — paste a job URL, description text, or screenshot; skills are extracted and questions are tailored to the role
- **OCR** — extract job descriptions from screenshots using Tesseract
- **Multi-strategy scraping** — Jsoup → Playwright → Selenium fallback chain for job posting URLs
- **800 questions** — backend, Java, algorithms, frontend, cloud, security, behavioural, and more; each tagged EASY / MEDIUM / HARD
- **Rate limiting** — 20 requests/minute per IP on answer submission endpoints

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5 |
| Database | PostgreSQL 15 + pgvector |
| Migrations | Flyway |
| AI | OpenAI API (`gpt-4o`, `text-embedding-3-small`) |
| Scraping | Jsoup, Playwright, Selenium |
| OCR | Tesseract (Tess4J) |
| Rate limiting | Bucket4j |
| Frontend | React 19, Material UI 7, Axios |

---

## Prerequisites

- Java 21
- Node.js 18+
- PostgreSQL 15 with the **pgvector** extension
- Maven wrapper included (`./mvnw`) — no global Maven install needed
- Tesseract OCR (optional, only needed for image upload)

---

## Quick Start (Docker)

The fastest way to run the full stack — no local Java, Maven, or PostgreSQL required:

```bash
# Optional: set your OpenAI key so AI features are enabled
export OPENAI_API_KEY=sk-...

docker compose up --build
```

- Frontend → **http://localhost:3000**
- Backend API → **http://localhost:8080**
- PostgreSQL → `localhost:5432` (user: `postgres`, db: `cvoptimizer`)

Flyway migrations and question seeding run automatically on first startup.

> **Note:** The frontend JS bundle is compiled with `REACT_APP_API_BASE_URL=http://localhost:8080` baked in. If you deploy to a server, rebuild the frontend image with `--build-arg REACT_APP_API_BASE_URL=https://api.yourdomain.com`.

---

## Quick Start (Local Dev)

### 1. Database setup

```sql
CREATE DATABASE cvoptimizer;
\c cvoptimizer
CREATE EXTENSION IF NOT EXISTS vector;
```

Flyway runs all migrations automatically on first startup.

### 2. Backend

Copy the example env file and fill in your values:

```bash
cp backend/.env.example backend/.env
```

Key variables (see `backend/.env.example` for the full list):

| Variable | Required | Default |
|---|---|---|
| `DB_URL` | Yes | `jdbc:postgresql://localhost:5432/cvoptimizer` |
| `DB_USERNAME` | Yes | `postgres` |
| `DB_PASSWORD` | Yes | `postgres` |
| `OPENAI_API_KEY` | No | *(blank — AI disabled, keyword scoring used)* |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:3000` |
| `OCR_TESSDATA_PATH` | No | *(auto-detected)* |

Spring Boot reads these from the environment automatically — export them or use a `.env` loader.

Start the backend:

```bash
cd backend
./mvnw spring-boot:run        # Mac / Linux
.\mvnw.cmd spring-boot:run    # Windows
```

The API starts on **http://localhost:8080**.

On first run, the question bank (800 questions) is seeded into the database. Subsequent startups skip the seed automatically.

### 3. Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm start
```

The app opens on **http://localhost:3000**.

---

## API Overview

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/interview/start` | Start a general interview session |
| `POST` | `/api/interview/job-specific` | Start a job-tailored session (URL / text / OCR) |
| `POST` | `/api/interview/{sessionId}/answer` | Submit an answer, get feedback + next question |
| `GET` | `/api/interview/{sessionId}/summary` | End-of-session summary, diagnosis, study plan |
| `GET` | `/api/interview/history?userId=&page=&size=` | Past sessions for a user |
| `GET` | `/api/interview/{sessionId}/details` | Per-question attempt breakdown |
| `GET` | `/api/interview/{sessionId}/skill-graph` | Skill scores for the session's user |
| `DELETE` | `/api/interview/users/{userId}/skill-graph` | Reset skill history |
| `GET` | `/api/interview/topics` | All available question topics |
| `POST` | `/api/image-upload` | Extract text from a job posting screenshot (OCR) |

A Postman collection is included in the `POSTMAN/` directory.

---

## Project Structure

```
backend/src/main/java/com/cvoptimizer/cv_backend/
├── interview/
│   ├── agent/          # Adaptive interview agent (rule-based difficulty scaling)
│   ├── ai/             # AI evaluation service + prompt templates
│   ├── feedback/       # End-of-session AI feedback & study plan
│   ├── rag/            # RAG pipeline (embeddings, pgvector, knowledge seeding)
│   ├── service/        # Core engine, evaluator, session store, skill graph
│   ├── controller/     # REST endpoints
│   └── persistence/    # JPA entities + repositories
├── scraper/            # Job posting scrapers (Jsoup, Playwright, Selenium)
├── service/            # OCR, job skill extractor
└── config/             # Security (CORS), rate limiting, RestTemplate

frontend/src/
├── pages/              # Home, Interview, Summary, History, SessionDetails
├── api/                # Axios API layer
└── utils/              # Guest ID generator (per-browser session isolation)
```

---

## How It Works

### Interview Flow

```
POST /start
  → select 10 questions (topic/job-filtered, difficulty-weighted)
  → return session ID + first question

POST /answer
  → evaluate answer (AI or keyword)
  → RAG context injected if API key is set
  → agent decides: next question / follow-up / increase/decrease difficulty / finish
  → return feedback + next question

GET /summary
  → AI generates diagnosis + study plan
  → skill graph updated (exponential weighted average)
  → return full results
```

### Adaptive Agent

The `RuleBasedInterviewAgentService` tracks:
- `consecutivePasses` / `consecutiveFails` — triggers difficulty change after 2 in a row
- `currentDifficulty` (1 EASY → 2 MEDIUM → 3 HARD) — filters question selection
- `followUpCount` — asks a targeted follow-up when a concept is missing, max once per question
- Early finish — triggers if score drops below 40% after question 7

### RAG Pipeline

```
Question text
  → embed with text-embedding-3-small
  → cosine similarity search in pgvector
  → top-k chunks injected into evaluation prompt
```

Skipped entirely when no API key is set (hash embeddings produce meaningless scores).

---

## Running Tests

```bash
cd backend
./mvnw test               # Mac / Linux
.\mvnw.cmd test           # Windows
```

Tests cover: adaptive agent logic, keyword evaluator, AI evaluation service (mocked HTTP), AI feedback service (mocked HTTP), RAG service, skill graph service, and interview flow integration.

---

## Notes

- Sessions survive backend restarts — the full session state is serialised as JSON into the `session_state` column on every answer submission and restored on cache miss.
- Guest users are identified by a UUID stored in `localStorage` — no login required.
- AI features degrade gracefully: if `OPENAI_API_KEY` is not set, evaluation falls back to keyword matching and feedback falls back to rule-based diagnosis. The app is fully usable without an API key.
