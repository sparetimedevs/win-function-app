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

import arrow.core.Either
import arrow.core.right
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.test.data.candidateLois
import com.sparetimedevs.win.algorithm.DetailsOfRolledDice
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.ErrorViewModel
import com.sparetimedevs.win.model.NextCandidateViewModel
import com.sparetimedevs.win.service.CandidateService
import com.sparetimedevs.win.trigger.handler.CONTENT_TYPE
import com.sparetimedevs.win.trigger.handler.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.win.trigger.handler.SERVICE_UNAVAILABLE_ERROR_MESSAGE
import com.sparetimedevs.win.util.toViewModel
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlin.coroutines.CoroutineContext

class GetNextCandidateTest : BehaviorSpec({
    
    mockkStatic("com.sparetimedevs.pofpaf.handler.HandlerKt")
    val request = mockk<HttpRequestMessage<String?>>()
    val context = mockk<ExecutionContext>()
    val candidateService = mockk<CandidateService>()
    
    coEvery { candidateService.determineNextCandidate() } throws Exception("This mock makes sure that if the handleHttp function is not mocked properly, the test case will fail.")
    
    given("get is called") {
        `when`("database is reachable") {
            then("returns next candidate's name") {
                val detailsOfRolledDice = DetailsOfRolledDice(listOf(3, 1, 5, 1, 2, 4))
                val nextCandidateAndDetailsOfAlgorithmInBody: String =
                    (candidateLois to detailsOfRolledDice).right()
                        .toViewModel()
                        .fold(
                            { fail("fail fast") },
                            { it }
                        )
                        .toString()
                val httpResponseMessage =
                    HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                        .body(nextCandidateAndDetailsOfAlgorithmInBody)
                        .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                        .build()
                
                every {
                    handleBlocking(
                        ctx = any<CoroutineContext>(),
                        domainLogic = any<suspend () -> Either<DomainError, NextCandidateViewModel>>(),
                        handleSuccess = any<suspend (nextCandidate: NextCandidateViewModel) -> Either<Throwable, HttpResponseMessage>>(),
                        handleDomainError = any<suspend (domainError: DomainError) -> Either<Throwable, HttpResponseMessage>>(),
                        handleSystemFailure = any<suspend (throwable: Throwable) -> Either<Throwable, HttpResponseMessage>>(),
                        handleHandlerFailure = any<suspend (throwable: Throwable) -> Either<Throwable, HttpResponseMessage>>(),
                        log = any<suspend (level: Level, message: String) -> Either<Throwable, Unit>>()
                    )
                } returns httpResponseMessage
                
                val response = GetNextCandidate(candidateService).get(request, context)
                
                response.status shouldBe HttpStatus.OK
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe nextCandidateAndDetailsOfAlgorithmInBody
            }
        }
        
        `when`("database is unreachable") {
            then("returns error message") {
                val errorInBody: String = ErrorViewModel(SERVICE_UNAVAILABLE_ERROR_MESSAGE).toString()
                val httpResponseMessage =
                    HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorInBody)
                        .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                        .build()
                
                every {
                    handleBlocking(
                        ctx = any<CoroutineContext>(),
                        domainLogic = any<suspend () -> Either<DomainError, NextCandidateViewModel>>(),
                        handleSuccess = any<suspend (nextCandidate: NextCandidateViewModel) -> Either<Throwable, HttpResponseMessage>>(),
                        handleDomainError = any<suspend (domainError: DomainError) -> Either<Throwable, HttpResponseMessage>>(),
                        handleSystemFailure = any<suspend (throwable: Throwable) -> Either<Throwable, HttpResponseMessage>>(),
                        handleHandlerFailure = any<suspend (throwable: Throwable) -> Either<Throwable, HttpResponseMessage>>(),
                        log = any<suspend (level: Level, message: String) -> Either<Throwable, Unit>>()
                    )
                } returns httpResponseMessage
                
                val response = GetNextCandidate(candidateService).get(request, context)
                
                response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
                response.body shouldBe errorInBody
            }
        }
    }
})
