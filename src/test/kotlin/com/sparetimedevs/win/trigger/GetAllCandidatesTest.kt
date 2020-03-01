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
import com.sparetimedevs.bow.http.CONTENT_TYPE
import com.sparetimedevs.bow.http.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.bow.http.ErrorResponse
import com.sparetimedevs.bow.http.handleHttp
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.util.toViewModels
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.Optional

class GetAllCandidatesTest : BehaviorSpec({
    
    mockkStatic("com.sparetimedevs.bow.http.HttpHandlerKt")
    val request = mockk<HttpRequestMessage<Optional<String>>>()
    val context = mockk<ExecutionContext>()
    val candidateService = mockk<CandidateService>()
    
    every { candidateService.getAllCandidates() } returns IO.raiseException(Exception("Not sure why this mock is needed."))
    
    given("get is called") {
        `when`("database is reachable") {
            then( "returns all candidates") {
                val candidatesInBody: String =
                        IO.just(candidates)
                                .toViewModels()
                                .unsafeRunSyncEither()
                                .fold(
                                        { fail("fail fast")},
                                        { it }
                                )
                                .toString()
                val httpResponseMessage =
                        HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                                .body(candidatesInBody)
                                .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                                .build()
                
                every {
                    handleHttp(
                            request = request,
                            context = context,
                            domainLogic = any<IO<DomainError, List<CandidateViewModel>>>(),
                            handleSuccess = any(),
                            handleDomainError = any()
                    )
                } returns httpResponseMessage
                
                val response = GetAllCandidates(candidateService).get(request, context)

                response.status shouldBe HttpStatus.OK
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe candidatesInBody
            }
        }
        
        `when`("database is unreachable") {
            then( "returns error message") {
                val errorInBody: String = ErrorResponse(SERVICE_UNAVAILABLE_ERROR_MESSAGE).toString()
                val httpResponseMessage =
                        HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(errorInBody)
                                .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                                .build()
                
                every {
                    handleHttp(
                            request = request,
                            context = context,
                            domainLogic = any<IO<DomainError, List<CandidateViewModel>>>(),
                            handleSuccess = any(),
                            handleDomainError = any(),
                            handleSystemFailure = any()
                    )
                } returns httpResponseMessage
                
                val response = GetAllCandidates(candidateService).get(request, context)
                
                response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe errorInBody
            }
        }
    }
})
