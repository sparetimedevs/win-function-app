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
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.util.parseDate
import io.kotlintest.matchers.string.contain
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.time.Instant
import java.util.Date
import java.util.Optional

class PutNextCandidateTest : BehaviorSpec({
    
    given("put is called") {
        `when`("database is reachable") {
            then( "returns HTTP status no content") {
                mockkStatic("com.sparetimedevs.win.util.DateParserKt")
                val request = mockk<HttpRequestMessage<Optional<String>>>()
                val context = mockk<ExecutionContext>()
                val dateString = "20190831"
                val date = Date.from(Instant.ofEpochSecond(1567202400L))
                val candidateService = mockk<CandidateService>()
                
                coEvery { dateString.parseDate() } returns Right(Date.from(Instant.ofEpochSecond(1567202400L)))
                every { candidateService.addDateToCandidate(candidateLois.name, date) } returns IO.just(candidateLois)
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.NO_CONTENT)
                
                val response = PutNextCandidate(candidateService).put(request, context, candidateLois.name, dateString)
                
                response.status shouldBe HttpStatus.NO_CONTENT
            }
        }
        
        `when`("database is unreachable") {
            then( "returns error message") {
                mockkStatic("com.sparetimedevs.win.util.DateParserKt")
                val request = mockk<HttpRequestMessage<Optional<String>>>()
                val context = mockk<ExecutionContext>()
                val dateString = "20190831"
                val date = Date.from(Instant.ofEpochSecond(1567202400L))
                val candidateService = mockk<CandidateService>()
                val ioContainingDomainError = IO.raiseError<Candidate>(DomainError.ServiceUnavailable())
                
                coEvery { dateString.parseDate() } returns Right(Date.from(Instant.ofEpochSecond(1567202400L)))
                every { candidateService.addDateToCandidate(candidateLois.name, date) } returns ioContainingDomainError
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)
                
                val response = PutNextCandidate(candidateService).put(request, context, candidateLois.name, dateString)
                
                response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe contain(SERVICE_UNAVAILABLE_ERROR_MESSAGE)
            }
        }
        
        `when`("date could not be parsed") {
            then( "returns error message") {
                mockkStatic("com.sparetimedevs.win.util.DateParserKt")
                val request = mockk<HttpRequestMessage<Optional<String>>>()
                val context = mockk<ExecutionContext>()
                val dateString = "boom"
                val candidateService = mockk<CandidateService>()
                val eitherContainingError = Left(DomainError.DateParseError())
                
                coEvery { dateString.parseDate() } returns eitherContainingError
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.BAD_REQUEST)
                
                val response = PutNextCandidate(candidateService).put(request, context, candidateLois.name, dateString)
                
                response.status shouldBe HttpStatus.BAD_REQUEST
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe contain(DATE_PARSE_ERROR_MESSAGE)
            }
        }
    }
})
