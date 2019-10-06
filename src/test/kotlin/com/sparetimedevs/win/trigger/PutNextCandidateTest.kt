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
import com.sparetimedevs.test.data.candidateLois
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.util.parseDate
import io.kotlintest.matchers.string.contain
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import java.time.Instant
import java.util.Date
import java.util.Optional
import java.util.logging.Logger

class PutNextCandidateTest : BehaviorSpec({

	given("put is called") {
		`when`("database is reachable") {
			then( "returns HTTP status no content") {
				mockkStatic("com.sparetimedevs.win.util.DateParserKt")
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val dateString = "20190831"
				val date = Date.from(Instant.ofEpochSecond(1567202400L))
				val context = mockk<ExecutionContext>()
				val candidateService = mockk<CandidateService>()

				coEvery { dateString.parseDate() } returns Right(Date.from(Instant.ofEpochSecond(1567202400L)))
				every { candidateService.addDateToCandidate(candidateLois.name, date) } returns IO.just(Right(candidateLois))
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.NO_CONTENT)

				val response = PutNextCandidate(candidateService).put(request, candidateLois.name, dateString, context)

				response.status shouldBe HttpStatus.NO_CONTENT
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				mockkStatic("com.sparetimedevs.win.util.DateParserKt")
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val dateString = "20190831"
				val date = Date.from(Instant.ofEpochSecond(1567202400L))
				val context = mockk<ExecutionContext>()
				val candidateService = mockk<CandidateService>()
				val logger = mockk<Logger>()

				every { context.logger } returns logger
				every { logger.severe(any<String>()) } just Runs
				val eitherContainingDomainError = Left(DomainError.ServiceUnavailable())
				coEvery { dateString.parseDate() } returns Right(Date.from(Instant.ofEpochSecond(1567202400L)))
				every { candidateService.addDateToCandidate(candidateLois.name, date) } returns IO.just(eitherContainingDomainError)
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)

				val response = PutNextCandidate(candidateService).put(request, candidateLois.name, dateString, context)

				response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
				response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
				response.body shouldBe contain(DomainError.ServiceUnavailable().message)
			}
		}

		`when`("date could not be parsed") {
			then( "returns error message") {
				mockkStatic("com.sparetimedevs.win.util.DateParserKt")
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val dateString = "boom"
				val context = mockk<ExecutionContext>()
				val candidateService = mockk<CandidateService>()
				val logger = mockk<Logger>()

				every { context.logger } returns logger
				every { logger.info(any<String>()) } just Runs
				val errorMessage = "An exception was thrown while parsing the date string."
				val eitherContainingError = Left(DomainError.DateParseError(errorMessage))
				coEvery { dateString.parseDate() } returns eitherContainingError
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.BAD_REQUEST)

				val response = PutNextCandidate(candidateService).put(request, candidateLois.name, dateString, context)

				response.status shouldBe HttpStatus.BAD_REQUEST
				response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
				response.body shouldBe contain(errorMessage)
			}
		}
	}
})
