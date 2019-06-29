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
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.BindingName
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.sparetimedevs.win.model.Name
import java.util.Optional

class PutNextCandidate {

    @FunctionName(FUNCTION_NAME)
    fun putNextCandidateHttpTrigger(
            @HttpTrigger(
                    name = TRIGGER_NAME,
                    methods = [HttpMethod.PUT],
                    route = "candidates/name/{$BINDING_NAME_NAME}/date/{$BINDING_NAME_DATE}",
                    authLevel = AuthorizationLevel.FUNCTION
            )
            request: HttpRequestMessage<Optional<String>>,
            @BindingName(BINDING_NAME_NAME) name: Name,
            @BindingName(BINDING_NAME_DATE) date: String,
            context: ExecutionContext
    ): HttpStatus {

        context.logger.info("The first name is: $name")
        context.logger.info("The date is: $date")
        context.logger.info("The queryParameters are: ${request.queryParameters}")
        context.logger.info("The body of the request message is: ${request.body}")

        return HttpStatus.NO_CONTENT
    }

    companion object {
        private const val FUNCTION_NAME = "Put_next_candidate_HTTP_trigger"
        private const val TRIGGER_NAME = "putNextCandidateHttpTrigger"
        private const val BINDING_NAME_NAME = "Name"
        private const val BINDING_NAME_DATE = "Date"
    }
}
