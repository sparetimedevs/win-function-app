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

import arrow.Kind
import arrow.core.Either
import arrow.core.ForListK
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.either.traverse.sequence
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.traverse.sequence
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.map
import arrow.fx.extensions.io.monad.flatten
import arrow.fx.fix
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.DomainError.ToViewModelError
import com.sparetimedevs.win.model.NextCandidateViewModel

fun IO<Either<DomainError, List<Candidate>>>.toViewModel(): IO<Either<DomainError, List<CandidateViewModel>>> =
		this.map { eitherDomainErrorOrCandidates: Either<DomainError, List<Candidate>> ->
			
			val eitherDomainErrorOrIoOfBoxedEitherDomainErrorOrCandidateViewModels: Either<DomainError, IO<Kind<ForListK, Either<DomainError, CandidateViewModel>>>> =
					eitherDomainErrorOrCandidates
							.map { candidates: List<Candidate> ->
								candidates.map { candidate: Candidate ->
									IO.effect { candidate.toViewModel() }
								}.sequence(IO.applicative()).fix()
							}
			
			val ioOfEitherDomainErrorOrBoxedCandidateViewModels: IO<Either<DomainError, Kind<ForListK, CandidateViewModel>>> =
					eitherDomainErrorOrIoOfBoxedEitherDomainErrorOrCandidateViewModels
							.map { ioOfBoxedEitherDomainErrorOrCandidateViewModels ->
								ioOfBoxedEitherDomainErrorOrCandidateViewModels
										.map{ boxedEitherDomainErrorOrCandidateViewModels: Kind<ForListK, Either<DomainError, CandidateViewModel>> ->
											boxedEitherDomainErrorOrCandidateViewModels.sequence(Either.applicative()).fix()
										}
							}.sequence(IO.applicative())
							.map {
								it.flatten()
							}
			
			val ioOfEitherDomainErrorOrCandidateViewModels: IO<Either<DomainError, List<CandidateViewModel>>> =
					ioOfEitherDomainErrorOrBoxedCandidateViewModels
							.map { eitherDomainErrorOrBoxedCandidateViewModels: Either<DomainError, Kind<ForListK, CandidateViewModel>> ->
								eitherDomainErrorOrBoxedCandidateViewModels
										.map { boxedCandidateViewModels ->
											val candidateViewModels: List<CandidateViewModel> = boxedCandidateViewModels.fix()
											candidateViewModels
										}
							}
			
			ioOfEitherDomainErrorOrCandidateViewModels
		}.flatten()

suspend fun Candidate.toViewModel(): Either<DomainError, CandidateViewModel> =
		Either.catch({ throwable: Throwable ->
			throwable.message?.let { ToViewModelError(it) } ?: ToViewModelError()
		}) {
			CandidateViewModel(name, firstAttendanceAndTurns.last(), firstAttendanceAndTurns.dropLast(1))
		}

fun Pair<Candidate, DetailsOfAlgorithm>.toViewModel(): NextCandidateViewModel {
	return NextCandidateViewModel(this.first.name, this.second)
}
