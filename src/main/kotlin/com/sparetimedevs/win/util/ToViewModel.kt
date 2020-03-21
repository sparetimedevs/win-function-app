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
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.ToViewModelError
import com.sparetimedevs.win.model.NextCandidateViewModel

suspend fun Either<DomainError, List<Candidate>>.toViewModels(): Either<DomainError, List<CandidateViewModel>> =
    this
        .map { candidates: List<Candidate> ->
            candidates.parTraverse { candidate -> candidate.toViewModel() }
        }
        .flatMap { candidates: List<Either<DomainError, CandidateViewModel>> ->
            candidates.sequence(Either.applicative())
                .map { it.fix() }
        }

suspend fun Candidate.toViewModel(): Either<DomainError, CandidateViewModel> =
    Either.catch({ throwable: Throwable ->
        throwable.message?.let { ToViewModelError(it) } ?: ToViewModelError()
    }) {
        CandidateViewModel(name, firstAttendanceAndTurns.last(), firstAttendanceAndTurns.dropLast(1))
    }

fun Either<DomainError, Pair<Candidate, DetailsOfAlgorithm>>.toViewModel(): Either<DomainError, NextCandidateViewModel> =
    this.map {
        it.toViewModel()
    }

fun Pair<Candidate, DetailsOfAlgorithm>.toViewModel(): NextCandidateViewModel =
    NextCandidateViewModel(this.first.name, this.second)
