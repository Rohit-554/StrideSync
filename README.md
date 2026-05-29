# StrideSync

[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Ktor](https://img.shields.io/badge/Ktor-Server%20and%20Client-087CFA?logo=ktor&logoColor=white)](https://ktor.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Koin](https://img.shields.io/badge/Koin-DI-FF6F00?logo=kotlin&logoColor=white)](https://insert-koin.io/)
[![Room](https://img.shields.io/badge/Room-Database-3DDC84?logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)

A Strava-style fitness tracking app built with Kotlin Multiplatform. Record outdoor activities via GPS, upload them to a Ktor backend, and share them in a social feed where followers can give kudos and leave comments.

---

## Stack

| Layer | Technology |
|---|---|
| Shared UI | Compose Multiplatform (Android + iOS) |
| Navigation | Navigation3 |
| Local storage | Room 3.1 |
| Networking | Ktor Client |
| Dependency injection | Koin |
| Image loading | Coil 3 |
| Backend | Ktor 3 + Exposed + PostgreSQL 16 |
| Auth | JWT |
| Live tracking | WebSocket |
| Infrastructure | Docker Compose |

---

## Features

- JWT authentication (register, login, auto-logout on 401)
- GPS activity recording with live distance, pace, and route drawing
- Activity upload with offline-first caching and background sync
- Social feed with pagination and pull-to-refresh
- Kudos, comments, follow/unfollow
- Athlete search and follow suggestions
- Statuses (ephemeral posts)
- Runtime permissions (location, notifications) — platform-specific
- Live activity tracking over WebSocket

---

## Project Structure

```
StrideSync/
├── composeApp/          # Shared KMP code — UI, ViewModels, Room, Ktor client, GPS
├── androidApp/          # Android entry point — Koin setup, setContent
├── iosApp/              # iOS SwiftUI shell — hosts the KMP framework
└── server/              # Ktor backend — auth, activities, social, WebSocket
```

---

## Requirements

- JDK 17+
- Android Studio Hedgehog or later
- Xcode 15+ (for iOS builds, macOS only)
- Docker Desktop (for local PostgreSQL)

---

## Quick Start

### 1. Configure environment

```bash
cp .env.example .env
# Fill in DB credentials, JWT secret, and pgAdmin values
```

### 2. Start the backend

```bash
./scripts/start-server.sh
# Starts PostgreSQL via Docker, then runs the Ktor server
# Server: http://localhost:8080
# Health: GET http://localhost:8080/health
```

### 3. Run the Android app

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

### 4. Run the iOS app

Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

---

## Docker Compose

```bash
# Start everything (PostgreSQL + server + pgAdmin)
docker compose up -d

# Start only PostgreSQL (used by scripts/start-server.sh)
docker compose up -d postgres
```

Services:
- `postgres` — port `${POSTGRES_PORT:-5432}`
- `server` — port `8080`
- `pgadmin` — port `${PGADMIN_PORT:-5050}` (optional)

---

## Environment Variables

See `.env.example` for the full list. Key variables:

```
DB_URL, DB_USER, DB_PASSWORD
JWT_SECRET, JWT_ISSUER, JWT_AUDIENCE
POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_PORT
PGADMIN_EMAIL, PGADMIN_PASSWORD, PGADMIN_PORT
```

> The server reads from the process environment. The Gradle tasks `:server:run` and `:server:test` forward values from the root `.env` automatically.

---

## Useful Commands

```bash
# Backend
./gradlew :server:run
./gradlew :server:test

# Android
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Room — run after any @Entity or @Dao change
./gradlew :composeApp:kspCommonMainMetadata

# Full build and test
./scripts/build.sh
./scripts/test.sh
```

---

## License

See `LICENSE`.

---

Made with ❤️ by jadu
