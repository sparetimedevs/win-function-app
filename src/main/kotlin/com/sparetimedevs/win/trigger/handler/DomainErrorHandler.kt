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
import com.sparetimedevs.win.model.ErrorViewModel

@Suppress("UNUSED_PARAMETER")
suspend fun <E> handleDomainErrorWithDefaultHandler(
    request: HttpRequestMessage<out Any?>,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>,
    e: E
): Either<Throwable, HttpResponseMessage> =
    createResponse(request, e)

suspend fun <E> createResponse(request: HttpRequestMessage<out Any?>, e: E): Either<Throwable, HttpResponseMessage> =
    Either.catch {
        request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .header(
                CONTENT_TYPE,
                CONTENT_TYPE_APPLICATION_JSON
            )
            .body(ErrorViewModel("$ERROR_MESSAGE_PREFIX $e"))
            .build()
    }
