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

package com.sparetimedevs.win.repository

import arrow.core.Either
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import com.sparetimedevs.win.model.DomainError

inline fun <reified T : Any> Result<Error, T>.toEither(): Either<DomainError, T> =
    when (this) {
        is Result.Failure -> Either.Left(this.value.toDomainError())
        is Result.Success -> Either.Right(this.value)
    }

fun Error.toDomainError(): DomainError =
    when (this) {
        is Error.EntityNotFound -> DomainError.EntityNotFound(this.message)
        is Error.ServiceUnavailable -> DomainError.ServiceUnavailable(this.message)
        is Error.UnknownError -> DomainError.UnknownError(this.message)
    }
