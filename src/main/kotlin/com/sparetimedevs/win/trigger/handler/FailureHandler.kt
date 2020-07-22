/*
 * Copyright (c) 2020 sparetimedevs and respective authors and developers.
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
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.ErrorViewModel

const val TO_VIEW_MODEL_ERROR_MESSAGE = "Something went wrong while transforming the data to view model."
const val DATE_PARSE_ERROR_MESSAGE = "Something went wrong while parsing the date."
const val ENTITY_NOT_FOUND_ERROR_MESSAGE = "What you are looking for is not found."
const val SERVICE_UNAVAILABLE_ERROR_MESSAGE = "The service is currently unavailable."
const val UNKNOWN_ERROR_MESSAGE = "An unknown error occurred."
const val ERROR_MESSAGE_PREFIX = "An error has occurred. The error is:"

suspend fun handleDomainError(
    request: HttpRequestMessage<out Any?>,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>,
    domainError: DomainError
): Either<Throwable, HttpResponseMessage> {
    if (domainError is DomainError.UnknownError) log(
        Level.ERROR,
        "$ERROR_MESSAGE_PREFIX $domainError."
    )
    return createResponse(request, domainError)
}

private suspend fun createResponse(
    request: HttpRequestMessage<out Any?>,
    domainError: DomainError
): Either<Throwable, HttpResponseMessage> =
    Either.catch {
        when (domainError) {
            is DomainError.ToViewModelError -> {
                request.createResponseBuilder(HttpStatus.CONFLICT)
                    .body(ErrorViewModel(TO_VIEW_MODEL_ERROR_MESSAGE))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
            }
            is DomainError.DateParseError -> {
                request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(ErrorViewModel(DATE_PARSE_ERROR_MESSAGE))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
            }
            is DomainError.EntityNotFound -> {
                request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body(ErrorViewModel(ENTITY_NOT_FOUND_ERROR_MESSAGE))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
            }
            is DomainError.ServiceUnavailable -> {
                request.createResponseBuilder(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ErrorViewModel(SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
            }
            is DomainError.UnknownError -> {
                request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorViewModel(UNKNOWN_ERROR_MESSAGE))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
            }
        }
    }
