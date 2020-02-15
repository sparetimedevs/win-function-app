/*
 * Copyright (c) 2020 sparetimedevs and respective authors and developers.
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

package com.sparetimedevs.incubator

import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import io.kotlintest.matchers.shouldBeInRange
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.BehaviorSpec
import java.util.Optional

class HttpHandlerKtTest : BehaviorSpec({
    
    given("handle") {
        `when`("supplied with any request, context and domain logic") {
            then("returns a deterministic result") {
                forAll(Gen.io(), Gen.httpRequestMessage(), Gen.executionContext()) { domainLogic: IO<Any>,
                                                                                     request: HttpRequestMessage<Optional<String>>,
                                                                                     context: ExecutionContext ->
                    val result =
                            handleHttp(
                                    request = request,
                                    context = context,
                                    domainLogic = domainLogic
                            )
                    
                    result.shouldBeInstanceOf<IO<HttpResponseMessage>>()
                    ALL_ASSERTIONS_ARE_POSITIVE
                }
            }
        }
        and("unsafe run sync") {
            `when`("supplied with any request, context and domain logic") {
                then("returns an HttpResponseMessage") {
                    forAll(Gen.io(), Gen.httpRequestMessage(), Gen.executionContext()) { domainLogic: IO<Any>,
                                                                                         request: HttpRequestMessage<Optional<String>>,
                                                                                         context: ExecutionContext ->
                        val response =
                                handleHttp(
                                        request = request,
                                        context = context,
                                        domainLogic = domainLogic
                                ).unsafeRunSync()
                        
                        response.shouldBeInstanceOf<HttpResponseMessage>()
                        response.statusCode shouldBeInRange IntRange(100, 599)
                        ALL_ASSERTIONS_ARE_POSITIVE
                    }
                }
            }
        }
    }
})
