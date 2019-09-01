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

package com.sparetimedevs.win.service

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.win.algorithm.CandidateAlgorithm
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.repository.CandidateRepository
import com.sparetimedevs.win.trigger.defaultSorting
import java.util.Date

class CandidateService(
		private val candidateAlgorithm: CandidateAlgorithm,
		private val candidateRepository: CandidateRepository
) {

	fun getAllCandidates(): IO<Either<Error, List<Candidate>>> =
			IO.fx {
				!effect { candidateRepository.findAll(defaultSorting) }
			}

	fun determineNextCandidate(): IO<Either<Error, Pair<Candidate, DetailsOfAlgorithm>>> =
			IO.fx {
				val candidates = !getAllCandidates()
				candidates.fold(
						{
							Left(it)
						},
						{
							Right(candidateAlgorithm.nextCandidate(it))
						}
				)
			}

	fun addDateToCandidate(name: String, date: Date): IO<Either<Error, Candidate>> =
			IO.fx {
				val candidate = !effect { candidateRepository.findOneByName(name) }
				candidate.fold(
						{
							Left(it)
						},
						{
							val turns = it.turns.toMutableList()
							turns.add(0, date)
							!effect { candidateRepository.update(it.id, it.copy(turns = turns)) }
						}
				)
			}
}
