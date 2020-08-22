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
import arrow.core.right
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.CandidateResponse
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.ErrorResponse
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.trigger.handler.CONTENT_TYPE
import com.sparetimedevs.win.trigger.handler.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.win.trigger.handler.CONTENT_TYPE_TEXT_PLAIN_UTF_8
import com.sparetimedevs.win.trigger.handler.SERVICE_UNAVAILABLE_ERROR_MESSAGE
import com.sparetimedevs.win.util.toResponse
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.logging.Logger

class GetAllCandidatesTest : BehaviorSpec({
    
    mockkStatic("com.sparetimedevs.pofpaf.handler.HandlerKt")
    val request = mockk<HttpRequestMessage<String?>>()
    val context = mockk<ExecutionContext>()
    val candidateService = mockk<CandidateService>()
    
    coEvery { candidateService.getAllCandidates() } throws Exception("This mock makes sure that if the handleHttp function is not mocked properly, the test case will fail.")
    
    given("get is called") {
        and("accept header contains application/json") {
            `when`("database is reachable") {
                then("returns all candidates") {
                    val candidatesInBody: String =
                        candidates.right()
                            .toResponse()
                            .fold(
                                { fail("fail fast") },
                                { it }
                            )
                            .toString()
                    val httpResponseMessage =
                        HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                            .body(candidatesInBody)
                            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .build()
                    
                    every { context.logger } returns mockk<Logger>()
                    
                    every {
                        handleBlocking(
                            ctx = any(),
                            f = any<suspend () -> Either<DomainError, List<CandidateResponse>>>(),
                            success = any<suspend (candidates: List<CandidateResponse>) -> Either<Throwable, HttpResponseMessage>>(),
                            error = any(),
                            throwable = any(),
                            unrecoverableState = any()
                        )
                    } returns httpResponseMessage
                    
                    val response = GetAllCandidates(candidateService).get(request, context)
                    
                    response.status shouldBe HttpStatus.OK
                    response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                    response.body shouldBe candidatesInBody
                }
            }
        }
        
        and("accept header contains text/plain and does not contain application/json") {
            `when`("database is reachable") {
                then("returns all candidates") {
                    val httpResponseMessage =
                        HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                            .body(EXPECTED_TEXT_PLAIN_BODY)
                            .header(CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN_UTF_8)
                            .build()
                    
                    every {
                        handleBlocking(
                            ctx = any(),
                            f = any<suspend () -> Either<DomainError, List<CandidateResponse>>>(),
                            success = any<suspend (candidates: List<CandidateResponse>) -> Either<Throwable, HttpResponseMessage>>(),
                            error = any(),
                            throwable = any(),
                            unrecoverableState = any()
                        )
                    } returns httpResponseMessage
                    
                    val response = GetAllCandidates(candidateService).get(request, context)
                    
                    response.status shouldBe HttpStatus.OK
                    response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_TEXT_PLAIN_UTF_8
                    response.body shouldBe EXPECTED_TEXT_PLAIN_BODY
                }
            }
        }
        
        `when`("database is unreachable") {
            then("returns error message") {
                val errorInBody: String = ErrorResponse(SERVICE_UNAVAILABLE_ERROR_MESSAGE).toString()
                val httpResponseMessage =
                    HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorInBody)
                        .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                        .build()
                
                every {
                    handleBlocking(
                        ctx = any(),
                        f = any<suspend () -> Either<DomainError, List<CandidateResponse>>>(),
                        success = any<suspend (candidates: List<CandidateResponse>) -> Either<Throwable, HttpResponseMessage>>(),
                        error = any(),
                        throwable = any(),
                        unrecoverableState = any()
                    )
                } returns httpResponseMessage
                
                val response = GetAllCandidates(candidateService).get(request, context)
                
                response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe errorInBody
            }
        }
    }
}) {
    companion object {
        private const val EXPECTED_TEXT_PLAIN_BODY = """| Number in list | Name | Dates | First attendance |
|--|--|--|--|
| 1 | Rose | 2020-06-29, 2020-05-08, 2020-03-25 | 2020-02-28 |
| 2 | Abbie | 2020-06-11 | 2020-02-28 |
| 3 | Tommy |  | 2020-02-28 |
| 4 | Joseph | 2020-07-27, 2020-05-28 | 2020-02-28 |
| 5 | Fani |  | 2020-02-28 |
| 6 | Eden |  | 2020-02-28 |
| 7 | Tiffany |  | 2020-02-28 |
| 8 | Aisha |  | 2020-02-28 |
| 9 | Elsa |  | 2020-06-11 |
| 10 | Ellen |  | 2020-02-28 |
| 11 | Cerys |  | 2020-02-28 |
| 12 | James |  | 2020-06-29 |
| 13 | Kevin |  | 2020-02-28 |
| 14 | William |  | 2020-02-28 |
| 15 | Elle |  | 2020-02-28 |
| 16 | Lois |  | 2020-02-28 |
| 17 | Alexa |  | 2020-02-28 |
| 18 | Kimberley |  | 2020-02-28 |
| 19 | Saffron |  | 2020-02-28 |
| 20 | Penny |  | 2020-02-28 |
| 21 | George |  | 2020-02-28 |
| 22 | Margaret |  | 2020-02-28 |"""
    }
}
