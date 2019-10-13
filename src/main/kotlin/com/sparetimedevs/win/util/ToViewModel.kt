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
import arrow.core.extensions.list.traverse.traverse
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.ToViewModelError
import com.sparetimedevs.win.model.NextCandidateViewModel

fun IO<List<Candidate>>.toViewModels(): IO<List<CandidateViewModel>> =
        this.flatMap { candidates: List<Candidate> ->
            candidates.traverse(IO.applicative(), ::toViewModel).fix()
                    .map {
                        it.fix()
                    }
        }

fun toViewModel(candidate: Candidate): IO<CandidateViewModel> =
        IO.effect {
            candidate.toViewModel()
        }.flattenRaisingError()

suspend fun Candidate.toViewModel(): Either<DomainError, CandidateViewModel> =
        Either.catch({ throwable: Throwable ->
            throwable.message?.let { ToViewModelError(it) } ?: ToViewModelError()
        }) {
            CandidateViewModel(name, firstAttendanceAndTurns.last(), firstAttendanceAndTurns.dropLast(1))
        }

fun Pair<Candidate, DetailsOfAlgorithm>.toViewModel(): NextCandidateViewModel {
    return NextCandidateViewModel(this.first.name, this.second)
}
