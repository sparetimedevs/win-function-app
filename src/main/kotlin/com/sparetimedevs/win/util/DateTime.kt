/*
 * Copyright (c) 2021 sparetimedevs and respective authors and developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparetimedevs.win.util

import arrow.core.Either
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.DateParseError
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).withZone(ZoneId.from(ZoneOffset.UTC))

suspend fun String.parseDate(): Either<DomainError, OffsetDateTime> =
    Either.catch {
        LocalDate.parse(this, DATE_TIME_FORMATTER)
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC)
            .plusHours(12)
    }
        .mapLeft { throwable: Throwable ->
            throwable.message?.let { DateParseError(it) } ?: DateParseError()
        }

fun OffsetDateTime.toDateFormattedString(): String =
    DATE_TIME_FORMATTER.format(this)

fun OffsetDateTime.toDatabaseDateFormat(): Date =
    Date(this.toInstant().toEpochMilli())

fun Date.fromDatabaseDateFormat(): OffsetDateTime =
    OffsetDateTime.from(this.toInstant().atOffset(ZoneOffset.UTC))
