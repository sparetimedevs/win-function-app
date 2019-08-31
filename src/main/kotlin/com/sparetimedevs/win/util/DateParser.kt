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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class DateParseError(val message: String)

private const val DATE_FORMAT = "yyyyMMdd"
private const val DEFAULT_DATE_PARSE_ERROR_MESSAGE = "An exception was thrown while parsing the date string."
private val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT)

suspend fun String.parseDate(): Either<DateParseError, Date> =
		Either.catch({ e -> DateParseError(e.message ?: DEFAULT_DATE_PARSE_ERROR_MESSAGE) }) {
			dateFormat.parse(this)
		}
