# Compose Preview Screenshot Test

`:screenshot-tests` is an Android-only host module for visual regression checks of the public
Compose-Pickers UI. The official `com.android.compose.screenshot` plugin does not support
Kotlin Multiplatform modules, so it must not be applied to `:pickers` or `:sample`.

The test cases live in
[`screenshot-tests/src/screenshotTest/kotlin`](../../screenshot-tests/src/screenshotTest/kotlin).
They use fixed values rather than the clock so reference images are deterministic.

Coverage is one case per public picker plus the visual states that unit and Robolectric tests
cannot assert: dark theme, `enabled = false`, and a `fontScale = 1.5` layout.

## Enabling the plugin

Two separate opt-ins are required and neither is redundant:

- `android.experimental.enableScreenshotTest=true` in `gradle.properties`, read when the plugin is
  applied.
- `experimentalProperties["android.experimental.enableScreenshotTest"] = true` in the module's
  `android { }` block, read when the module is configured.

Removing either one fails the build with a message naming the other.

## Update approved references

Run this intentionally after reviewing an expected visual change:

```bash
./gradlew :screenshot-tests:updateDebugScreenshotTest --no-daemon
```

Commit the generated PNG files under `screenshot-tests/src/screenshotTestDebug/reference/` with
the source change.

A reference image's file name is derived from the test function name **and a hash of the `@Preview`
arguments**. Renaming a function or changing any `@Preview` argument — even `heightDp` — produces a
new file name, and `updateDebugScreenshotTest` writes the new file **without deleting the old one**.
Validation still passes with the orphan present, so stale references accumulate silently. After an
update, check `git status` for untracked PNGs alongside tracked ones that are no longer written, and
delete the orphans in the same commit.

## Validate references

```bash
./gradlew :screenshot-tests:validateDebugScreenshotTest --no-daemon
```

`:screenshot-tests:check` depends on this task, so the normal verification tasks cover it too.
`.github/workflows/screenshot-test.yml` also runs it on every pull request that touches `pickers/`,
`screenshot-tests/`, or the Gradle configuration, which is what catches a reference image that only
reproduces on the machine that recorded it.

On a mismatch, inspect the local HTML report at
`screenshot-tests/build/reports/screenshotTest/preview/debug/index.html`. Do not update reference
images merely to make a failing comparison pass; first review the generated diff.

Comparison allows a 0.01% pixel difference (`imageDifferenceThreshold`) so that host-level
antialiasing noise between the machine that recorded an image and the machine that validates it
does not fail the build. Real visual regressions are far larger than that.

## What invalidates references

Reference images are rendered by layoutlib against the module's `compileSdk`, and text is rendered
with the fonts from that SDK platform's directory. Expect to re-record baselines — deliberately,
after reviewing the diff — when `compileSdk`, the Compose version, or the screenshot plugin version
changes. `compileSdk` must also stay aligned with the other Android modules, which compile against
37; anything older fails the AAR metadata check, because `:pickers` and the androidx Compose
artifacts on the `screenshotTest` classpath both declare a minimum `compileSdk` of 37.
