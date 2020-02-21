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
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.bow.CONTENT_TYPE
import com.sparetimedevs.bow.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.bow.ErrorResponse
import com.sparetimedevs.bow.handleHttp
import com.sparetimedevs.test.data.candidateLois
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.service.CandidateService
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.Optional

class PutNextCandidateTest : BehaviorSpec({
    
    mockkStatic("com.sparetimedevs.bow.HttpHandlerKt")
    val request = mockk<HttpRequestMessage<Optional<String>>>()
    val context = mockk<ExecutionContext>()
    val candidateService = mockk<CandidateService>()
    
    given("put is called") {
        `when`("database is reachable") {
            then( "returns HTTP status no content") {
                val nameInput = candidateLois.name
                val dateInput = "20190831"
                val ioContainingHttpResponseMessage =
                        IO {
                            HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.NO_CONTENT)
                                    .build()
                        }
                
                every { handleHttp(
                        request = request,
                        context = context,
                        domainLogic = any<IO<Candidate>>(),
                        handleFailure = any()
                ) } returns ioContainingHttpResponseMessage
                
                val response = PutNextCandidate(candidateService).put(request, context, nameInput, dateInput)
                
                response.status shouldBe HttpStatus.NO_CONTENT
            }
        }
        
        `when`("database is unreachable") {
            then( "returns error message") {
                val nameInput = candidateLois.name
                val dateInput = "20190831"
                val errorInBody: String = ErrorResponse(SERVICE_UNAVAILABLE_ERROR_MESSAGE).toString()
                val ioContainingHttpResponseMessage =
                        IO {
                            HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(errorInBody)
                                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                                    .build()
                        }
                
                every { handleHttp(
                        request = request,
                        context = context,
                        domainLogic = any<IO<Candidate>>(),
                        handleFailure = any()
                ) } returns ioContainingHttpResponseMessage
                
                val response = PutNextCandidate(candidateService).put(request, context, nameInput, dateInput)
                
                response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe errorInBody
            }
        }
        
        `when`("date could not be parsed") {
            then( "returns error message") {
                val nameInput = candidateLois.name
                val dateInput = "20190831"
                val errorInBody: String = ErrorResponse(DATE_PARSE_ERROR_MESSAGE).toString()
                val ioContainingHttpResponseMessage =
                        IO {
                            HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.BAD_REQUEST)
                                    .body(errorInBody)
                                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                                    .build()
                        }
                
                every { handleHttp(
                        request = request,
                        context = context,
                        domainLogic = any<IO<Candidate>>(),
                        handleFailure = any()
                ) } returns ioContainingHttpResponseMessage
                
                val response = PutNextCandidate(candidateService).put(request, context, nameInput, dateInput)
                
                response.status shouldBe HttpStatus.BAD_REQUEST
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe errorInBody
            }
        }
    }
})
