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
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import io.kotlintest.properties.Gen
import java.net.URI
import java.util.Optional
import java.util.logging.Logger

internal fun Gen.Companion.io(): Gen<IO<Any>> =
        oneOf(
                genAny().map(IO.Companion::just),
                Gen.throwable().map(IO.Companion::raiseError)
        )

private fun Gen.Companion.genAny(): Gen<Any> =
        from(
                listOf(
                        Any(),
                        String(),
                        IO.just(Any()),
                        IO.just(String())
                )
        )

private fun Gen.Companion.throwable(): Gen<Throwable> =
        from(
                listOf(
                        Error(),
                        Exception(),
                        RuntimeException(),
                        IllegalArgumentException(),
                        IllegalStateException(),
                        IndexOutOfBoundsException(),
                        UnsupportedOperationException(),
                        ArithmeticException(),
                        NumberFormatException(),
                        NullPointerException(),
                        ClassCastException(),
                        AssertionError(),
                        NoSuchElementException(),
                        ConcurrentModificationException()
                )
        )

internal fun Gen.Companion.httpRequestMessage(): Gen<HttpRequestMessage<Optional<String>>> =
        from(
                listOf(
                        HttpRequestMessageTestImpl(
                                URI.create("https://sparetimedevs.com"),
                                HttpMethod.GET,
                                mapOf(
                                        "a" to "b",
                                        "c" to "d"
                                ),
                                mapOf(
                                        "a" to "b",
                                        "c" to "d"
                                ),
                                Optional.of("value")
                        ),
                        HttpRequestMessageTestImpl(
                                URI.create("https://sparetimedevs.com"),
                                HttpMethod.POST,
                                mapOf(
                                        "e" to "f",
                                        "g" to "h"
                                ),
                                mapOf(
                                        "a" to "b",
                                        "c" to "d"
                                ),
                                Optional.of("{ \"value\": \"test\" }")
                        )
                )
        )

internal fun Gen.Companion.executionContext(): Gen<ExecutionContext> =
        from(
                listOf(
                        ExecutionContextTestImpl(
                                "lol1",
                                ExecutionTraceContextTestImpl(
                                        "lol2",
                                        "lol3",
                                        mapOf(
                                                "lol4" to "lol5",
                                                "lol6" to "lol7",
                                                "lol8" to "lol9"
                                        )
                                ),
                                Logger.getGlobal(),
                                "lol10"
                        )
                )
        )
