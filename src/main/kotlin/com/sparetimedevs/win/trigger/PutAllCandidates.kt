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

package com.sparetimedevs.win.trigger

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.core.flatMap
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.win.dependencyModule
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.trigger.handler.handleDomainError
import com.sparetimedevs.win.trigger.handler.handleSuccessWithNoContentHttpResponse
import com.sparetimedevs.win.trigger.handler.handleSystemFailure
import com.sparetimedevs.win.trigger.validator.validateAllCandidatesInput
import com.sparetimedevs.win.util.log
import java.util.logging.Level

class PutAllCandidates(
    private val candidateService: CandidateService = dependencyModule.candidateService
) {
    
    @FunctionName(FUNCTION_NAME)
    fun put(
        @HttpTrigger(
            name = TRIGGER_NAME,
            methods = [HttpMethod.PUT],
            route = ROUTE,
            authLevel = AuthorizationLevel.ADMIN
        )
        request: HttpRequestMessage<String?>,
        context: ExecutionContext
    ): HttpResponseMessage =
        handleBlocking(
            f = {
                request.validateAllCandidatesInput()
                    .toEither()
                    .flatMap { candidates: List<Candidate> ->
                        candidateService.deleteAll()
                            .map { candidates }
                    }
                    .flatMap { candidates: List<Candidate> ->
                        candidateService.addAll(candidates)
                            .sequence(Either.applicative()).fix().map { it.fix() }
                    }
            },
            success = { candidate -> handleSuccessWithNoContentHttpResponse(request, { level, message -> log(context, level, message) }, candidate) },
            error = { domainError -> handleDomainError(request, { level, message -> log(context, level, message) }, domainError) },
            throwable = { throwable -> handleSystemFailure(request, { level, message -> log(context, level, message) }, throwable) },
            unrecoverableState = { throwable -> log(context, Level.SEVERE, "A throwable was thrown: $throwable") }
        )
    
    companion object {
        private const val FUNCTION_NAME = "PutAllCandidates"
        private const val TRIGGER_NAME = "putAllCandidates"
        private const val ROUTE = "candidates"
    }
}
