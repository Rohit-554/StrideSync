# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.
Read this entire file before writing any code, running any command, or making any architectural decision.

---

## What is StrideSync?

StrideSync is a fitness tracking and social app built with Kotlin Multiplatform.
Users record outdoor activities like runs, rides, and hikes using GPS on their phone.
After finishing, the activity is uploaded to a Ktor backend and appears in the social feed of their followers.
Followers can give kudos and leave comments. Think Strava but built entirely in Kotlin.

## Project Goal

Build a full stack KMP app end to end using AI agents and Claude Code.
The Ktor server handles auth, activity storage, GPS data, social graph, and a live WebSocket tracking feed.
The KMP client handles GPS recording, offline caching with Room, and a shared Compose Multiplatform UI for Android and iOS.

---

## Modules

| Module | Purpose |
|---|---|
| `composeApp` | All shared KMP code including UI, ViewModels, Room, Ktor client, GPS, navigation |
| `androidApp` | Android entry point only. Sets up Koin and calls setContent. No business logic here. |
| `server` | Ktor 3 backend. Postgres via Exposed. JWT auth. WebSocket live tracking. Docker. |
| `iosApp` | iOS SwiftUI shell. Hosts the KMP framework. No business logic here. |

---

## Screens and What They Do

| Screen | Purpose |
|---|---|
| Splash | Check stored JWT token. Navigate to Feed if valid, Login if not. |
| Login | Email and password sign in. On success store token in AppPreferences. |
| Register | Create account with name, email, password. On success go to Feed. |
| Feed | Paginated list of activities from followed users. Kudos toggle. Pull to refresh. |
| Record (Idle) | Sport type picker. Live GPS map. Big orange start button. |
| Record (Active) | Live distance, duration, pace. Route drawing on map. Pause and stop controls. |
| Activity Complete | Full stats summary after stopping. Title input. Save or discard. |
| Activity Detail | Full map with route. Stats grid. Elevation chart. Kudos list. Comments. |
| Profile | Avatar, follower stats, weekly chart, recent activity list. |
| Explore | Search athletes by name. Follow suggestions. |

---

## Architecture

Clean layered architecture. All business logic lives in `composeApp/src/commonMain`.
Platform code only exists in `androidMain` and `iosMain` behind expect/actual interfaces.

```
Screen (Composable)
  └── ViewModel (StateFlow)
        └── Repository (interface in domain, impl in data)
              ├── Room DAO (local, offline-first)
              └── Ktor API Service (remote)
```

### Domain Layer — `commonMain/domain/`
- `model/` — Pure data classes. User, Activity, GpsPoint, FeedItem, Comment, SportType.
- `repository/` — Interfaces only. AuthRepository, ActivityRepository, FeedRepository, GpsRepository.

### Data Layer — `commonMain/data/`
- `local/` — Room 3.1 database, entities, DAOs, DatabaseFactory (expect/actual).
- `remote/dto/` — Serializable DTOs that map to server JSON.
- `remote/api/` — Ktor HTTP client service classes. AuthApi, ActivityApi, SocialApi, WebSocketApi.
- `remote/interceptor/` — AuthInterceptor adds JWT Bearer header to every request.
- `repository/` — Concrete implementations of domain repository interfaces.
- `preferences/` — AppPreferences stores JWT token and userId via multiplatform-settings.

### Presentation Layer — `commonMain/ui/`
- `theme/` — Material 3 Expressive. Primary color #FC4C02 (Strava orange). Dark background #111318.
- `components/` — ActivityCard, KudosButton, StatRow, RecordButton, SportTypeIcon, GpsSignalIndicator.
- `screens/` — auth/, feed/, record/, detail/, profile/, explore/.
- `viewmodel/` — AuthViewModel, FeedViewModel, RecordViewModel, ActivityDetailViewModel, ProfileViewModel.

### GPS — `commonMain/gps/` + platform actuals
- `GpsProvider.kt` — expect class with `observeLocation(): Flow<GpsPoint>` and `requestPermission()`.
- Android actual: FusedLocationProviderClient inside a foreground service.
- iOS actual: CLLocationManager with background location capability.

### Tracking Engine — `commonMain/tracking/`
- `TrackingEngine.kt` — Collects GPS flow, buffers points, computes live distance and pace.
- `DistanceCalculator.kt` — Haversine formula between consecutive GPS points.
- `PaceCalculator.kt` — Seconds per kilometer from distance and duration.

### Navigation — `commonMain/navigation/`
- `Screen.kt` — Sealed class. Login, Register, Feed, Record, ActivityDetail(id), Profile, Explore.
- `AppNavigation.kt` — Navigation3 with `rememberNavBackStack()` and Crossfade. No NavDisplay.

### DI — Koin
- `di/AppModule.kt` — All bindings: API services, DAOs, repositories, ViewModels.
- `di/PlatformModule.kt` — expect declaration. Platform actuals in androidMain and iosMain.

### Network — `commonMain/network/`
- `HttpClientFactory.kt` — Ktor 3 client with ContentNegotiation JSON, AuthInterceptor, logging.
- Base URL: `10.0.2.2:8080` for Android emulator, `localhost:8080` for iOS simulator.

---

## Server Architecture — `server/src/main/kotlin/`

```
Application.kt
config/          — DatabaseConfig, JwtConfig, WebSocketConfig
db/tables/       — Exposed table objects: Users, Activities, GpsPoints, Follows, Kudos, Comments
dto/             — Request and response data classes (Serializable)
repository/      — Database access using Exposed DSL and dbQuery wrapper
service/         — Business logic: AuthService, ActivityService, StatsService, FeedService
routes/          — Ktor routing: AuthRoutes, ActivityRoutes, FeedRoutes, SocialRoutes, TrackingRoutes
plugins/         — Serialization, Authentication, Routing, WebSockets
jobs/            — ActivityStatsJob: Haversine distance, elevation gain, pace computation
```

Server runs on port 8080. Docker Compose starts Postgres on port 5432.
Server main class: `io.jadu.stridesync.server.ApplicationKt`

---

## Build and Run Commands

```bash
# Android
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# iOS compile check
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Room KSP — run this after any @Entity or @Dao change
./gradlew :composeApp:kspCommonMainMetadata

# Server
./gradlew :server:run
./gradlew :server:compileKotlin

# Docker (Postgres + server)
docker-compose up -d
docker-compose up --build

# Tests
./gradlew :composeApp:test
./gradlew :server:test

# Full build
./gradlew build

# Clean
./gradlew clean
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode (requires macOS + Xcode 15+).

---

## Verification After Every Task

After completing any task, run both of these. Both must pass before committing.

```bash
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

If Room entities or DAOs changed, run KSP first:

```bash
./gradlew :composeApp:kspCommonMainMetadata
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

---

## Key Conventions

- All business logic goes in `commonMain`. Never put logic in `androidMain` or `iosMain`.
- Platform code only exists behind `expect`/`actual`. The expect is in `commonMain`, actuals in `androidMain` and `iosMain`.
- Navigation3 only. Use `rememberNavBackStack()` and `Crossfade`. Never use `NavDisplay`.
- Room 3.1 is the local database. Not SQLDelight. Run KSP after any entity or DAO change.
- All Koin bindings go in `AppModule.kt`. Platform-specific bindings go in `PlatformModule` actuals.
- DTOs live in `data/remote/dto/`. Domain models live in `domain/model/`. Room entities live in `data/local/entity/`. These are three separate layers. Never mix them.
- Map between layers with explicit extension functions: `dto.toDomain()`, `entity.toDomain()`, `domain.toEntity()`.
- No JVM-only libraries in `commonMain`. Everything must compile for both Android and iOS native.
- No `runBlocking` in production code. Use `suspend fun` for one-shot, `Flow` for streams.
- StateFlow for all UI state. Screens collect with `collectAsState()`.
- Wrap all API calls in `runCatching`. Never swallow exceptions silently.
- Dependencies are version-managed in `gradle/libs.versions.toml`. Never hardcode versions.
- Android: minSdk 24, targetSdk 36, Java 17.

---

## Android Specifics

- Foreground service required for GPS recording during workouts.
- Declare `<service android:foregroundServiceType="location">` in AndroidManifest.
- Use `10.0.2.2:8080` as the server base URL when running on an emulator.
- BuildConfig values only exist in `androidApp`. Use `AppConfig` object in `commonMain` for shared config.

## iOS Specifics

- Background location requires `location` capability in the app target.
- Add `NSLocationWhenInUseUsageDescription` and `NSLocationAlwaysAndWhenInUseUsageDescription` to Info.plist.
- Use `localhost:8080` as the server base URL when running on the iOS simulator.
- If a top-level Kotlin/Native call fails, try `ClassName.Companion.method()` instead.

---

## Optional AI Integration

Copy `local.properties.example` to `local.properties` and add API keys for Claude, Groq, or Gemini.
AI integration is available for future features like route suggestions but is not required for the MVP build.

---

## Agent Skills

Skills in `.claude/skills/` are auto-loaded by Claude Code.

| Skill | When to use |
|---|---|
| `bloom-build` | Adding a new screen end to end: composable, ViewModel, repository, Room, Koin wiring |
| `bloom-navigate` | Swapping providers, configuring notifications, removing features |
| `clean-code` | Code quality review on naming, functions, and error handling |
| `compose` | CMP patterns, Material 3, animation, navigation, state management |