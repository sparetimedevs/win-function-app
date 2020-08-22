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

package com.sparetimedevs.win.trigger.handler

import arrow.core.Either
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.win.model.CandidateResponse

suspend fun handleSuccessWithTextPlainHandler(
    request: HttpRequestMessage<out Any?>,
    candidates: List<CandidateResponse>
): Either<Throwable, HttpResponseMessage> =
    Either.catch {
        val candidateRows = candidates.mapIndexed { index, candidate ->
            "| ${index + 1} | ${candidate.name} | ${candidate.turns.joinToString { it }} | ${candidate.firstAttendance} |"
        }
        val textPlainBody = TABLE_TOP.plus(candidateRows.joinToString(separator = "\n"))
        request.createTextPlainResponse(textPlainBody)
    }

private fun HttpRequestMessage<out Any?>.createTextPlainResponse(textPlainBody: String): HttpResponseMessage =
    this.createResponseBuilder(HttpStatus.OK)
        .header(CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN_UTF_8)
        .body(textPlainBody)
        .build()

const val CONTENT_TYPE_TEXT_PLAIN_UTF_8 = "text/plain;charset=UTF-8"
private const val TABLE_TOP = """| Number in list | Name | Dates | First attendance |
|--|--|--|--|
"""
