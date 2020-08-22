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

package com.sparetimedevs.win.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.IOPool
import arrow.fx.coroutines.evalOn
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import com.sparetimedevs.win.model.DomainError

suspend inline fun <reified T : Any> databaseRequest(crossinline block: suspend () -> Result<Error, T>): Either<DomainError, T> =
    Either.catch {
        evalOn(IOPool) { block() }
    }.fold(
        { DomainError.UnknownError(it.message ?: "An unknown error occurred while accessing the database.").left() },
        { result ->
            when (result) {
                is Result.Failure -> result.value.toDomainError().left()
                is Result.Success -> result.value.right()
            }
        }
    )
