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
import com.sparetimedevs.win.model.CandidateResponse

suspend fun handleSuccessWithDefaultOrTextPlainHandler(
    request: HttpRequestMessage<out Any?>,
    candidates: List<CandidateResponse>
): Either<Throwable, HttpResponseMessage> =
    if (isTextPlainPreferred(request.headers)) {
        handleSuccessWithTextPlainHandler(request, candidates)
    } else {
        handleSuccessWithDefaultHandler(request, candidates)
    }

private fun isTextPlainPreferred(headers: Map<String, String>): Boolean {
    val accept = headers.getOrDefault("accept", CONTENT_TYPE_APPLICATION_JSON)
    return when {
        accept.contains(CONTENT_TYPE_APPLICATION_JSON) && accept.contains("text/plain") -> true
        accept.contains(CONTENT_TYPE_APPLICATION_JSON) -> false
        accept.contains("text/plain") -> true
        else -> false
    }
}
