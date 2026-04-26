# Miljonaren TV

An Android TV quiz game inspired by the classic "Who Wants to Be a Millionaire" format.

The project is built as a modular Kotlin app with Swedish-first content and local multiplayer.

## What This Project Is

- A TV-first quiz game for Android TV devices.
- Language: Swedish (`sv`) for UI and questions.
- Multiplayer mode: local turn-based play on one TV.
- Sprint 1 rules:
  - 1-4 players
  - Fixed 5 rounds
  - One question per player turn
  - +1 point for correct answer, 0 for wrong
  - Equal number of turns for all players
  - No timer (planned for a later sprint)

## Current Status

Implemented:

- Multi-module project scaffolding
- Core domain models and game use cases
- Local Swedish seed question set
- Local no-repeat question repository
- Basic app flow:
  - Home -> Player Setup -> Gameplay -> Results
- Unit tests for core game logic (`domain-usecase`)

## Tech Stack

- Kotlin
- Jetpack Compose (TV-capable UI)
- AndroidX Navigation Compose
- Hilt (DI setup in place)
- Gradle version catalog (`libs.versions.toml`)
- JUnit for unit tests

## Project Structure

Top-level modules:

- `app-tv` - Android TV app module and navigation host
- `core/*` - shared utilities, navigation constants, UI foundations
- `domain/*` - game models and business logic use cases
- `data/*` - local content and repository implementations
- `feature/*` - screen-level UI modules
- `sync/*` - reserved for future sync/background work

## Getting Started

### Prerequisites

- JDK 17
- Android SDK with:
  - Platform Tools
  - Platform `android-35`
  - Build Tools `35.0.0`

### Local setup

1. Ensure `local.properties` exists in the repo root with your SDK path:

```properties
sdk.dir=/Users/<your-user>/Library/Android/sdk
```

2. Build debug APK:

```bash
./gradlew :app-tv:assembleDebug
```

3. Run domain tests:

```bash
./gradlew :domain:domain-usecase:test
```

## Build Output

After a successful debug build, APK is created at:

`app-tv/build/outputs/apk/debug/app-tv-debug.apk`

## Notes

- This project is inspired by a quiz-show format, but should use original branding/content/assets.
- Question quality and content curation are critical and will continue to evolve.

## Next Milestones

- Improve TV UI polish and focus behavior
- Add richer question packs/categories
- Persist game history with Room
- Add lifelines (`50/50`, audience poll, phone-a-friend style)
- Optional online multiplayer/companion features in later phases

## Implementation Checklist

- Sprint 1.1 execution checklist: `docs/sprint-1.1-checklist.md`
