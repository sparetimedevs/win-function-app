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

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.monad.map
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.core.flatMap
import arrow.fx.coroutines.parTraverse
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.CandidateResponse
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.ToResponseError
import com.sparetimedevs.win.model.NextCandidateResponse

suspend fun Either<DomainError, List<Candidate>>.toResponse(): Either<DomainError, List<CandidateResponse>> =
    this
        .map { candidates: List<Candidate> ->
            candidates.parTraverse { candidate -> candidate.toResponse() }
        }
        .flatMap { candidates: List<Either<DomainError, CandidateResponse>> ->
            candidates.sequence(Either.applicative())
                .map { it.fix() }
        }

suspend fun Candidate.toResponse(): Either<DomainError, CandidateResponse> =
    Either.catch {
        CandidateResponse(name, firstAttendanceAndTurns.last().toDateFormattedString(), firstAttendanceAndTurns.dropLast(1).map { it.toDateFormattedString() })
    }
        .mapLeft { throwable: Throwable ->
            throwable.message?.let { ToResponseError(it) } ?: ToResponseError()
        }

fun Either<DomainError, Pair<Candidate, DetailsOfAlgorithm>>.toResponse(): Either<DomainError, NextCandidateResponse> =
    this.map {
        it.toResponse()
    }

fun Pair<Candidate, DetailsOfAlgorithm>.toResponse(): NextCandidateResponse =
    NextCandidateResponse(this.first.name, this.second)
