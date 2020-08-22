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
import arrow.core.flatMap
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.BindingName
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.win.dependencyModule
import com.sparetimedevs.win.model.Name
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.trigger.handler.handleDomainError
import com.sparetimedevs.win.trigger.handler.handleSystemFailureWithDefaultHandler
import com.sparetimedevs.win.util.log
import com.sparetimedevs.win.util.parseDate

class PutNextCandidate(
    private val candidateService: CandidateService = dependencyModule.candidateService
) {
    
    @FunctionName(FUNCTION_NAME)
    fun put(
        @HttpTrigger(
            name = TRIGGER_NAME,
            methods = [HttpMethod.PUT],
            route = ROUTE,
            authLevel = AuthorizationLevel.FUNCTION
        )
        request: HttpRequestMessage<String?>,
        context: ExecutionContext,
        @BindingName(BINDING_NAME_NAME) name: Name,
        @BindingName(BINDING_NAME_DATE) date: String
    ): HttpResponseMessage =
        handleBlocking(
            domainLogic = {
                date.parseDate()
                    .flatMap {
                        candidateService.addDateToCandidate(name, it)
                    }
            },
            handleSuccess = { candidate -> handleSuccess(request, { level, message -> log(context, level, message) }, candidate) },
            handleDomainError = { domainError -> handleDomainError(request, { level, message -> log(context, level, message) }, domainError) },
            handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, { level, message -> log(context, level, message) }, throwable) },
            log = { level, message -> log(context, level, message) }
        )
    
    companion object {
        private const val FUNCTION_NAME = "PutNextCandidate"
        private const val TRIGGER_NAME = "putNextCandidate"
        private const val BINDING_NAME_NAME = "Name"
        private const val BINDING_NAME_DATE = "Date"
        private const val ROUTE = "candidates/name/{$BINDING_NAME_NAME}/date/{$BINDING_NAME_DATE}"
    }
}

@Suppress("UNUSED_PARAMETER")
private suspend fun <A> handleSuccess(
    request: HttpRequestMessage<out Any?>,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>,
    a: A
): Either<Throwable, HttpResponseMessage> =
    Either.catch { request.createResponseBuilder(HttpStatus.NO_CONTENT).build() }
