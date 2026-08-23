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

Comparison allows a 0.01% pixel difference (`imageDifferenceThreshold`).

## Why validation runs on macOS

`screenshot-test.yml` uses a macOS runner deliberately. Reference images reproduce only on a host
that renders like the one that recorded them, and the 0.01% allowance is nowhere near enough to
absorb the difference. Measured on 2026-08-23 by validating the committed references, recorded on
macOS arm64, on both runner types:

| | differing pixels | per-pixel delta |
| :--- | ---: | ---: |
| Linux x64 runner | 0.164% - 0.888%, all 9 references fail | exactly 1/255, every pixel |
| macOS runner | 0 | - |
| One digit changed (`LocalTime(13, 5)` to `13, 6`), same host | 0.189% | up to 225/255 |

Two things follow.

- Do not move this job to a Linux runner to save cost. Host difference there is 16-90x the
  threshold, so every reference fails.
- Do not raise `imageDifferenceThreshold` to make a cross-host run pass. The setting counts
  differing pixels with no per-pixel tolerance, so a threshold high enough for Linux noise
  (>=0.9%) also masks a one-digit regression at 0.189%. What separates the two is how *much* each
  pixel differs - 1 versus 225 - which this setting cannot express.

If the recording host ever changes, every reference has to be re-recorded there and this job's
runner has to change with it.

## What invalidates references

Reference images are rendered by layoutlib against the module's `compileSdk`, and text is rendered
with the fonts from that SDK platform's directory. Expect to re-record baselines — deliberately,
after reviewing the diff — when `compileSdk`, the Compose version, or the screenshot plugin version
changes. `compileSdk` must also stay aligned with the other Android modules, which compile against
37; anything older fails the AAR metadata check, because `:pickers` and the androidx Compose
artifacts on the `screenshotTest` classpath both declare a minimum `compileSdk` of 37.
