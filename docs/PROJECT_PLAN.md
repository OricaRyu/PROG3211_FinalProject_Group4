# Spot: Project Plan (PROG3211 Final Group Project, Group 4)

**Group 4:** Sébastien Violette, Ana Cericatto, Vraj Joshi, Daniel Peleg

Working title: **Spot**, a check-in journal for Android (Kotlin, Jetpack Compose).
Users find a venue (nearby or by search), pick a category, rate it, add a note, and save it. Check-ins are stored in SQLite, synced to Firestore, and shown in a list, on a map, and in a detail screen.

Base project (already pushed): package `com.example.finalprojectgroup4`, empty Compose template, Material 3 theme files in `ui/theme/`, minSdk 24, one commit on `main`. No README, navigation, ViewModel, database or Firebase yet.

Everything below is a proposal. Items marked **Confirm** need an answer from the instructor or the group.

---

## 1. Deadlines

| What | Due | Submit to | 
|---|---|---|
| Group enrollment | Nov 8 | eConestoga | 
| Project overview and mockup (Word doc) | **Nov 14, 11:59 PM** | Group Project | 
| M1: GitHub setup and planning | Nov 21 | Group Project - GitHub Checkpoint M1 | 
| M2: Development progress 1 | Nov 28 | Checkpoint M2 |
| M3: Development progress 2 | Dec 5 | Checkpoint M3 | 
| M4 and final submission | **Dec 13, 11:59 PM** | Checkpoint M4 and Group Project | 

Internal target: feature-complete by **Dec 8**, leaving five days for polish, documentation and the video.

If any of the three final items (source ZIP, Word documentation, demo video) is missing, the project scores 0.

---

## 2. Rubric map: where each mark is earned

| Rubric item (marks) | How Spot meets it | Stage |
|---|---|---|
| M1 repository setup (2) | Private repo, collaborators, README, starter project, assigned Issues | M1 |
| M2 / M3 progress (2.5 each) | Regular commits, PRs, updated Issues, individual summaries | M2, M3 |
| M4 final GitHub (3) | PRs merged to `main`, `v1.0.0` release, evidence submitted | M4 |
| Core screens (2) | Splash, Home, List, Detail, plus Map, Login, New check-in | M1 to M3 |
| Navigation and FAB (2) | Navigation Compose, bottom tabs plus stack, FAB on Home and List | M1, M2 |
| SQLite and CRUD (2) | `checkins` table, create, read, update, delete from the UI | M2 |
| Firestore (2) | Store, retrieve and sync check-ins per user | M3 |
| REST API (2) | Geoapify nearby venues and search, result used in Home and New check-in | M3 |
| Kotlin logic and ViewModel (2) | ViewModels, repository, separation of concerns | M1 to M3 |
| Validation and events (2) | Sealed class validation on login and check-in; click handlers | M2 |
| UI and best practices (2) | Material 3, light and dark, no hardcoded text | M1 to M4 |
| Documentation and source (2) | Word doc with screenshots (light and dark) and commented code as text | M4 |
| Individual demonstration (2) | Each member presents and explains their own code | M4 |

---

## 3. Team tracks and GitHub workflow

Each student is graded individually from commits, Issues, PRs and the demo. Give each member one **track** they own end to end, so each person has real code to explain.

| Track | Owner | Scope |
|---|---|---|
| A: App shell and UI | Sébastien Violette | Theme, navigation, Splash, Home, List, Detail UI, strings, dark mode |
| B: Local data | Ana Cericatto | SQLite helper or DAO, repository, CRUD, validation sealed class, New check-in form |
| C: Firebase | Vraj Joshi | Firebase project, Auth, Firestore sync, security rules, Login |
| D: API and map | Daniel Peleg | Geoapify client, JSON parsing, venue search, Google Maps screen, location permission |

Tracks follow the order the members were listed. Swap them if someone prefers another track, but keep one owner per track.

**Rules (the instructor checks these):**
- One private repo, everyone commits with their **own** GitHub account.
- Never commit to `main` directly. Use `feature/<track>-<short-name>` branches and a Pull Request for each Issue. Another member reviews and merges.
- Every task is a GitHub Issue with an assignee, a label (`track-a` to `track-d`) and a GitHub Milestone (M1 to M4). Update Issue status as work moves.
- Small, frequent commits with clear messages. One huge upload near the deadline does not count as progress.
- At each checkpoint, each member writes a short summary of their own work since the last one.

---

## 4. Things to confirm

1. **Database:** does "SQLite" allow Room, or must we use `SQLiteOpenHelper`? Both are SQLite. Room needs KSP and more setup.
2. **Libraries:** are Navigation Compose, Firebase Auth, Maps Compose, coroutines and a JSON or HTTP library allowed or taught? The plan below assumes yes.
3. **Firebase Auth:** the brief only names Firestore. If Auth is not allowed, replace sign-in with a local profile id and keep the same Firestore paths.
4. **Map:** confirmed as Google Maps Compose. Check whether a billing-enabled Google Cloud project is needed for the key.

---

## 5. Architecture and package layout

Keep UI, state, data and network separate. Package root: `com.example.finalprojectgroup4`.

```
MainActivity.kt                 single activity, sets content
navigation/
  AppNavigation.kt              NavHost, routes, bottom bar
  Routes.kt                     route name constants
ui/theme/                       Color.kt, Type.kt, Theme.kt (exists)
ui/components/                  shared composables: CheckinCard, CategoryChips, ...
features/
  splash/    SplashScreen.kt
  auth/      LoginScreen.kt, AuthViewModel.kt
  home/      HomeScreen.kt, HomeViewModel.kt
  map/       MapScreen.kt, MapViewModel.kt
  checkins/  CheckinListScreen.kt, CheckinDetailScreen.kt, NewCheckinScreen.kt, CheckinViewModel.kt
data/
  model/     Checkin.kt, Venue.kt, Category.kt
  local/     CheckinDbHelper.kt (or Room: CheckinDao.kt, AppDatabase.kt)
  remote/    FirestoreSource.kt, GeoapifyClient.kt
  repository/ CheckinRepository.kt, VenueRepository.kt
validation/  ValidationResult.kt, Validators.kt
res/values/strings.xml          all UI text
```

Flow: `Screen -> ViewModel -> Repository -> (SQLite | Firestore | Geoapify)`. Screens never touch the database or network directly.

Routes: `splash`, `login`, `home`, `map`, `checkins`, `checkin/{id}`, `new`, `edit/{id}`.
Navigation: bottom tabs (Home, Map, Check-ins) plus stack navigation for the rest. After login, `popUpTo("login") { inclusive = true }`.

---

## 6. Data model

### SQLite table `checkins`

```sql
CREATE TABLE checkins (
  id          TEXT PRIMARY KEY,      -- UUID, also the Firestore document id
  user_id     TEXT NOT NULL,         -- Firebase Auth uid (or local profile id)
  venue_id    TEXT,                  -- Geoapify place_id
  venue_name  TEXT NOT NULL,
  address     TEXT,
  latitude    REAL NOT NULL,
  longitude   REAL NOT NULL,
  category    TEXT NOT NULL,         -- RESTAURANT | EVENT | OTHER
  rating      INTEGER NOT NULL,      -- 1..5
  note        TEXT,                  -- up to 200 characters
  created_at  INTEGER NOT NULL,      -- epoch millis
  updated_at  INTEGER NOT NULL,
  synced      INTEGER NOT NULL DEFAULT 0,   -- 0 = needs push to Firestore
  deleted     INTEGER NOT NULL DEFAULT 0    -- 1 = deleted locally, push the delete, then remove the row
);
```

Optional `venue_cache` table (venue_id, name, category, address, lat, lng, cached_at) to save API calls.

### Firestore

```
users/{uid}                      displayName, createdAt
users/{uid}/checkins/{id}        same fields as the table (no synced / deleted flags)
```

Security rules (M3): only the signed-in user can read and write their own subtree.

```
match /users/{uid}/{document=**} {
  allow read, write: if request.auth != null && request.auth.uid == uid;
}
```

### Category mapping

Spot has three user-picked categories. Geoapify categories only **suggest** a default:

| Geoapify top-level key | Suggested Spot category |
|---|---|
| `catering` | Restaurant |
| `entertainment`, `activity.events_venue` | Event |
| anything else | Other |

Geoapify lists places, not scheduled events, so "Event" stays a user choice.

---

## 7. Stage plan

### Stage 0: Overview and mockup (due Nov 14)

Word document `ProjectOverview_4.docx`:
- [ ] Full names and college IDs of all four members (names are set; each member adds their own college ID)
- [ ] Title, description, purpose, target users
- [ ] Screen descriptions and navigation flow (at least four screens; we have seven)
- [ ] Mockups exported from the wireframes (Splash, Home, Map, Check-ins, Detail, New check-in, plus Login)
- [ ] Submit to the Group Project folder **before** the deadline

---

### M1: GitHub setup and planning (due Nov 21, tag `m1`)

**Repository (all members)**
- [ ] Repo is private; all four members and the instructor are collaborators
- [ ] Add `README.md`: app name, description, group members, how to build (keys setup)
- [ ] Starter project is pushed (done: initial commit)
- [ ] Create GitHub Milestones M1 to M4 with due dates
- [ ] Create labels `track-a` to `track-d`
- [ ] Create Issues for every task in this plan and assign each one
- [ ] Each member makes at least one PR (for example their track's setup branch)
- [ ] Tag `m1`, submit repo URL, Milestone URL and PR URLs

**Project setup (split by track)**
- [ ] A: add dependencies for Navigation Compose, ViewModel Compose, Material icons
- [ ] A: package structure from section 5, `strings.xml` seeded, theme colors chosen
- [ ] A: `AppNavigation` with placeholder screens for every route, Splash goes to Home after a delay
- [ ] B: decide Room vs `SQLiteOpenHelper` (after the instructor answers)
- [ ] C: create the Firebase project, add the Android app, add `google-services.json`, add the Google services plugin and Firebase BoM, enable Auth (email and password) and Firestore (test mode for now)
- [ ] D: get a Geoapify key and a Google Maps API key
- [ ] D: wire keys through `local.properties` (already git-ignored)

**Secrets setup (D, everyone follows it)**

`local.properties` (never committed):
```
GEOAPIFY_API_KEY=your_key
MAPS_API_KEY=your_key
```
Read them in `app/build.gradle.kts` and expose them as `BuildConfig` fields (set `buildFeatures { buildConfig = true }`) and as a manifest placeholder for the Maps key. Add a `local.properties.example` to the repo listing the names with empty values.

**Done when:** the app launches on emulator and device, navigates between placeholder screens, and the Firebase and key setup works for every member.

---

### M2: Local app, CRUD and validation (due Nov 28, tag `m2`)

Goal: everything that works **without** the network is finished and demoable.

**Track A: UI**
- [ ] Splash with name and logo, then auto-navigate to Home
- [ ] Home screen layout (nearby section can show sample data for now)
- [ ] Check-ins list screen with `LazyColumn` and category filter chips
- [ ] Detail screen layout with Edit and Delete actions and a delete confirmation
- [ ] FAB on Home and List opening New check-in
- [ ] Bottom navigation (Home, Map placeholder, Check-ins)

**Track B: local data and validation**
- [ ] `Checkin` model, `Category` enum
- [ ] SQLite create, read (all, by id, by category), update, delete
- [ ] `CheckinRepository` and `CheckinViewModel` exposing state to the screens
- [ ] `ValidationResult` sealed class (`Success`, `Error(message)`)
- [ ] Validators: venue chosen, rating 1 to 5, note up to 200 characters
- [ ] New check-in and Edit forms with inline errors from the validators

**Track C: authentication**
- [ ] Login screen (no sign-up: users are created manually in the Firebase console)
- [ ] Validators: email format, password not empty
- [ ] `AuthViewModel` using Firebase Auth; start destination chooses Login or Home from auth state
- [ ] Logout action
- [ ] Check-ins are filtered by `user_id` of the signed-in user

**Track D: groundwork**
- [ ] `Venue` model and `GeoapifyClient` skeleton with one working request (hard-coded location)
- [ ] JSON parsing of the response into `Venue` objects
- [ ] Map screen skeleton showing a Google Map centred on a fixed point

**Everyone**
- [ ] All UI text in `strings.xml`
- [ ] PRs merged with at least one review each
- [ ] Individual summary written for M2

**Done when:** you can sign in, create, view, edit and delete a check-in, it survives an app restart, and invalid input shows the right error.

---

### M3: Integrations (due Dec 5, tag `m3`)

**Track D: Geoapify and map**
- [ ] Nearby venues on Home from the user's location (location permission with a fallback location if denied)
- [ ] Venue search in New check-in with debounce (search after the user stops typing)
- [ ] Cache results in SQLite (`venue_cache`) to reduce calls
- [ ] Map screen: pins for the user's check-ins (filled) and nearby venues (outline), category filter chips, preview card on pin tap, Details button
- [ ] Show "Powered by Geoapify" where the data appears
- [ ] Error and empty states: no network, no results, API error

**Track C: Firestore sync**
- [ ] Push new and edited check-ins to `users/{uid}/checkins`
- [ ] Push deletes (rows with `deleted = 1`), then remove them locally
- [ ] Pull on app start and listen for changes while the app is open
- [ ] Conflict rule: the newer `updated_at` wins
- [ ] Retry unsynced rows when the network returns
- [ ] Sync status chip on Home and Detail
- [ ] Deploy security rules from section 6

**Track A and B**
- [ ] Hook the New check-in form to the venue search result (venue fields fill automatically)
- [ ] Suggest a default category from the Geoapify category
- [ ] Polish list, detail and form screens

**Test checklist**
- [ ] Create a check-in on one device, see it on another signed in as the same user
- [ ] Airplane mode: create and edit offline, then reconnect and see it sync
- [ ] A second user cannot see the first user's check-ins

**Done when:** every item in the rubric's technical list works at least once end to end. Anything unfinished becomes a cut or a bug list for M4.

---

### M4: Polish and final submission (due Dec 13, tag `m4`)

**Feature freeze: Dec 8.** After that, only bug fixes and polish.

**Quality**
- [ ] Dark and light themes both checked on every screen
- [ ] No hardcoded UI text anywhere (search for string literals in composables)
- [ ] Remove debug logs, unused files and dead code
- [ ] Meaningful comments in the code (the documentation needs them)
- [ ] Run on the emulator **and** a physical device
- [ ] Rotate the screen, deny permissions, turn off the network: no crashes

**GitHub**
- [ ] All approved PRs merged into `main`
- [ ] Issues closed or updated
- [ ] Create release `v1.0.0` and tag `m4`
- [ ] Each member's contribution summary written
- [ ] Submit repo URL, Milestone URL, PR URLs and tag in the M4 folder

**Final package (all three are mandatory)**
- [ ] **Source ZIP:** Android Studio, File, Export, Export to Zip File. Remove secrets (`local.properties`) before zipping
- [ ] **Documentation** `GroupProject_4.docx`: light and dark screenshots of every screen, plus the complete source code pasted as **text** (not images) with meaningful comments
- [ ] **Demo video** (or link): app running on the emulator, every required feature shown, key source code walked through, **each member presents their own part**

**Done when:** the three items are uploaded to the Group Project folder with a safety margin before 11:59 PM on Dec 13.

---

## 8. Risks and how to handle them

| Risk | Handling |
|---|---|
| M3 is overloaded (API, Firestore, Map at once) | Start the Geoapify request in M2; keep Firestore sync simple (last write wins) |
| One member does not contribute | Individual tracks, PRs and summaries make this visible early; report it early as the brief requires |
| Merge conflicts in shared files (navigation, strings) | Small PRs, merge often, one person owns `AppNavigation.kt` |
| API key leaks | Keys only in `local.properties`; private repo; never paste keys in Issues or chat |
| Free API limit exceeded | Cache venue results, debounce search, limit result counts |
| Library not allowed or not taught | Ask early (section 4); keep a fallback for each choice |
| Late final submission scores 0 | Feature freeze Dec 8; upload the package a day early |

---

## 9. Appendix

**Geoapify endpoints (check the docs for current parameters)**
- Nearby places: `https://api.geoapify.com/v2/places?categories=catering,entertainment&filter=circle:<lon>,<lat>,<radius_m>&limit=20&apiKey=<key>`
- Place and address search: `https://api.geoapify.com/v1/geocode/autocomplete?text=<query>&apiKey=<key>`
- The free plan allows about 3,000 requests a day and requires visible attribution.

**Tagging and release**
```
git tag m1 && git push origin m1
# repeat for m2, m3, m4
gh release create v1.0.0 --title "v1.0.0" --notes "Final submission"
```

**Branch and PR habit**
```
git checkout -b feature/track-b-checkin-crud
git add -p && git commit -m "Add check-in CRUD to SQLite helper"
git push -u origin feature/track-b-checkin-crud
# open a PR, link the Issue (Closes #12), ask a teammate to review
```
