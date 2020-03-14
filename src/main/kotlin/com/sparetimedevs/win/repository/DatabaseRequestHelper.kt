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

package com.sparetimedevs.win.repository

import arrow.fx.IO
import arrow.fx.IO.Companion.effect
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.dispatchers.dispatchers
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import com.sparetimedevs.win.model.DomainError

inline fun <reified T : Any> databaseRequest(crossinline block: suspend () -> Result<Error, T>): IO<DomainError, T> =
    IO.fx<DomainError, T> {
        continueOn(IO.dispatchers<Nothing>().io())
        val result = !effect { block() }
        continueOn(IO.dispatchers<Nothing>().default())
        when (result) {
            is Result.Failure -> IO.raiseError<DomainError, T>(result.value.toDomainError()).bind()
            is Result.Success -> result.value
        }
    }
