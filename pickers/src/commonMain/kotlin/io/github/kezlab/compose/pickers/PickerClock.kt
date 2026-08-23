@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.kezlab.compose.pickers

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Reads the current local date and time.
 *
 * `remember*State` initial values default to this, and apps can call it for the same purpose. The
 * value is read at call time, so a long-running app never observes a stale composition-time clock.
 *
 * @param timeZone The time zone used to convert the current instant. Defaults to the system time
 * zone; pass an explicit zone when the picker represents time in a fixed or user-selected zone.
 * @return The current [LocalDateTime] in [timeZone].
 */
fun currentDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
    Clock.System.now().toLocalDateTime(timeZone)

/**
 * Reads the current local date.
 *
 * @param timeZone The time zone used to convert the current instant. Defaults to the system time
 * zone; pass an explicit zone when the picker represents dates in a fixed or user-selected zone.
 * @return The current [LocalDate] in [timeZone].
 */
fun currentDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    currentDateTime(timeZone).date
