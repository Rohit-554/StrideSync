# AGENTS.md — StrideSync (Strava-like Fitness App)

### PHASE 1 — Server Foundation

> **Checkpoint:** Ktor server starts, connects to Postgres, health endpoint returns 200.

#### Task 1.1 — Docker + Postgres setup
Create `docker-compose.yml` at project root with Postgres 16 and pgAdmin. Create `.env.example` with DB credentials.

**Files:** `docker-compose.yml`, `.env.example`
**Verify:** `docker-compose up -d` → Postgres accepting connections on 5432

#### Task 1.2 — Server Gradle dependencies
Add to `:server` `build.gradle.kts`: Ktor 3 (netty, content-negotiation, serialization, auth-jwt, websockets, cors), Exposed (core, dao, jdbc, java-time), PostgreSQL JDBC driver, bcrypt, logback.

**Files:** `server/build.gradle.kts`, `gradle/libs.versions.toml`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 1.3 — Database config + connection
Create `DatabaseConfig.kt` — reads DB_URL, DB_USER, DB_PASSWORD from environment. Initializes HikariCP connection pool. Create `DatabaseFactory.kt` with `init()` function that creates tables.

**Files:** `server/.../config/DatabaseConfig.kt`, `server/.../db/DatabaseFactory.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 1.4 — Exposed table objects (Users + Activities)
Create `UsersTable` and `ActivitiesTable` Exposed table objects with all columns from the schema section below.

**Files:** `server/.../db/tables/UsersTable.kt`, `server/.../db/tables/ActivitiesTable.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 1.5 — Exposed table objects (GpsPoints, Follows, Kudos, Comments)
Create remaining four table objects.

**Files:** `server/.../db/tables/GpsPointsTable.kt`, `server/.../db/tables/FollowsTable.kt`, `server/.../db/tables/KudosTable.kt`, `server/.../db/tables/CommentsTable.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 1.6 — Ktor Application + plugins + health route
Create `Application.kt` with Ktor module. Install plugins: ContentNegotiation (JSON), CORS, WebSockets. Add `GET /health` returning `{ "status": "ok" }`. Call `DatabaseFactory.init()` on startup.

**Files:** `server/.../Application.kt`, `server/.../plugins/Serialization.kt`, `server/.../plugins/Routing.kt`
**Verify:** `./gradlew :server:run` → `curl localhost:8080/health` returns 200

---

### PHASE 2 — Server Auth

> **Checkpoint:** Register a user via curl, login, get a JWT token back.

#### Task 2.1 — Auth DTOs
Create `AuthDtos.kt` with: `RegisterRequest(email, displayName, password)`, `LoginRequest(email, password)`, `AuthResponse(token, user)`, `UserResponse(id, displayName, email, avatarUrl)`. All `@Serializable`.

**Files:** `server/.../dto/AuthDtos.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 2.2 — JWT config
Create `JwtConfig.kt` reading JWT_SECRET from env. Configure Ktor JWT authentication plugin with issuer, audience, realm, and token validation.

**Files:** `server/.../config/JwtConfig.kt`, `server/.../plugins/Authentication.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 2.3 — User repository
Create `UserRepository.kt` with: `createUser(email, displayName, passwordHash) → UUID`, `findByEmail(email) → UserRow?`, `findById(id) → UserRow?`. All suspend + `dbQuery {}` wrapper.

**Files:** `server/.../repository/UserRepository.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 2.4 — Auth service
Create `AuthService.kt` with: `register(request) → AuthResponse` (hash password with bcrypt, create user, issue JWT), `login(request) → AuthResponse` (verify password, issue JWT). Throw proper exceptions for duplicate email, wrong password.

**Files:** `server/.../service/AuthService.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 2.5 — Auth routes
Create `AuthRoutes.kt` with: `POST /auth/register`, `POST /auth/login`. Wire into main routing. No auth required on these routes.

**Files:** `server/.../routes/AuthRoutes.kt`
**Verify:** `./gradlew :server:run` → curl POST register + login both return JWT

---

### PHASE 3 — Server Activity CRUD

> **Checkpoint:** Authenticated user can create an activity with GPS points and retrieve it.

#### Task 3.1 — Activity DTOs
Create `ActivityDtos.kt`: `CreateActivityRequest(sportType, title, gpsPoints[], startedAt)`, `GpsPointDto(lat, lng, altitude?, speed?, timestamp)`, `ActivityResponse(id, userId, sportType, title, distanceM, durationSec, elevationM, avgPace, polyline, startedAt, createdAt)`.

**Files:** `server/.../dto/ActivityDtos.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 3.2 — Activity repository
Create `ActivityRepository.kt`: `create(userId, request) → UUID`, `findById(id) → ActivityRow?`, `findByUser(userId, page, size) → List<ActivityRow>`, `delete(id, userId) → Boolean`. Include `GpsPointRepository` for batch-inserting GPS points.

**Files:** `server/.../repository/ActivityRepository.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 3.3 — Activity service + stats computation
Create `ActivityService.kt`: on create, batch-insert GPS points, then compute distance (Haversine sum), duration, elevation gain, avg pace. Update activity row with computed stats. Encode GPS points into polyline string.

**Files:** `server/.../service/ActivityService.kt`, `server/.../service/StatsService.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 3.4 — Activity routes
Create `ActivityRoutes.kt`: `POST /activities` (authenticated), `GET /activities/{id}`, `GET /activities?page&size` (own), `DELETE /activities/{id}`. Extract userId from JWT principal.

**Files:** `server/.../routes/ActivityRoutes.kt`
**Verify:** `./gradlew :server:run` → curl create + get activity works with token

---

### PHASE 4 — Server Social + Feed

> **Checkpoint:** User A follows User B. User B posts activity. User A sees it in feed.

#### Task 4.1 — Social DTOs
Create `SocialDtos.kt`: `FeedItemResponse(activity, user, kudosCount, commentCount, hasKudosed)`, `CommentResponse(id, userId, displayName, text, createdAt)`, `UserProfileResponse(id, displayName, avatarUrl, activityCount, followerCount, followingCount)`.

**Files:** `server/.../dto/SocialDtos.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 4.2 — Social repository
Create `SocialRepository.kt`: `follow(followerId, followeeId)`, `unfollow(...)`, `addKudos(activityId, userId)`, `removeKudos(...)`, `addComment(activityId, userId, text)`, `getComments(activityId)`, `hasKudosed(activityId, userId)`.

**Files:** `server/.../repository/SocialRepository.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 4.3 — Feed repository
Create `FeedRepository.kt`: `getFeed(userId, page, size) → List<FeedItem>` — join Activities + Users where activity.userId is in the set of users the current user follows. Include kudos count, comment count, and whether current user has kudosed.

**Files:** `server/.../repository/FeedRepository.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 4.4 — Social + Feed routes
Create `SocialRoutes.kt`: `POST/DELETE /users/{id}/follow`, `POST/DELETE /activities/{id}/kudos`, `POST /activities/{id}/comments`, `GET /users/{id}`. Create `FeedRoutes.kt`: `GET /feed?page&size`.

**Files:** `server/.../routes/SocialRoutes.kt`, `server/.../routes/FeedRoutes.kt`
**Verify:** `./gradlew :server:run` → full curl flow: register 2 users, follow, post activity, get feed

---

### PHASE 5 — Server WebSocket + Docker

> **Checkpoint:** WebSocket accepts GPS points. Server runs in Docker alongside Postgres.

#### Task 5.1 — WebSocket GPS tracking route
Create `TrackingRoutes.kt`: authenticated WebSocket at `/ws/track`. Accepts JSON GPS points, buffers in-memory, batch-inserts to DB every 5 seconds. On connection close, finalize the activity.

**Files:** `server/.../routes/TrackingRoutes.kt`, `server/.../plugins/WebSockets.kt`
**Verify:** `./gradlew :server:compileKotlin` passes

#### Task 5.2 — Server Dockerfile
Create `Dockerfile` for the server: multi-stage build (Gradle build → slim JRE runtime). Create `server/Dockerfile` and update `docker-compose.yml` to include the server service.

**Files:** `server/Dockerfile`, update `docker-compose.yml`
**Verify:** `docker-compose up --build` → health endpoint reachable → server logs show DB connected

---

### PHASE 6 — Client Domain + Data Models

> **Checkpoint:** Client compiles on both Android and iOS with new domain models and Room entities.

#### Task 6.1 — Domain models
Create in `commonMain`: `domain/model/User.kt`, `Activity.kt`, `GpsPoint.kt`, `SportType.kt` (enum: Run, Ride, Hike, Swim, Walk, Other), `FeedItem.kt`, `Comment.kt`. Pure data classes, no framework dependencies.

**Files:** `composeApp/.../domain/model/*.kt`
**Verify:** Android + iOS compile pass

#### Task 6.2 — Room entities
Create in `commonMain`: `data/local/entity/ActivityEntity.kt`, `GpsPointEntity.kt`, `CachedFeedEntity.kt`, `PendingUploadEntity.kt`. With `@Entity`, `@PrimaryKey`, proper column annotations. Run KSP.

**Files:** `composeApp/.../data/local/entity/*.kt`
**Verify:** `./gradlew :composeApp:kspCommonMainMetadata` + Android + iOS compile pass

#### Task 6.3 — Room DAOs
Create in `commonMain`: `data/local/dao/ActivityDao.kt` (insert, getById, getAll, delete, getUnsynced), `GpsPointDao.kt` (batchInsert, getByActivityId), `FeedDao.kt` (insertAll, getPage, clearOlderThan). All return `Flow` for observable queries.

**Files:** `composeApp/.../data/local/dao/*.kt`
**Verify:** KSP + Android + iOS compile pass

#### Task 6.4 — Extend AppDatabase
Add new entities and DAOs to the existing Room `AppDatabase.kt`. Register the new entities in the `@Database` annotation. Add abstract DAO accessor functions.

**Files:** Update `composeApp/.../data/local/AppDatabase.kt`
**Verify:** KSP + Android + iOS compile pass

---

### PHASE 7 — Client Networking

> **Checkpoint:** Client can call auth endpoints and receive a JWT token. Compiles on both platforms.

#### Task 7.1 — Network DTOs
Create in `commonMain`: `data/remote/dto/AuthDto.kt` (RegisterRequest, LoginRequest, AuthResponse, UserResponse), `ActivityDto.kt`, `FeedDto.kt`, `GpsPointDto.kt`. All `@Serializable`. Match server DTO field names exactly.

**Files:** `composeApp/.../data/remote/dto/*.kt`
**Verify:** Android + iOS compile pass

#### Task 7.2 — Auth interceptor + token storage
Create `AuthInterceptor.kt` — Ktor HttpClient plugin that reads JWT from `AppPreferences` and adds `Authorization: Bearer` header. Create/extend `AppPreferences` to store/retrieve/clear JWT token.

**Files:** `composeApp/.../data/remote/interceptor/AuthInterceptor.kt`, update `data/preferences/AppPreferences.kt`
**Verify:** Android + iOS compile pass

#### Task 7.3 — Extend HttpClientFactory with auth
Update existing `HttpClientFactory.kt` to install the auth interceptor, configure base URL (read from `AppConfig`), add content negotiation with JSON, add logging in debug.

**Files:** Update `composeApp/.../network/HttpClientFactory.kt`
**Verify:** Android + iOS compile pass

#### Task 7.4 — Auth API service
Create `data/remote/api/AuthApi.kt`: `suspend fun register(request): AuthResponse`, `suspend fun login(request): AuthResponse`. Use Ktor client.

**Files:** `composeApp/.../data/remote/api/AuthApi.kt`
**Verify:** Android + iOS compile pass

#### Task 7.5 — Activity API service
Create `data/remote/api/ActivityApi.kt`: `suspend fun create(request): ActivityResponse`, `suspend fun getById(id): ActivityResponse`, `suspend fun getFeed(page, size): List<FeedItemResponse>`, `suspend fun delete(id)`.

**Files:** `composeApp/.../data/remote/api/ActivityApi.kt`
**Verify:** Android + iOS compile pass

#### Task 7.6 — Social API service
Create `data/remote/api/SocialApi.kt`: `suspend fun follow(userId)`, `suspend fun unfollow(userId)`, `suspend fun kudos(activityId)`, `suspend fun removeKudos(activityId)`, `suspend fun addComment(activityId, text)`, `suspend fun getUser(userId): UserProfileResponse`.

**Files:** `composeApp/.../data/remote/api/SocialApi.kt`
**Verify:** Android + iOS compile pass

---

### PHASE 8 — Client Repositories + DI

> **Checkpoint:** Repository layer wired. Koin can resolve all dependencies. Both platforms compile.

#### Task 8.1 — Repository interfaces
Create in `domain/repository/`: `AuthRepository.kt`, `ActivityRepository.kt`, `FeedRepository.kt`, `GpsRepository.kt`. Define suspend functions and Flow returns. No implementation details.

**Files:** `composeApp/.../domain/repository/*.kt`
**Verify:** Android + iOS compile pass

#### Task 8.2 — Auth repository implementation
Create `AuthRepositoryImpl.kt`: calls `AuthApi`, stores JWT in `AppPreferences`, maps DTOs to domain User model.

**Files:** `composeApp/.../data/repository/AuthRepositoryImpl.kt`
**Verify:** Android + iOS compile pass

#### Task 8.3 — Activity repository implementation
Create `ActivityRepositoryImpl.kt`: offline-first — save to Room on create, sync to server. On fetch, try server first, cache in Room, fallback to Room on failure. Manage `PendingUploadEntity` for unsynced activities.

**Files:** `composeApp/.../data/repository/ActivityRepositoryImpl.kt`
**Verify:** Android + iOS compile pass

#### Task 8.4 — Feed repository implementation
Create `FeedRepositoryImpl.kt`: fetch from `FeedApi`, cache in `CachedFeedEntity`, return `Flow` from Room so UI updates reactively. Support pull-to-refresh (clear + re-fetch).

**Files:** `composeApp/.../data/repository/FeedRepositoryImpl.kt`
**Verify:** Android + iOS compile pass

#### Task 8.5 — Register all new Koin bindings
Update `AppModule.kt`: register all new API services, repositories (bind interface to impl), ViewModels. Update `PlatformModule` files if any platform-specific bindings are needed.

**Files:** Update `composeApp/.../di/AppModule.kt`
**Verify:** Android + iOS compile pass

---

### PHASE 9 — Auth UI

> **Checkpoint:** User can see login screen, type credentials, tap login. App compiles and renders on both platforms.

#### Task 9.1 — Auth ViewModel
Create `AuthViewModel.kt` in `ui/viewmodel/`: `UiState` sealed class (Idle, Loading, Success, Error). Functions: `login(email, password)`, `register(email, displayName, password)`. Expose `stateFlow`. Check stored token on init → auto-navigate if valid.

**Files:** `composeApp/.../ui/viewmodel/AuthViewModel.kt`
**Verify:** Android + iOS compile pass

#### Task 9.2 — Login screen
Create `LoginScreen.kt`: email field, password field, login button, "Don't have an account? Register" link. Collect AuthViewModel state. Show loading indicator, error snackbar. M3 Expressive styling with Strava orange.

**Files:** `composeApp/.../ui/screens/auth/LoginScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 9.3 — Register screen
Create `RegisterScreen.kt`: display name, email, password, confirm password fields. Register button. Navigate to Feed on success.

**Files:** `composeApp/.../ui/screens/auth/RegisterScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 9.4 — Add auth screens to navigation
Add `Screen.Login` and `Screen.Register` to `Screen.kt`. Add composable entries in `AppNavigation.kt`. Set Login as the start destination when no token is stored. Route to Feed on successful auth.

**Files:** Update `navigation/Screen.kt`, `navigation/AppNavigation.kt`
**Verify:** Android + iOS compile pass — app launches to login screen

---

### PHASE 10 — Feed UI

> **Checkpoint:** Feed screen shows list of activity cards with kudos button. Pull-to-refresh works.

#### Task 10.1 — Feed ViewModel
Create `FeedViewModel.kt`: loads feed from repository on init. Exposes `StateFlow<FeedUiState>` with list of `FeedItem`. Functions: `refresh()`, `toggleKudos(activityId)`. Pagination support with `loadMore()`.

**Files:** `composeApp/.../ui/viewmodel/FeedViewModel.kt`
**Verify:** Android + iOS compile pass

#### Task 10.2 — ActivityCard component
Create `ui/components/ActivityCard.kt`: Card with user avatar (initials circle fallback), display name, sport type icon, stat row (distance km, duration HH:mm:ss, pace min/km), kudos heart button with count, comment icon with count. M3 card with Strava orange accents.

**Files:** `composeApp/.../ui/components/ActivityCard.kt`
**Verify:** Android + iOS compile pass

#### Task 10.3 — StatRow + KudosButton + SportTypeIcon components
Create small reusable composables: `StatRow.kt` (three stats in a row), `KudosButton.kt` (heart toggle with count, optimistic UI), `SportTypeIcon.kt` (icon per sport type enum).

**Files:** `composeApp/.../ui/components/StatRow.kt`, `KudosButton.kt`, `SportTypeIcon.kt`
**Verify:** Android + iOS compile pass

#### Task 10.4 — FeedScreen
Create `FeedScreen.kt`: `LazyColumn` of `ActivityCard` items. Pull-to-refresh via `PullToRefreshBox`. Empty state when no activities. Loading skeleton. Error state with retry. FAB to navigate to Record screen.

**Files:** `composeApp/.../ui/screens/feed/FeedScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 10.5 — Add Feed to navigation + bottom nav
Add `Screen.Feed` to navigation. Create bottom navigation bar with three tabs: Feed, Record, Profile. Feed is default after login.

**Files:** Update `navigation/Screen.kt`, `AppNavigation.kt`, `App.kt`
**Verify:** Android + iOS compile pass — app shows bottom nav with Feed tab selected

---

### PHASE 11 — GPS Recording Engine

> **Checkpoint:** GPS expect/actual compiles on both platforms. Tracking engine buffers points in memory.

#### Task 11.1 — GpsPoint data class + expect GpsProvider
Create `gps/GpsPoint.kt` (lat, lng, altitude?, speed?, timestamp) and `gps/GpsProvider.kt` as expect class with `observeLocation(): Flow<GpsPoint>`, `requestPermission(): Boolean`, `stopTracking()`.

**Files:** `composeApp/.../gps/GpsPoint.kt`, `composeApp/.../gps/GpsProvider.kt`
**Verify:** Android + iOS compile pass (expect without actual will fail — create empty actuals in next tasks)

#### Task 11.2 — Android actual GpsProvider
Implement using `FusedLocationProviderClient`. Priority HIGH_ACCURACY, interval 1s, fastest 500ms. Emit `GpsPoint` via `callbackFlow`. Handle permission check.

**Files:** `composeApp/src/androidMain/.../gps/GpsProvider.android.kt`
**Verify:** Android compile pass

#### Task 11.3 — iOS actual GpsProvider
Implement using `CLLocationManager`. Accuracy `kCLLocationAccuracyBest`, distance filter 5m. Emit via `callbackFlow` wrapping `CLLocationManagerDelegate`. Handle permission.

**Files:** `composeApp/src/iosMain/.../gps/GpsProvider.ios.kt`
**Verify:** iOS compile pass

#### Task 11.4 — TrackingEngine
Create `tracking/TrackingEngine.kt` in commonMain. Manages recording state (Idle, Recording, Paused). Collects from `GpsProvider`, buffers points in a list, tracks elapsed time, computes live distance (Haversine), live pace.

**Files:** `composeApp/.../tracking/TrackingEngine.kt`
**Verify:** Android + iOS compile pass

#### Task 11.5 — DistanceCalculator + PaceCalculator
Create `tracking/DistanceCalculator.kt` (Haversine formula between two GpsPoints, and cumulative distance for a list). Create `tracking/PaceCalculator.kt` (seconds per kilometer from distance + duration).

**Files:** `composeApp/.../tracking/DistanceCalculator.kt`, `PaceCalculator.kt`
**Verify:** Android + iOS compile pass

#### Task 11.6 — Android foreground service
Create `tracking/TrackingService.kt` in androidMain. Foreground service with persistent notification showing "Recording activity...". Holds `TrackingEngine` instance. Declare in AndroidManifest with `foregroundServiceType="location"`.

**Files:** `composeApp/src/androidMain/.../tracking/TrackingService.kt`, update `androidApp/.../AndroidManifest.xml`
**Verify:** Android compile pass. iOS already passes from 11.5.

---

### PHASE 12 — Record Screen UI

> **Checkpoint:** Record screen shows live stats. Start/stop/pause buttons work. GPS points collected.

#### Task 12.1 — RecordViewModel
Create `RecordViewModel.kt`: exposes recording state, elapsed time, live distance, live pace, GPS signal quality. Functions: `startRecording()`, `pauseRecording()`, `resumeRecording()`, `stopAndSave()`. On stop — create activity in repository.

**Files:** `composeApp/.../ui/viewmodel/RecordViewModel.kt`
**Verify:** Android + iOS compile pass

#### Task 12.2 — RecordButton component
Create `ui/components/RecordButton.kt`: large circular button that changes appearance based on state. Idle → green "Start". Recording → red "Stop" with pulsing animation. Paused → yellow "Resume". Use M3 Expressive shapes.

**Files:** `composeApp/.../ui/components/RecordButton.kt`
**Verify:** Android + iOS compile pass

#### Task 12.3 — GpsSignalIndicator component
Create `ui/components/GpsSignalIndicator.kt`: shows GPS accuracy as bars (strong/weak/none). Small composable for the top of RecordScreen.

**Files:** `composeApp/.../ui/components/GpsSignalIndicator.kt`
**Verify:** Android + iOS compile pass

#### Task 12.4 — RecordScreen
Create `RecordScreen.kt`: top — GpsSignalIndicator + sport type selector. Center — large live stats (distance, time, pace in big text). Bottom — RecordButton + pause button. Collect RecordViewModel state.

**Files:** `composeApp/.../ui/screens/record/RecordScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 12.5 — Wire Record into navigation
Add `Screen.Record` to navigation. Bottom nav Record tab navigates here. Back press during recording shows "Discard activity?" confirmation dialog.

**Files:** Update `navigation/Screen.kt`, `AppNavigation.kt`
**Verify:** Android + iOS compile pass — can navigate to Record tab

---

### PHASE 13 — Activity Detail + Profile

> **Checkpoint:** Tapping an activity card opens detail view. Profile screen shows user stats.

#### Task 13.1 — ActivityDetailViewModel
Create `ActivityDetailViewModel.kt`: loads activity + comments from repository by ID. Functions: `toggleKudos()`, `addComment(text)`. Exposes activity state, comments list.

**Files:** `composeApp/.../ui/viewmodel/ActivityDetailViewModel.kt`
**Verify:** Android + iOS compile pass

#### Task 13.2 — ActivityDetailScreen
Create `ActivityDetailScreen.kt`: header with sport icon + title. Stat cards grid (distance, duration, pace, elevation). Map placeholder (gray rounded rect for now — map comes in Phase 14). Comments list with input field at bottom. Kudos button.

**Files:** `composeApp/.../ui/screens/detail/ActivityDetailScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 13.3 — ProfileViewModel
Create `ProfileViewModel.kt`: loads current user profile, activity count, follower/following counts. Loads recent activity list.

**Files:** `composeApp/.../ui/viewmodel/ProfileViewModel.kt`
**Verify:** Android + iOS compile pass

#### Task 13.4 — ProfileScreen
Create `ProfileScreen.kt`: avatar + name header. Stats row (activities, followers, following). Recent activities list (reuse ActivityCard). Logout button at bottom.

**Files:** `composeApp/.../ui/screens/profile/ProfileScreen.kt`
**Verify:** Android + iOS compile pass

#### Task 13.5 — Wire detail + profile into navigation
Add `Screen.ActivityDetail(id)` and `Screen.Profile` to navigation. ActivityCard onClick → navigates to detail. Bottom nav Profile tab → profile screen.

**Files:** Update `navigation/Screen.kt`, `AppNavigation.kt`
**Verify:** Android + iOS compile pass — full navigation flow works

---

### PHASE 14 — Platform Map View

> **Checkpoint:** Activity detail shows a real map with the recorded route polyline.

#### Task 14.1 — expect PlatformMapView composable
Create `map/PlatformMapView.kt` in commonMain: `@Composable expect fun PlatformMapView(gpsPoints: List<GpsPoint>, modifier: Modifier)`.

**Files:** `composeApp/.../map/PlatformMapView.kt`
**Verify:** Compile will fail until actuals exist — that's expected. Move to 14.2 + 14.3.

#### Task 14.2 — Android actual MapView
Implement using Google Maps Compose SDK. Add dependency. Draw `Polyline` from GPS points. Fit camera bounds to the route. Show start/end markers.

**Files:** `composeApp/src/androidMain/.../map/PlatformMapView.android.kt`, update `build.gradle.kts`
**Verify:** Android compile pass

#### Task 14.3 — iOS actual MapView
Implement using `MKMapView` via `UIKitView` interop. Add `MKPolyline` overlay. Set visible map region to fit route.

**Files:** `composeApp/src/iosMain/.../map/PlatformMapView.ios.kt`
**Verify:** iOS compile pass

#### Task 14.4 — Replace map placeholder in ActivityDetailScreen
Replace the gray rect placeholder in `ActivityDetailScreen` with the real `PlatformMapView` composable.

**Files:** Update `ui/screens/detail/ActivityDetailScreen.kt`
**Verify:** Android + iOS compile pass

---

### PHASE 15 — WebSocket Live Tracking + Polish

> **Checkpoint:** Live GPS points stream to server via WebSocket during recording. Full end-to-end flow works.

#### Task 15.1 — WebSocket client service
Create `data/remote/api/WebSocketApi.kt` in commonMain: `suspend fun connectTracking(onPoint: (GpsPoint) -> Unit)`, `suspend fun sendPoint(point: GpsPoint)`, `fun disconnect()`. Use Ktor WebSocket client with auth token.

**Files:** `composeApp/.../data/remote/api/WebSocketApi.kt`
**Verify:** Android + iOS compile pass

#### Task 15.2 — Wire WebSocket into TrackingEngine
Update `TrackingEngine` to send GPS points to `WebSocketApi` during recording. Buffer locally if disconnected. Flush buffer on reconnect.

**Files:** Update `composeApp/.../tracking/TrackingEngine.kt`
**Verify:** Android + iOS compile pass

#### Task 15.3 — Strava orange theme finalization
Create/update `StrideSyncTheme.kt`: seed color `#FC4C02`, configure light + dark color schemes with M3 Expressive. Apply theme in `App.kt`. Ensure all screens use theme colors consistently.

**Files:** `composeApp/.../ui/theme/StrideSyncTheme.kt`, update `App.kt`
**Verify:** Android + iOS compile pass — app shows Strava orange theme

#### Task 15.4 — Error handling pass
Add proper error handling across all screens: network error snackbars, retry buttons, loading states. Wrap all API calls in `runCatching`. Show user-friendly messages.

**Files:** Update various screens and ViewModels
**Verify:** Android + iOS compile pass

---

## Server Database Schema Reference

```
UsersTable
  id          UUID  PK  default(generateUUID)
  email       VARCHAR(255)  UNIQUE
  displayName VARCHAR(100)
  passwordHash VARCHAR(255)
  avatarUrl   VARCHAR(500)  NULLABLE
  createdAt   TIMESTAMP  default(now)

ActivitiesTable
  id           UUID  PK
  userId       UUID  FK → UsersTable
  sportType    VARCHAR(20)
  title        VARCHAR(200)
  distanceM    DOUBLE
  durationSec  INT
  elevationM   DOUBLE
  avgPace      DOUBLE  NULLABLE
  polyline     TEXT
  startedAt    TIMESTAMP
  createdAt    TIMESTAMP

GpsPointsTable
  id          LONG  PK  AUTO
  activityId  UUID  FK → ActivitiesTable  (indexed)
  lat         DOUBLE
  lng         DOUBLE
  altitude    DOUBLE  NULLABLE
  speed       DOUBLE  NULLABLE
  timestamp   LONG

FollowsTable
  followerId  UUID  FK → UsersTable
  followeeId  UUID  FK → UsersTable
  createdAt   TIMESTAMP
  PK(followerId, followeeId)

KudosTable
  activityId  UUID  FK → ActivitiesTable
  userId      UUID  FK → UsersTable
  PK(activityId, userId)

CommentsTable
  id          UUID  PK
  activityId  UUID  FK → ActivitiesTable  (indexed)
  userId      UUID  FK → UsersTable
  text        VARCHAR(500)
  createdAt   TIMESTAMP
```

---

## API Endpoints Reference

```
POST   /auth/register             → { token, user }
POST   /auth/login                → { token, user }
POST   /activities                → { activity }           (auth)
GET    /activities/{id}           → { activity, points, kudos, comments }
GET    /activities?page&size      → { activities[] }       (auth, own)
DELETE /activities/{id}           → 204                    (auth)
GET    /feed?page&size            → { feedItems[] }        (auth)
POST   /users/{id}/follow        → 201                    (auth)
DELETE /users/{id}/follow        → 204                    (auth)
POST   /activities/{id}/kudos    → 201                    (auth)
DELETE /activities/{id}/kudos    → 204                    (auth)
POST   /activities/{id}/comments → { comment }            (auth)
GET    /users/{id}               → { user, stats }
GET    /health                   → { status: "ok" }
WS     /ws/track                 ← GPS points stream      (auth)
```

---

## Build Commands

```bash
# Client — run after EVERY task
./gradlew :androidApp:assembleDebug                         # Android
./gradlew :composeApp:compileKotlinIosSimulatorArm64         # iOS
./gradlew :composeApp:kspCommonMainMetadata                  # Room regen (after entity/DAO changes)

# Server
./gradlew :server:run                                        # Dev server
./gradlew :server:compileKotlin                              # Server compile check
docker-compose up -d                                         # Postgres
docker-compose up --build                                    # Postgres + server

# Full
bash scripts/build.sh                                        # Android + iOS
bash scripts/test.sh                                         # All tests
```

---

## Critical Notes

1. **Navigation3:** `rememberNavBackStack()` + `Crossfade`. Never `NavDisplay`.
2. **Room KSP:** Run `./gradlew :composeApp:kspCommonMainMetadata` after Entity/DAO changes.
3. **No JVM-only libs in commonMain.** Wrap behind expect/actual.
4. **Foreground Service:** Declare `<service android:foregroundServiceType="location">` in AndroidManifest.
5. **Server/Client DTOs are independent.** Same field names, separate classes.
6. **iOS K/N:** Use `ClassName.Companion.method()` if top-level ObjC call fails.
7. **Base URL:** Android emulator uses `10.0.2.2:8080`, iOS sim uses `localhost:8080`.
8. **Polyline:** Use Google encoded polyline algorithm for GPS compression.
9. **BuildConfig:** Only in `:androidApp`. Use `AppConfig` object in commonMain.
10. **AI keys:** Set on `AppConfig` in `MainActivity.onCreate` before `startKoin`.

---

## Coding Conventions

- Kotlin idioms only. `data class`, `sealed class`, `when`, extension functions.
- `suspend fun` for one-shot, `Flow` for streams. Never `runBlocking` in prod.
- `StateFlow` for UI state. Collect with `collectAsState()`.
- Named arguments when 3+ parameters.
- No magic strings — constants for endpoints, table names, preference keys.
- Wrap API calls in `runCatching`. Never swallow exceptions.
- Explicit mappers: `dto.toDomain()`, `entity.toDomain()`, `domain.toEntity()`.

---

## Agent Skills

| Skill              | When to Use                                                    |
|--------------------|----------------------------------------------------------------|
| `bloom-build`      | Add a new screen end-to-end (composable → VM → repo → Room → Koin) |
| `bloom-navigate`   | Swap AI provider, configure notifications, remove features     |
| `clean-code`       | Code quality — naming, functions, error handling               |
| `compose`          | CMP patterns, M3, animation, navigation, state management     |

---

*Last updated: May 2026 — StrideSync on Catylst v1.0*