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

package com.sparetimedevs.win.trigger

import arrow.core.Either
import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.win.model.DomainError
import java.util.Optional

const val ERROR_MESSAGE_PREFIX = "An error occurred. The error is:"
const val TO_VIEW_MODEL_ERROR_MESSAGE = "Something went wrong while transforming the data to view model."
const val DATE_PARSE_ERROR_MESSAGE = "Something went wrong while parsing the date."
const val ENTITY_NOT_FOUND_ERROR_MESSAGE = "What you are looking for is not found."
const val SERVICE_UNAVAILABLE_ERROR_MESSAGE = "The service is currently unavailable."
const val UNKNOWN_ERROR_MESSAGE = "An unknown error occurred."

private const val THROWABLE_MESSAGE_PREFIX = "An exception was thrown. The exception is:"

fun handleFailure(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, throwable: Throwable): IO<HttpResponseMessage> =
        when (throwable) {
            is DomainError -> {
                if (throwable is DomainError.UnknownError) log(context, throwable)
                createResponse(request, throwable)
            }
            else -> {
                log(context, throwable)
                createResponse(request, throwable)
            }
        }

private fun log(context: ExecutionContext, throwable: Throwable): IO<Either<Throwable, Unit>> =
        IO {
            context.logger.severe("$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
        }.attempt()

private fun createResponse(request: HttpRequestMessage<Optional<String>>, domainError: DomainError): IO<HttpResponseMessage> =
        IO {
            when (domainError) {
                is DomainError.ToViewModelError -> {
                    request.createResponseBuilder(HttpStatus.CONFLICT)
                            .body(ErrorResponse(TO_VIEW_MODEL_ERROR_MESSAGE))
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                }
                is DomainError.DateParseError -> {
                    request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body(ErrorResponse(DATE_PARSE_ERROR_MESSAGE))
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                }
                is DomainError.EntityNotFound -> {
                    request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body(ErrorResponse(ENTITY_NOT_FOUND_ERROR_MESSAGE))
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                }
                is DomainError.ServiceUnavailable -> {
                    request.createResponseBuilder(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(ErrorResponse(SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                }
                is DomainError.UnknownError -> {
                    request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ErrorResponse(UNKNOWN_ERROR_MESSAGE))
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                }
            }
        }

private fun createResponse(request: HttpRequestMessage<Optional<String>>, throwable: Throwable): IO<HttpResponseMessage> =
        IO {
            request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("$ERROR_MESSAGE_PREFIX $throwable"))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
        }

private data class ErrorResponse(val errorMessage: String)
