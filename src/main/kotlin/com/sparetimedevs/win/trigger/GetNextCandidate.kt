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
import com.sparetimedevs.win.algorithm.CandidateAlgorithm
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.NextCandidateViewModel
import com.sparetimedevs.win.repository.CandidateRepository
import kotlinx.coroutines.runBlocking
import java.util.Optional

class GetNextCandidate {

	private val candidateAlgorithm: CandidateAlgorithm = ServiceLocator.defaultInstance.candidateAlgorithm
	private val candidateRepository: CandidateRepository = ServiceLocator.defaultInstance.candidateRepository

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
    ): HttpResponseMessage  = runBlocking {
		candidateRepository.findAll(sort).fold(
				{
					context.logger.severe("$ERROR_MESSAGE$it")
					request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("$ERROR_MESSAGE$it")
							.build()
				},
				{
					request.createResponseBuilder(HttpStatus.OK)
							.body(candidateAlgorithm.nextCandidate(it).toViewModel())
							.header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
							.build()
				}
		)
	}

    companion object {
        private const val FUNCTION_NAME = "GetNextCandidate"
        private const val TRIGGER_NAME = "getNextCandidate"
        private const val ROUTE = "candidates/next"
    }
}

fun Pair<Candidate, DetailsOfAlgorithm>.toViewModel(): NextCandidateViewModel {
	return NextCandidateViewModel(this.first.name, this.second)
}
