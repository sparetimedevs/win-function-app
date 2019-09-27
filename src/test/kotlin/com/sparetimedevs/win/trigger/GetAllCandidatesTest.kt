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

import arrow.core.Left
import arrow.core.Right
import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.util.toViewModel
import io.kotlintest.matchers.string.contain
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.util.Optional
import java.util.logging.Logger

class GetAllCandidatesTest : BehaviorSpec({

	given("get is called") {
		`when`("database is reachable") {
			then( "returns all candidates") {
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val context = mockk<ExecutionContext>()
				val candidateService = mockk<CandidateService>()
				val ioContainingEitherContainingCandidates = IO.just(Right(candidates))
				every { candidateService.getAllCandidates() } returns ioContainingEitherContainingCandidates
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)

				val response = GetAllCandidates(candidateService).get(request, context)

				response.status shouldBe HttpStatus.OK
				response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
				response.body shouldBe ioContainingEitherContainingCandidates.toViewModel().toString()
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val context = mockk<ExecutionContext>()
				val logger = mockk<Logger>()
				val candidateService = mockk<CandidateService>()
				every { context.logger } returns logger
				every { logger.severe(any<String>()) } just Runs
				val ioContainingEitherContainingDomainError = IO.just(Left(DomainError.ServiceUnavailable()))
				every { candidateService.getAllCandidates() } returns ioContainingEitherContainingDomainError
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)

				val response = GetAllCandidates(candidateService).get(request, context)

				response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
				response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
				response.body shouldBe contain(DomainError.ServiceUnavailable().message)
			}
		}
	}
})
