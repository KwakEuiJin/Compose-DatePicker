# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Language Preference

**Always respond in Korean (한국어)** when interacting with this codebase. All explanations, summaries, documentation, and guidance should be written in Korean. Code identifiers, file/folder names, and technical terms should remain in English, but conceptual explanations and context should be in Korean.

## Project Overview

Compose-Pickers (formerly Compose-DateTimePicker) is a Kotlin Multiplatform library providing constraint-aware wheel selection for Compose: a generic `WheelPicker<T>` plus date, time, and duration presets. It targets Android, iOS, Desktop (JVM), and Web with a shared codebase. The target Maven coordinates are `io.github.kez-lab:compose-pickers` (first release pending); the latest published artifact is still `io.github.kez-lab:compose-date-time-picker:0.6.0`.

**Repository VERSION_NAME**: 0.8.0
**License**: Apache 2.0

## Architecture

### Component Hierarchy

The library follows a **composition-based architecture** with a single generic `Picker<T>` component as the foundation:

```
Picker<T> (generic scrollable picker)
  ↓ composed into
TimePicker (hour + minute + optional AM/PM)
YearMonthPicker (year + month)
DatePicker (year + month + day)
```

**Key pattern**: Higher-level components (`TimePicker`, `YearMonthPicker`) are compositions of multiple controlled `Picker` instances, not subclasses. Generic `Picker<T>` receives `selectedItem` and `onSelectedItemChange` from the caller; higher-level state classes own only logical date/time values.

### Core Components

**`Picker.kt`** (~440 lines)
- Generic scrollable picker using `LazyColumn` with snap behavior
- Supports infinite (`isInfinity=true`) and bounded scrolling
- Uses a bounded cyclic buffer (`items.size * 1000`) for infinite mode
- Fading edge effect via `graphicsLayer` + `BlendMode.DstIn`
- State updates via `snapshotFlow { firstVisibleItemIndex }` in `LaunchedEffect`

**State files**
- `time/TimePickerState.kt` contains `TimePickerState` and `rememberTimePickerState`
- `date/DatePickerState.kt` contains `DatePickerState` and `rememberDatePickerState`
- `date/YearMonthPickerState.kt` contains `YearMonthPickerState` and `rememberYearMonthPickerState`
- `TimePickerState` exposes `selectedTime: LocalTime` and `selectedHourOfDay`
- `YearMonthPickerState` exposes `selectedYearMonth: YearMonth`; `selectedMonthDate: LocalDate` is for interoperability with date APIs
- Specialized picker states use saveable state; generic `Picker<T>` is controlled by caller-owned state because arbitrary `T` is not guaranteed saveable

**`DatePicker.kt`** / **`DatePickerState.kt`**
- Compose year, month, and day pickers
- Clamp selected day when the selected year/month has fewer days
- `DatePickerState` exposes `selectedDate: LocalDate`

**`TimePicker.kt`** / **`YearMonthPicker.kt`**
- Compose multiple `Picker` instances in a `Row`
- Handle 12/24-hour format conversion (TimePicker only)
- Use `kotlinx-datetime` for date/time utilities

### State Management Flow

```
User scrolls LazyColumn
  → LazyListState.firstVisibleItemIndex changes
  → snapshotFlow emits new index
  → Picker calls onSelectedItemChange(item)
  → caller-owned selectedItem updates
  → Recomposition shows updated UI
```

### Multiplatform Structure

```
pickers/src/
├── commonMain/kotlin/    # Shared UI and logic
├── androidMain/          # Android-specific (UI tooling preview)
├── iosMain/              # iOS-specific (currently minimal)
├── desktopMain/          # Desktop-specific (currently minimal)
├── jsMain/               # Web-specific source set directory, currently minimal
└── commonTest/           # Shared unit tests
```

Most logic lives in `commonMain`. Platform-specific code is minimal.

## Development Commands

## Maintainer Workflow Preferences

- Use `feature/*` branch names for new implementation work in this repository.
- Treat `main` as the integration branch. There is currently no `develop` branch on the remote.
- For long autonomous improvement runs, keep one PR-sized slice active at a time. Prefer a separate worktree from `origin/main` when the current checkout has dirty feature work, and do not mix agent workflow assets with picker product changes unless the maintainer explicitly asks for workflow assets.
- In autonomous improvement runs, a PR-sized slice is not complete until it has its own commit(s), pushed `feature/*` branch, opened PR, and merge attempt after local verification. For these autonomous merges, use GitHub's `mergeable` value as the merge gate: proceed when it reports `MERGEABLE` and the branch has passed the relevant local verification. Do not wait for hosted PR automation or `mergeStateStatus == CLEAN` when PR automation is disabled or intentionally omitted. If merge is blocked, record the blocker and continue with the next actionable slice only after the blocker is explicit.
- After a substantial implementation step, run a six-agent feedback loop when the maintainer asks for autonomous improvement work: collect feedback, fix actionable issues, verify again, then open or update the PR.
- Hosted GitHub Actions PR automation is intentionally disabled while the matrix is too slow. Merge PRs after relevant local verification passes and GitHub reports the PR as `MERGEABLE`; run the manual `workflow_dispatch` CI only when hosted evidence is explicitly requested.
- The one exception is `.github/workflows/screenshot-test.yml`, which runs `:screenshot-tests:validateDebugScreenshotTest` on pull requests. It needs no emulator or device, so it is cheap enough to guard every PR that can change how the pickers render, and it is the only automatic check that the reference images reproduce on a host other than the one that recorded them. Keep it out of the slow matrix; do not add emulator or multiplatform work to it.
- Keep improving toward Android developer ergonomics first: state APIs, sample usability, documentation clarity, accessibility, and predictable behavior in real app lifecycles.
- Before adding custom performance machinery, verify that the standard Compose/runtime primitives are insufficient. Prefer `remember`, `derivedStateOf`, `snapshotFlow`, stable object ownership, and domain helpers before introducing bespoke caches or dirty-check classes. If the reason is performance, record the measured or directly inspected evidence in the PR.
- Treat bespoke caching, manual invalidation, synchronization, and equality-key logic as high-risk code. If such code remains, add focused regression coverage for stale data, source identity changes, non-default column orders, and state changes that should invalidate derived values. Private code still needs tests when it replaces framework guarantees.
- When the same invariant appears in multiple components, extract the common rule or deliberately audit every counterpart. Keep `contains`, `coerce*`, validation, rendered item filtering, and selection repair behavior aligned across TimePicker, DatePicker, YearMonthPicker, and DateRangePicker.
- Before handing off a PR for review, red-team the implementation against the assumptions used to build it: look for unmeasured optimization, duplicated local patterns, missing fast paths, missing tests around non-standard logic, and inconsistent failure contracts.
- During 0.x API stabilization, prefer the best long-term API shape over source compatibility when the maintainer explicitly authorizes breaking changes. Document every breaking change in README/CHANGELOG and update ABI dumps.
- Keep public state APIs colocated with their component package unless there is a strong API-design reason not to. For example, `TimePickerState` and `rememberTimePickerState` live in `io.github.kezlab.compose.pickers.time`; date and year-month state APIs live in `io.github.kezlab.compose.pickers.date`.
- Prefer controlled picker APIs with a single source of truth. Avoid APIs where both a state object and positional parameter can initialize or mutate the same selection.
- For composite pickers, expose a state object for saveable logical selection plus an optional `onSelected*Change` callback for user-driven changes. Document that programmatic `state.select*` calls require the caller to update app-owned state in the same event handler.
- For year/month-only values, prefer `date.YearMonth` over encoding the selection as the first day of a `LocalDate`; keep `selectedMonthDate` only as an interop convenience.
- Keep repeated visual/layout picker configuration grouped in `PickerStyle` instead of expanding the public composable parameter list. New visual options should usually be added to `PickerDefaults.style(...)` and documented as a breaking API change while the library is still 0.x.
- Keep interaction availability as a top-level `enabled` parameter on generic and composite pickers. Disabled pickers should block scroll/click/custom semantics actions while keeping the selected value visible and exposed as disabled semantics.
- Keep repeated structural semantics configuration grouped in `PickerSemantics` or the component-specific semantics option objects instead of expanding composable signatures with more label/action parameters.
- Keep repeated custom item-list configuration grouped in component-specific item option objects instead of adding separate list parameters to picker composables.
- When public picker APIs accept custom item-list option objects, validate non-empty lists, duplicate values, supported value ranges, and presence of the current selected value before composing the underlying `Picker`.
- When app-driven selection can come from restored values or presets, prefer public `items.coerce*` helpers or `state.select*(value, items)` overloads over asking apps to duplicate picker constraint logic.
- When custom item lists affect first-render validity, provide `remember*State(items = ..., initial... = ...)` examples so apps can normalize initial state before the picker composes.
- Use `TimePickerConstraints` / `PickerDefaults.timePickerItems(minTime = ..., maxTime = ...)` for exact inclusive time bounds. Ensure examples create state with the same `items` object so restored or preset times are normalized before composition.
- Keep value formatting (`PickerItemFormat` / component `format`) responsible for visible item text and optional value content descriptions. Keep `PickerSemantics` for structural picker labels and action labels. When localized visible labels and TalkBack output should differ, update format examples and semantics examples together.
- For `DatePickerItems.dayItems`, remember that the list is filtered by the selected year/month maximum day. If a year/month change makes the current day unavailable, keep the component behavior predictable by moving to the closest available valid day and documenting that behavior.
- For exact `DatePicker` min/max date use cases, prefer `DatePickerConstraints` / `PickerDefaults.datePickerItems(minDate = ..., maxDate = ...)` over asking apps to approximate ranges with independent year/month/day lists. Keep `contains`, `coerceDate`, validation, and rendered column filtering aligned.
- For exact `YearMonthPicker` min/max use cases, prefer `YearMonthPickerConstraints` / `PickerDefaults.yearMonthPickerItems(minYearMonth = ..., maxYearMonth = ...)`. Keep `contains`, `coerceYearMonth`, validation, and rendered column filtering aligned by whole `YearMonth`, not independent year/month fields.
- When public validation rules change, update KDoc plus `README.md` and `README_KO.md` in the same PR, including failure mode and whether the app must clamp state before rendering.
- Do not reintroduce legacy `startTime`, `startLocalDate`, or generic `startIndex` component parameters. Prefer explicit `remember*State` overloads for initial values and controlled `selectedItem` for generic `Picker<T>`.
- Treat `remember*State` initial parameters as first-composition defaults. Avoid resetting picker state from changing clock/date expressions during recomposition unless the API explicitly models a reset, and keep internal picker scroll state/effects keyed with state resets.
- When adding public state mutation APIs, keep logical state and picker scroll position synchronized, and require apps to keep custom item lists consistent with requested values.
- During programmatic selection sync, do not let intermediate `LazyListState` scroll positions overwrite the requested state value before the picker settles.
- When higher-level components pass semantics labels to `Picker`, expose them through component-specific semantics option objects with sensible defaults so Android apps can localize TalkBack output. Update KDoc and both READMEs in the same PR.
- Kotlin 2.4.10 ABI validation uses `checkKotlinAbi`/`updateKotlinAbi` in this repo. The former `checkLegacyAbi`/`updateLegacyAbi` names still resolve but are deprecated aliases. Also note the DSL change: `abiValidation { }` now enables validation on its own and the `enabled` property was removed. Re-check task names and dump format after Kotlin upgrades.
- The build runs AGP 9 in compatibility mode: `android.builtInKotlin=false` and `android.newDsl=false` in `gradle.properties`. Both are required because AGP 9 forbids `org.jetbrains.kotlin.multiplatform` together with `com.android.library`/`com.android.application`, which is how `:pickers` and `:sample` are structured, and `org.jetbrains.kotlin.android` (used by `:benchmark`/`:benchmark-app`) is incompatible with the new DSL. AGP 10 removes both flags, so revisit when KMP supports the AGP 9 DSL — `:pickers` would move to `com.android.kotlin.multiplatform.library` and the Android app part of `:sample` would need its own non-KMP subproject.
- Treat `pickers/api/` as committed release-gate data. Public API changes must include reviewed ABI dump updates, and reviewers should separate intended picker/state API changes from preview/generated resource churn.
- Keep `@Preview` composables private tooling code so sample previews do not become part of the supported public API surface. Reject ABI dump changes that add `*Preview` symbols back to `pickers/api/`. If accidental preview symbols are removed from ABI dumps, call out the compatibility impact in the PR and release notes.
- Distinguish the latest public Maven Central/GitHub Release version from the repository `VERSION_NAME`. Before changing README install snippets, verify the public release and do not point copy-paste dependency examples at unpublished versions unless the docs explicitly mark them as unreleased/local-publish usage.
- When README install snippets use the latest public release but Usage/API Reference examples target `main` or unreleased APIs, add visible release-status notes near Installation, Usage, and API Reference, including the `publishToMavenLocal` command for local testing.
- Keep repository guidance up to date in this `AGENTS.md` when the maintainer gives durable process feedback.
- Do not include local agent/tooling folders such as `.agents/` or `.claude/` in product PRs unless the change is explicitly about agent workflow assets.

### Build

```bash
# Build library module only
./gradlew :pickers:assemble --no-daemon

# Build all targets (Android, iOS, Desktop, JS)
./gradlew build --no-daemon

# Compile specific targets
./gradlew :pickers:compileKotlinMetadata --no-daemon  # Common
./gradlew :pickers:compileKotlinDesktop --no-daemon   # Desktop
./gradlew :pickers:compileDebugKotlinAndroid --no-daemon
```

### Testing

```bash
# Run all tests
./gradlew :pickers:test --no-daemon

# Run library Android component UI tests on Robolectric.
# AGP 9 no longer registers a release unit test variant, so debug is the only one.
./gradlew :pickers:testDebugUnitTest --no-daemon

# Compile sample Android instrumented tests
./gradlew :sample:assembleDebugAndroidTest --no-daemon

# Run sample Android instrumented tests on the Gradle Managed Device target used by CI
./gradlew :sample:pixel2Api35DebugAndroidTest \
  -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect \
  --no-daemon

# Run sample Android instrumented tests on an already connected local device or emulator
./gradlew :sample:connectedDebugAndroidTest --no-daemon

# Run checks (tests + lint)
./gradlew :pickers:check --no-daemon

# Check PR diff whitespace/conflict markers (same hygiene gate as CI)
git diff --check origin/main...HEAD

# Validate committed Compose preview screenshot references.
# :screenshot-tests:check depends on this task.
./gradlew :screenshot-tests:validateDebugScreenshotTest --no-daemon

# Re-record screenshot references after an intentional visual change.
# Review the diff first, and delete orphaned PNGs left behind by renamed previews.
./gradlew :screenshot-tests:updateDebugScreenshotTest --no-daemon

# Check public Kotlin ABI against the committed reference dump.
# Run this explicitly; do not assume :pickers:check covers the ABI gate.
./gradlew :pickers:checkKotlinAbi --no-daemon

# Update the Kotlin ABI reference dump after an intentional public API change
./gradlew :pickers:updateKotlinAbi --no-daemon

# Verify sample app compilation
./gradlew :sample:compileKotlinDesktop --no-daemon
./gradlew :sample:wasmJsBrowserDistribution --no-daemon
```

**Run a single test**: Use `--tests` filter:
```bash
./gradlew :pickers:testDebugUnitTest --tests "io.github.kezlab.compose.pickers.TimePickerStateTest.timePickerState_24HourFormat_initialValues_areCorrect" --no-daemon
```

### Sample App

```bash
# Run sample on Desktop
./gradlew :sample:desktopRun

# Install sample on Android
./gradlew :sample:installDebug
adb shell am start -n io.github.kezlab.compose.pickers.sample/.MainActivity

# iOS: open iosApp/iosApp.xcodeproj in Xcode
```

### Publishing

```bash
# Check version
./gradlew printVersion

# Test local publish
./gradlew :pickers:publishToMavenLocal

# Production deploy: manually triggered by GitHub Actions workflow_dispatch
# See .github/workflows/maven-central-deploy.yml
```

## Key Implementation Details

### Infinite Scrolling

The `Picker` uses a bounded cyclic buffer for infinite mode:

```kotlin
private const val INFINITE_SCROLL_MULTIPLIER = 1000
val listScrollCount = adjustedItems.size * INFINITE_SCROLL_MULTIPLIER
fun getItem(index: Int) = adjustedItems[index.mod(adjustedItems.size)]
```

This keeps the picker virtually cyclic without exposing `Int.MAX_VALUE` list sizes.

### Snap Behavior

Uses `rememberSnapFlingBehavior(lazyListState)` to snap items to the center position. The selected item is determined by:

```kotlin
listState.firstVisibleItemIndex + visibleItemsMiddle
```

### Text Scaling Animation

The `Picker` interpolates font size and color based on item offset from center:

```kotlin
val fraction = abs(offset / itemHeight).coerceIn(0f, 1f)
fontSize = lerp(selectedTextStyle.fontSize, textStyle.fontSize, fraction)
color = lerp(selectedTextStyle.color, textStyle.color, fraction)
```

## Testing Strategy

**Current coverage** (estimated):
- Unit tests: picker states, date validation, picker index utility behavior, and date/time coercion behavior
- Robolectric Android component UI tests: picker accessibility semantics for selected values, custom labels, state descriptions, state restoration, and higher-level picker forwarding
- Sample Android instrumented smoke tests: home-screen example entry points and a representative navigation path
- Compose preview screenshot tests in `:screenshot-tests`: one case per public picker plus dark theme, disabled, and large-font rendering
- Missing: broader UI interaction tests and full TalkBack/readout validation

**When adding tests**:
- Unit tests → `pickers/src/commonTest/kotlin/`
- Library Android component UI tests that do not need a real Activity/app journey → `pickers/src/androidUnitTest/kotlin/` with Robolectric
- Library Android instrumented tests that must run on a device/emulator → `pickers/src/androidInstrumentedTest/kotlin/`
- Sample Android smoke tests → `sample/src/androidInstrumentedTest/kotlin/`
- Visual regression cases → `screenshot-tests/src/screenshotTest/kotlin/` (Android-only host module; the screenshot plugin does not support KMP modules, so never apply it to `:pickers` or `:sample`)
- Use `:pickers:testDebugUnitTest` (or `:pickers:test`) for library Robolectric component UI tests; AGP 9 no longer registers a release unit test variant. Use `:sample:assembleDebugAndroidTest` to verify sample Android test APK compilation/packaging. Use `:sample:pixel2Api35DebugAndroidTest -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect` for the Gradle Managed Device path used by CI; it requires Android Emulator, the API 35 AOSP ATD system image for the host architecture, and local virtualization/KVM. Use `:sample:connectedDebugAndroidTest` when a local device or emulator is already available. If managed-device prerequisites are unavailable locally, run `assembleDebugAndroidTest` or a managed-device `--dry-run` and rely on CI for the actual emulator run.
- Public Kotlin API/ABI changes → run `:pickers:checkKotlinAbi`. If the API change is intentional and SemVer-appropriate, run `:pickers:updateKotlinAbi`, commit the updated `pickers/api/` dumps, and review preview/generated resource changes separately from supported picker/state API changes.
- Follow naming: `<ComponentName>Test.kt`, `<ComponentName>RobolectricTest.kt`, or `<ComponentName>AndroidTest.kt`

## Code Style

- **Kotlin official style**: `kotlin.code.style=official`
- **Naming**: PascalCase for composables (`TimePicker`), camelCase for functions (`rememberTimePickerState`)
- **Documentation**: KDoc for all public APIs with `@param` descriptions
- **Formatting**: Handled by Kotlin plugin (no explicit ktlint/detekt config found)

## Version Management

Version is set in `gradle.properties`:
```properties
VERSION_NAME=0.8.0
```

Follow **Semantic Versioning**: MAJOR.MINOR.PATCH

## CI/CD

GitHub Actions workflows:
- **`integration-build-test.yml`**: Manual-only (`workflow_dispatch`) hosted verification. PR automation is commented out because the full multiplatform/emulator matrix is slow. Prefer local gates first: `git diff --check origin/main...HEAD`, targeted Gradle tests, `checkKotlinAbi` for public API changes, and sample compilation. Trigger the hosted workflow only when explicit hosted evidence is needed.
- **`maven-central-deploy.yml`**: Publishes releases to Maven Central

Build matrix: Ubuntu latest for Android/Desktop/Wasm and macOS 14 for iOS, using JDK 17 (Temurin)
