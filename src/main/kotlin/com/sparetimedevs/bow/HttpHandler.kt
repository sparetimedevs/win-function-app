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

package com.sparetimedevs.bow

import arrow.fx.IO
import arrow.fx.extensions.io.functor.unit
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import java.util.Optional

const val CONTENT_TYPE = "Content-Type"
const val CONTENT_TYPE_APPLICATION_JSON = "application/json"
const val ERROR_MESSAGE_PREFIX = "An error occurred. The error is:"
private const val THROWABLE_MESSAGE_PREFIX = "An exception was thrown. The exception is:"

fun <T> handleHttp(
        request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext,
        domainLogic: IO<T>,
        handleSuccess: (request: HttpRequestMessage<Optional<String>>, t: T) -> IO<HttpResponseMessage> =
                ::handleSuccess,
        handleFailure: (request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, throwable: Throwable) -> IO<HttpResponseMessage> =
                ::handleFailure
): IO<HttpResponseMessage> =
        domainLogic
                .redeemWith(
                        { throwable: Throwable ->
                            handleFailure(request, context, throwable)
                        },
                        { t: T ->
                            handleSuccess(request, t)
                        }
                )

private fun <T> handleSuccess(request: HttpRequestMessage<Optional<String>>, t: T): IO<HttpResponseMessage> =
        IO { request.createResponse() }

private fun HttpRequestMessage<Optional<String>>.createResponse(): HttpResponseMessage =
        this.createResponseBuilder(HttpStatus.NO_CONTENT)
                .build()

private fun handleFailure(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, throwable: Throwable): IO<HttpResponseMessage> {
    log(context, throwable)
    return createResponse(request, throwable)
}

fun log(context: ExecutionContext, throwable: Throwable): IO<Unit> =
        IO {
            context.logger.severe("$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
        }.unit()

fun createResponse(request: HttpRequestMessage<Optional<String>>, throwable: Throwable): IO<HttpResponseMessage> =
        IO {
            request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("$ERROR_MESSAGE_PREFIX $throwable"))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
        }

data class ErrorResponse(val errorMessage: String)
