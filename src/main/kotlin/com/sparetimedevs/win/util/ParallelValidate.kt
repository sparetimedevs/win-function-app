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

package com.sparetimedevs.win.util

import arrow.core.NonEmptyList
import arrow.core.Tuple2
import arrow.core.Validated
import arrow.core.Validated.Invalid
import arrow.core.Validated.Valid
import arrow.core.extensions.nonemptylist.monad.flatten

fun <Error, A, B, C> parallelValidate(
    v1: Validated<Error, A>,
    v2: Validated<Error, B>,
    transformation: (A, B) -> C
): Validated<NonEmptyList<Error>, C> =
    when {
        v1 is Valid && v2 is Valid -> Valid(transformation(v1.a, v2.a))
        v1 is Valid && v2 is Invalid -> v2.toValidatedNel()
        v1 is Invalid && v2 is Valid -> v1.toValidatedNel()
        v1 is Invalid && v2 is Invalid -> Invalid(NonEmptyList(v1.e, listOf(v2.e)))
        else -> throw IllegalStateException("Not possible value")
    }

fun <Error, A, B, C, D> parallelValidate(
    v1: Validated<Error, A>,
    v2: Validated<Error, B>,
    v3: Validated<Error, C>,
    transformation: (A, B, C) -> D
): Validated<NonEmptyList<Error>, D> =
    parallelValidate(
        parallelValidate(v1, v2) { a, b -> Tuple2(a, b) },
        v3.toValidatedNel()
    ) { aAndB, c ->
        transformation(aAndB.a, aAndB.b, c)
    }.mapLeft { it.flatten() }
