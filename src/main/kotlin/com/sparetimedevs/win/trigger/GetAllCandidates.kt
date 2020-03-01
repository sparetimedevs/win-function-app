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

import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.sparetimedevs.bow.http.CONTENT_TYPE
import com.sparetimedevs.bow.http.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.bow.http.handleHttp
import com.sparetimedevs.win.dependencyModule
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.util.toViewModels
import java.util.Optional

class GetAllCandidates(
        private val candidateService: CandidateService = dependencyModule.candidateService
) {
    
    @FunctionName(FUNCTION_NAME)
    fun get(
            @HttpTrigger(
                    name = TRIGGER_NAME,
                    methods = [HttpMethod.GET],
                    route = ROUTE,
                    authLevel = AuthorizationLevel.FUNCTION
            )
            request: HttpRequestMessage<Optional<String>>,
            context: ExecutionContext
    ): HttpResponseMessage =
            handleHttp(
                    request = request,
                    context = context,
                    domainLogic = candidateService.getAllCandidates().toViewModels(),
                    handleSuccess = ::handleSuccess,
                    handleDomainError = ::handleDomainError
            )
    
    companion object {
        private const val FUNCTION_NAME = "GetAllCandidates"
        private const val TRIGGER_NAME = "getAllCandidates"
        private const val ROUTE = "candidates"
    }
}

@Suppress("UNUSED_PARAMETER")
private fun handleSuccess(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, candidates: List<CandidateViewModel>): IO<Nothing, HttpResponseMessage> =
        IO { request.createResponse(candidates) }

private fun HttpRequestMessage<Optional<String>>.createResponse(candidates: List<CandidateViewModel>): HttpResponseMessage =
        this.createResponseBuilder(HttpStatus.OK)
                .body(candidates)
                .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                .build()
