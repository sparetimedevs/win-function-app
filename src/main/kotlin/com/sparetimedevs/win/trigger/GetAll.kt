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

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.sparetimedevs.win.ServiceLocator
import com.sparetimedevs.win.repository.CandidateRepository
import kotlinx.coroutines.runBlocking
import java.util.Optional

class GetAll {

    private val candidateRepository: CandidateRepository = ServiceLocator.defaultInstance.candidateRepository

    @FunctionName(FUNCTION_NAME)
    fun getAllHttpTrigger(
            @HttpTrigger(
                    name = TRIGGER_NAME,
                    methods = [HttpMethod.GET],
                    route = "candidates",
                    authLevel = AuthorizationLevel.FUNCTION
            )
            request: HttpRequestMessage<Optional<String>>,
            context: ExecutionContext
    ): HttpResponseMessage {

        context.logger.info("The body of the request message is: ${request.body}")

        var message = "This message should be overridden."

        runBlocking {

            val candidates = candidateRepository.findAll()
            candidates.fold(
                    {
                        message = "ERROR!!! $it"
                    },
                    {
                        message = "SUCCESS!!! The list $it"
                    }
            )
        }

        context.logger.info(message)

        return request.createResponseBuilder(HttpStatus.OK).body("TODO, return all candidates. For now you have to do it with: $message" ).build()
    }

    companion object {
        private const val FUNCTION_NAME = "Get_all_HTTP_trigger"
        private const val TRIGGER_NAME = "getAllHttpTrigger"
    }
}
