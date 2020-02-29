/*
 * Copyright (c) 2019 sparetimedevs and respective authors and developers.
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
import arrow.fx.IO
import arrow.fx.extensions.toIO
import arrow.fx.flatMap
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.DateParseError
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

private const val DATE_FORMAT = "yyyyMMdd"
private val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT)

fun String.parseDate(): IO<DomainError, Date> =
        IO.effect { this.parseDateSafely() }
                .flatMap { it.toIO() }

suspend fun String.parseDateSafely(): Either<DomainError, Date> =
        Either.catch({ throwable: Throwable ->
            throwable.message?.let { DateParseError(it) } ?: DateParseError()
        }) {
            dateFormat.parse(this)
        }
