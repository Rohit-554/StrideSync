# GEMINI.md

This file provides strict rules and project guidelines for Gemini and other AI agents working on this repository.

---

## 🚨 CRITICAL RULE: NO ABSOLUTE USER PATHS

**Agents must NEVER print or show absolute local paths** (e.g., paths starting with `/Users/rohitx/` or `/Users/rohitx/All/Projects/Experimental/StrideSync/`) in the chat UI, terminal outputs, tool calls, or logs. 

- **Always use relative paths** starting from the project root (e.g., `composeApp/src/commonMain/...` instead of `/Users/rohitx/...`).
- If you need to print a directory path, file path, or run a command, format it relative to the repository root.
- Keep absolute path details hidden from the user interface and terminal strictly.

---

## Project Structure

StrideSync is a Kotlin Multiplatform fitness tracking and social application structured as follows:

```
StrideSync/ (Root Directory)
├── composeApp/                 # Shared Kotlin Multiplatform client code
│   └── src/
│       ├── commonMain/         # All shared client logic and UI (Compose Multiplatform)
│       │   └── kotlin/io/jadu/strideSync/
│       │       ├── data/       # Data layer: repository impl, local DB (Room), remote APIs (Ktor)
│       │       ├── di/         # Dependency injection (Koin modules)
│       │       ├── domain/     # Domain layer: repository interfaces, pure models
│       │       ├── gps/        # GPS tracking provider interfaces (expect declarations)
│       │       ├── navigation/ # Navigation setup using Navigation3 and Crossfade
│       │       ├── tracking/   # GPS tracking engine, pace & distance calculations
│       │       └── ui/         # Presentation layer: ViewModels, components, and Screens
│       ├── androidMain/        # Android-specific actual implementations (e.g. Foreground Service)
│       ├── iosMain/            # iOS-specific actual implementations (e.g. CLLocationManager integration)
│       └── desktopMain/        # Desktop-specific actual implementations and preview configs
├── androidApp/                 # Android App target / entry point wrapper
├── iosApp/                     # iOS Swift App target hosting the KMP framework
├── server/                     # Ktor 3 Backend Server (PostgreSQL, Exposed ORM, WebSockets)
├── scripts/                    # Shared automation scripts (e.g., build.sh, test.sh)
└── docker-compose.yml          # Container configuration for local PostgreSQL database
```

---

## 🎨 Icons Guidelines

- **Never create custom vector icons** (in `StrideVectors.kt` or elsewhere) if they are already available in the standard Material 3 Icons package (`androidx.compose.material.icons.Icons` / `androidx.compose.material.icons.filled.*`).
- Custom vector definitions in `StrideVectors.kt` are reserved exclusively for custom brand icons (such as Google and Apple logos) that are not part of the standard Material 3 spec.
- Always check the standard Material 3 Icon catalog before creating a new icon. Use `compose.materialIconsExtended` if extended Material icons are needed.
