# Spot

A check-in journal for Android, built with Kotlin and Jetpack Compose.
PROG3211 Final Group Project, **Group 4**.

Users find a venue (nearby or by search), pick a category, rate it, add a note, and save it. Check-ins are stored locally in SQLite, synced to Firebase Firestore, and shown in a list, on a map, and in a detail screen.

## Group members

| Member | Track | Scope |
|---|---|---|
| Sébastien Violette | A: App shell and UI | Theme, navigation, Splash, Home, List, Detail UI, strings, dark mode |
| Ana Cericatto | B: Local data | SQLite, repository, CRUD, validation, New check-in form |
| Vraj Joshi | C: Firebase | Firebase Auth, Firestore sync, security rules, Login and Sign up |
| Daniel Peleg | D: API and map | Geoapify client, venue search, Google Maps screen, location permission |

## Features

- Splash, Login, Sign up, Home, Map, Check-ins list, Check-in detail, New/Edit check-in
- Categories: Restaurant, Event, Other
- Rating (1 to 5) and a note of up to 200 characters, with input validation
- Create, read, update and delete check-ins (SQLite)
- Per-user cloud sync with Firestore (offline-first, newest `updated_at` wins)
- Nearby venues and venue search through the Geoapify REST API
- Google Maps view of check-ins and nearby venues
- Material 3, light and dark themes

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Navigation Compose (bottom tabs plus stack)
- ViewModel and repository architecture
- SQLite for local storage
- Firebase Authentication and Cloud Firestore
- Geoapify Places and Geocoding API
- Google Maps Compose
- minSdk 24, package `com.example.finalprojectgroup4`

## Architecture

Flow: `Screen -> ViewModel -> Repository -> (SQLite | Firestore | Geoapify)`. Screens never touch the database or network directly.

```
navigation/     NavHost, routes, bottom bar
ui/theme/       Material 3 theme
ui/components/  shared composables
features/       splash, auth, home, map, checkins (screens + ViewModels)
data/           model, local (SQLite), remote (Firestore, Geoapify), repository
validation/     ValidationResult sealed class and validators
```

## Getting started

### Requirements

- Android Studio (recent stable version)
- JDK 17 or newer
- An emulator or device running Android 7.0 (API 24) or higher

### API keys

Keys are read from `local.properties`, which is git-ignored and must never be committed. Create or edit it in the project root:

```
GEOAPIFY_API_KEY=your_key
MAPS_API_KEY=your_key
```

- Geoapify key: https://www.geoapify.com
- Google Maps key: Google Cloud Console (enable Maps SDK for Android)

A `local.properties.example` file lists the required names.

### Firebase

1. Ask the Track C owner to add you to the Firebase project.
2. Download `google-services.json` from the Firebase console and place it in `app/`.
3. Email/password Auth and Firestore must be enabled in the console.

### Build and run

1. Clone the repository and open it in Android Studio.
2. Add the keys and `google-services.json` as described above.
3. Sync Gradle, then run the `app` configuration on an emulator or device.

## Team workflow

- Never commit to `main` directly. Work on `feature/<track>-<short-name>` branches and open a Pull Request per Issue.
- Another member reviews and merges each PR.
- Every task is a GitHub Issue with an assignee, a `track-a` to `track-d` label and a Milestone (M1 to M4).
- Small, frequent commits with clear messages.

## Milestones

| Milestone | Due | Focus |
|---|---|---|
| M1 | Nov 21 | GitHub setup, planning, project skeleton |
| M2 | Nov 28 | Local app, CRUD, validation, authentication |
| M3 | Dec 5 | Geoapify, Firestore sync, map |
| M4 | Dec 13 | Polish, documentation, demo video, `v1.0.0` release |

## Attribution

Venue data powered by [Geoapify](https://www.geoapify.com).
