# Spot: Ticket Table

Source: [PROJECT_PLAN.md](PROJECT_PLAN.md). Each row becomes one GitHub Issue. All tickets start in **To do**.

**Numbering:** GitHub shares one counter between Issues and PRs. Existing items: Issue #1 (Create README, closed) and PR #2 (Add README, merged). The README work is done, so the first new ticket is **#3**. If more Issues or PRs are opened before these are created, shift the IDs (and the Dependencies column) to match.

**Columns:** ID, Title (issue title), Description (issue body), Acceptance Criteria (`- [ ]` checklist), Labels, Priority (High / Medium / Low), Dependencies (blocked by), Assignee, Milestone.

Assignees by track: A = Sébastien Violette, B = Ana Cericatto, C = Vraj Joshi, D = Daniel Peleg. "Group" tickets need one person picked at creation.

---

## M1: GitHub setup and planning (due Nov 21)

| ID | Title | Description | Acceptance Criteria | Labels | Priority | Dependencies | Assignee | Milestone |
|---|---|---|---|---|---|---|---|---|
| #3 | Select Material Design 3 theme | Choose the Material 3 color scheme, typography and shapes for Spot (light and dark) and apply them in `ui/theme/`. | - [ ] Primary, secondary and tertiary colors chosen<br>- [ ] Light and dark color schemes defined in `Color.kt` and `Theme.kt`<br>- [ ] Typography set in `Type.kt`<br>- [ ] Contrast readable in both themes<br>- [ ] Choice shared with the group | track-a, ui | High | None | Sébastien Violette | M1 |
| #4 | Add navigation, ViewModel and icon dependencies | Add Navigation Compose, ViewModel Compose and Material icons to Gradle. | - [ ] Project syncs<br>- [ ] App still builds and launches | track-a, setup | High | None | Sébastien Violette | M1 |
| #5 | Create package structure and seed strings | Create the packages from the plan (navigation, features, data, validation). Seed `strings.xml`. Theme choice is in #3. | - [ ] Packages exist<br>- [ ] `strings.xml` has app name and screen titles | track-a, setup | High | None | Sébastien Violette | M1 |
| #6 | Create AppNavigation with placeholder screens | NavHost and `Routes.kt` with a placeholder screen for every route. Splash goes to Home after a delay. | - [ ] All routes (`splash`, `login`, `home`, `map`, `checkins`, `checkin/{id}`, `new`, `edit/{id}`) reachable<br>- [ ] App launches and navigates between placeholders | track-a, ui | High | #4, #5 | Sébastien Violette | M1 |
| #7 | Decide Room vs SQLiteOpenHelper | Get the instructor's answer on whether Room is allowed and record the decision. | - [ ] Instructor answer received<br>- [ ] Decision written in `PROJECT_PLAN.md` section 4 | track-b, data | High | None | Ana Cericatto | M1 |
| #8 | Set up Firebase project | Create the Firebase project, register the Android app, add `google-services.json`, the Google services plugin and Firebase BoM. Enable Email/Password Auth and Firestore (test mode). Create the user accounts manually in the Firebase console (no in-app sign-up). | - [ ] All members can run the app with Firebase<br>- [ ] Test users created manually, one per member plus one extra for the two-user test<br>- [ ] Auth and Firestore enabled in the console<br>- [ ] `google-services.json` is not committed | track-c, firebase, setup | High | None | Vraj Joshi | M1 |
| #9 | Obtain Geoapify and Google Maps API keys | Create the two keys and share them with members privately (never in Issues or chat logs). | - [ ] Both keys created<br>- [ ] Maps SDK for Android enabled<br>- [ ] Billing requirement for the Maps key checked | track-d, setup | High | None | Daniel Peleg | M1 |
| #10 | Wire API keys through local.properties | Read keys from `local.properties` into `BuildConfig` and the Maps manifest placeholder. Add `local.properties.example`. | - [ ] `buildConfig = true`<br>- [ ] `GEOAPIFY_API_KEY` and `MAPS_API_KEY` read at build time<br>- [ ] `local.properties.example` committed with empty values<br>- [ ] `local.properties` stays git-ignored | track-d, setup | High | #9 | Daniel Peleg | M1 |
| #11 | Create GitHub milestones and labels | Create milestones M1 to M4 with due dates and labels `track-a` to `track-d`, plus type labels. | - [ ] Milestones M1 to M4 with dates<br>- [ ] Four track labels<br>- [ ] Type labels (`ui`, `data`, `api`, `firebase`, `setup`, `docs`) | setup | High | None | Group | M1 |
| #12 | Finish M1 submission | Add the instructor as collaborator, make sure each member has at least one PR, tag `m1` and submit. | - [ ] Repo private, all members and instructor are collaborators<br>- [ ] Every member has at least one PR<br>- [ ] Tag `m1` pushed<br>- [ ] Repo, Milestone and PR URLs submitted | docs, setup | High | #4 to #11, #3 | Group | M1 |

---

## M2: Local app, CRUD and validation (due Nov 28)

| ID | Title | Description | Acceptance Criteria | Labels | Priority | Dependencies | Assignee | Milestone |
|---|---|---|---|---|---|---|---|---|
| #13 | Build Splash screen | Splash with app name and logo, then auto-navigate. | - [ ] Shows name and logo<br>- [ ] Navigates automatically after a short delay<br>- [ ] Text from `strings.xml` | track-a, ui | Medium | #6 | Sébastien Violette | M2 |
| #14 | Build Home screen layout | Home with a nearby venues section (sample data for now) and recent check-ins. | - [ ] Layout matches mockup<br>- [ ] Works in light and dark<br>- [ ] Sample data replaceable later | track-a, ui | High | #6 | Sébastien Violette | M2 |
| #15 | Build Check-ins list screen | `LazyColumn` of check-in cards with category filter chips. | - [ ] List renders<br>- [ ] Chips filter by category<br>- [ ] Tapping a card opens Detail | track-a, ui | High | #6, #19 | Sébastien Violette | M2 |
| #16 | Build Check-in detail screen | Detail layout with Edit and Delete actions and a delete confirmation dialog. | - [ ] All fields displayed<br>- [ ] Edit opens the form<br>- [ ] Delete asks for confirmation | track-a, ui | High | #6, #19 | Sébastien Violette | M2 |
| #17 | Add FAB on Home and List | Floating action button that opens New check-in. | - [ ] FAB on Home and List<br>- [ ] Opens `new` route | track-a, ui | Medium | #6 | Sébastien Violette | M2 |
| #18 | Add bottom navigation | Bottom tabs: Home, Map (placeholder), Check-ins. | - [ ] Tabs switch screens<br>- [ ] Selected tab highlighted<br>- [ ] Hidden on Splash and Login | track-a, ui | High | #6 | Sébastien Violette | M2 |
| #19 | Create Checkin model and Category enum | Kotlin model matching the `checkins` table, and `Category` (Restaurant, Event, Other). | - [ ] `Checkin.kt` and `Category.kt` created<br>- [ ] Fields match the plan's schema | track-b, data | High | #5 | Ana Cericatto | M2 |
| #20 | Implement SQLite storage and CRUD | `checkins` table with create, read (all, by id, by category), update and delete. | - [ ] Table created with the plan's schema<br>- [ ] All CRUD methods work<br>- [ ] Data survives app restart | track-b, data | High | #7, #19 | Ana Cericatto | M2 |
| #21 | Create CheckinRepository and CheckinViewModel | Repository over SQLite and a ViewModel exposing state to screens. | - [ ] Screens never call SQLite directly<br>- [ ] ViewModel exposes list and single check-in state | track-b, data | High | #20 | Ana Cericatto | M2 |
| #22 | Create ValidationResult and check-in validators | Sealed class `ValidationResult` (`Success`, `Error(message)`) and validators. | - [ ] Venue chosen<br>- [ ] Rating 1 to 5<br>- [ ] Note up to 200 characters<br>- [ ] Error messages in `strings.xml` | track-b, data | High | #5 | Ana Cericatto | M2 |
| #23 | Build New and Edit check-in forms | Forms with venue, category, rating and note, with inline validation errors. | - [ ] Create and edit both work<br>- [ ] Invalid input shows the right error<br>- [ ] Save writes to SQLite | track-b, ui | High | #21, #22 | Ana Cericatto | M2 |
| #24 | Build Login screen | Login screen UI (email, password, login button). There is no sign-up screen; users are created manually in the Firebase console. | - [ ] Screen matches mockup<br>- [ ] Login button wired to the ViewModel | track-c, ui | High | #6 | Vraj Joshi | M2 |
| #25 | Add auth validators | Validators for email format and a non-empty password. | - [ ] Uses `ValidationResult`<br>- [ ] Inline errors shown on the Login screen | track-c, firebase | High | #22, #24 | Vraj Joshi | M2 |
| #26 | Implement AuthViewModel with Firebase Auth | Sign in and auth state. Start destination chooses Login or Home. | - [ ] Can sign in with a manually created user<br>- [ ] Wrong credentials show an error<br>- [ ] Signed-in user skips Login<br>- [ ] Back button does not return to Login (`popUpTo`) | track-c, firebase | High | #8, #24, #25 | Vraj Joshi | M2 |
| #27 | Add logout action | Logout control that signs out and returns to Login. | - [ ] Signs out<br>- [ ] Clears back stack | track-c, firebase | Medium | #26 | Vraj Joshi | M2 |
| #28 | Filter check-ins by signed-in user | All queries use the signed-in user's `user_id`. | - [ ] Two users on one device see only their own check-ins | track-c, data | High | #21, #26 | Vraj Joshi | M2 |
| #29 | Create Venue model and GeoapifyClient skeleton | `Venue` model and one working request with a hard-coded location. | - [ ] Request returns data<br>- [ ] Key read from `BuildConfig` | track-d, api | High | #10 | Daniel Peleg | M2 |
| #30 | Parse Geoapify JSON into Venue objects | Map the response to `Venue` (id, name, address, lat, lng, category). | - [ ] Parsing works on a real response<br>- [ ] Missing fields handled | track-d, api | High | #29 | Daniel Peleg | M2 |
| #31 | Build Map screen skeleton | Google Map centred on a fixed point. | - [ ] Map renders with the key<br>- [ ] Reachable from the bottom tab | track-d, ui | Medium | #10, #18 | Daniel Peleg | M2 |
| #32 | Move all UI text to strings.xml | Replace hardcoded text in composables built so far. | - [ ] No string literals in composables<br>- [ ] Each member covers their own screens | ui | Medium | #13 to #31 | Group | M2 |

---

## M3: Integrations (due Dec 5)

| ID | Title | Description | Acceptance Criteria | Labels | Priority | Dependencies | Assignee | Milestone |
|---|---|---|---|---|---|---|---|---|
| #33 | Add location permission with fallback | Request location permission and use a fixed fallback location if denied. | - [ ] Permission dialog shown<br>- [ ] Denying still lets the app work | track-d, api | High | #31 | Daniel Peleg | M3 |
| #34 | Show nearby venues on Home | Load venues from Geoapify using the user's location. | - [ ] Real venues displayed<br>- [ ] Replaces sample data | track-d, api, ui | High | #14, #30, #33 | Daniel Peleg | M3 |
| #35 | Add venue search with debounce | Search box in New check-in using Geoapify autocomplete, searching after typing stops. | - [ ] Results appear after a pause in typing<br>- [ ] No request per keystroke | track-d, api | High | #23, #30 | Daniel Peleg | M3 |
| #36 | Cache venues in SQLite | `venue_cache` table to reduce API calls. | - [ ] Repeat searches use the cache<br>- [ ] Cache has a timestamp | track-d, data | Low | #20, #30 | Daniel Peleg | M3 |
| #37 | Show check-ins and venues on the Map | Filled pins for check-ins, outline pins for nearby venues, category chips, preview card with Details button. | - [ ] Both pin types shown<br>- [ ] Chips filter pins<br>- [ ] Tap shows preview card<br>- [ ] Details opens the detail screen | track-d, ui | High | #31, #34 | Daniel Peleg | M3 |
| #38 | Add Geoapify attribution | Show "Powered by Geoapify" where API data appears. | - [ ] Visible on Home, search and Map | track-d, ui | Medium | #34 | Daniel Peleg | M3 |
| #39 | Add error and empty states for the API | Handle no network, no results and API errors. | - [ ] Each case shows a message<br>- [ ] No crash with network off | track-d, ui | Medium | #34, #35 | Daniel Peleg | M3 |
| #40 | Push new and edited check-ins to Firestore | Write to `users/{uid}/checkins` and mark rows synced. | - [ ] Create and edit appear in Firestore<br>- [ ] `synced` flag updated | track-c, firebase | High | #21, #26 | Vraj Joshi | M3 |
| #41 | Sync deletes to Firestore | Push rows with `deleted = 1`, then remove them locally. | - [ ] Delete removes the Firestore document<br>- [ ] Local row removed afterwards | track-c, firebase | High | #40 | Vraj Joshi | M3 |
| #42 | Pull check-ins on start and listen for changes | Fetch on app start and listen while the app is open. | - [ ] Second device receives changes<br>- [ ] Local DB updated | track-c, firebase | High | #40 | Vraj Joshi | M3 |
| #43 | Implement conflict rule | Newer `updated_at` wins. | - [ ] Older edits never overwrite newer ones | track-c, firebase | Medium | #42 | Vraj Joshi | M3 |
| #44 | Retry unsynced rows when network returns | Detect connectivity and push pending rows. | - [ ] Offline edits sync after reconnect | track-c, firebase | Medium | #40, #41 | Vraj Joshi | M3 |
| #45 | Add sync status chip | Chip on Home and Detail showing synced or pending. | - [ ] Reflects the `synced` flag | track-c, ui | Low | #40 | Vraj Joshi | M3 |
| #46 | Deploy Firestore security rules | Only the signed-in user can read and write their own subtree. | - [ ] Rules from the plan deployed<br>- [ ] Second user cannot read the first user's data | track-c, firebase | High | #40 | Vraj Joshi | M3 |
| #47 | Fill New check-in form from venue search | Selecting a search result fills venue fields automatically. | - [ ] Name, address, lat, lng and venue id filled | track-b, ui | High | #23, #35 | Ana Cericatto | M3 |
| #48 | Suggest default category from Geoapify | Map Geoapify categories to a suggested Spot category (see plan section 6). | - [ ] `catering` suggests Restaurant<br>- [ ] `entertainment` suggests Event<br>- [ ] Others suggest Other<br>- [ ] User can change it | track-b, data | Medium | #47 | Ana Cericatto | M3 |
| #49 | Polish list, detail and form screens | Spacing, empty states and consistency. | - [ ] Empty list shows a message<br>- [ ] Consistent spacing and typography | track-a, ui | Medium | #15, #16, #23 | Sébastien Violette | M3 |
| #50 | Run sync test checklist | Two devices, airplane mode and two users. | - [ ] Check-in created on one device appears on another<br>- [ ] Offline edits sync later<br>- [ ] Second user sees nothing of the first | track-c, docs | High | #40 to #46 | Vraj Joshi | M3 |

---

## M4: Polish and final submission (due Dec 13, feature freeze Dec 8)

| ID | Title | Description | Acceptance Criteria | Labels | Priority | Dependencies | Assignee | Milestone |
|---|---|---|---|---|---|---|---|---|
| #51 | Check light and dark theme on every screen | Review all screens in both themes. | - [ ] No unreadable text or contrast issues | track-a, ui | High | #49 | Sébastien Violette | M4 |
| #52 | Final audit for hardcoded text | Search composables for string literals. | - [ ] None remain | ui | Medium | #32 | Group | M4 |
| #53 | Clean up code | Remove debug logs, unused files and dead code. | - [ ] No `Log.d` or unused imports left | docs | Medium | #49 | Group | M4 |
| #54 | Add meaningful code comments | Each member comments their own code (needed for the documentation). | - [ ] Every class and non-trivial function commented | docs | High | #53 | Group | M4 |
| #55 | Test on emulator and physical device | Rotate, deny permissions, turn off network. | - [ ] No crashes in any case | docs | High | #50 | Group | M4 |
| #56 | Merge PRs, close Issues and release v1.0.0 | All approved PRs merged to `main`, Issues closed, release and tag `m4`. | - [ ] Release `v1.0.0` published<br>- [ ] Tag `m4` pushed<br>- [ ] Repo, Milestone, PR URLs submitted | docs | High | #55 | Daniel Peleg | M4 |
| #57 | Prepare source ZIP | Export from Android Studio without secrets. | - [ ] `local.properties` removed<br>- [ ] ZIP opens and builds | docs | High | #55 | Group | M4 |
| #58 | Write documentation GroupProject_4.docx | Light and dark screenshots of every screen plus all source code as text. | - [ ] Every screen in both themes<br>- [ ] Code pasted as text with comments | docs | High | #54 | Group | M4 |
| #59 | Record demo video | Emulator demo of every feature, with each member presenting their own part. | - [ ] All required features shown<br>- [ ] Each member presents<br>- [ ] Uploaded or linked | docs | High | #55 | Group | M4 |
