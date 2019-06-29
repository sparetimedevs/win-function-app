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

package com.sparetimedevs.testhelper

import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.repository.CandidateRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class DataInitializer(private val candidateRepository: CandidateRepository) {

	suspend fun initData(): Unit = coroutineScope<Unit> {

		candidateRepository.deleteAll()

		launch {
			candidateRepository.save(
					Candidate(
							name = "Me Is Cool",
							date = null,
							listOfDates = emptyList()
					)
			)
		}
		launch {
			candidateRepository.save(
					Candidate(
							name = "He Less Cool",
							date = null,
							listOfDates = emptyList()
					)
			)
		}
		launch {
			val nextDate = Date.from(Instant.now().plus(14L, ChronoUnit.DAYS))
			val oldDate = Date.from(Instant.now().minus(42L, ChronoUnit.DAYS))
			candidateRepository.save(
					Candidate(
							name = "She Is next",
							date = nextDate,
							listOfDates = listOf(nextDate, oldDate)
					)
			)
		}
	}
}
