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

import arrow.fx.IO
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

	fun getAllCandidates(): IO<List<Candidate>> =
			candidateRepository.findAll(defaultSorting)
	
	fun determineNextCandidate(): IO<Pair<Candidate, DetailsOfAlgorithm>> =
			getAllCandidates()
					.map {
						candidateAlgorithm.nextCandidate(it)
					}
	
	fun addDateToCandidate(name: String, date: Date): IO<Candidate> =
			candidateRepository.findOneByName(name)
					.flatMap {
						val turns = it.firstAttendanceAndTurns.toMutableList()
						turns.add(0, date)
						candidateRepository.update(it.id, it.copy(firstAttendanceAndTurns = turns))
					}
}