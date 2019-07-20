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

package com.sparetimedevs.win.algorithm

import com.sparetimedevs.win.model.Candidate
import io.kotlintest.matchers.collections.shouldBeOneOf
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class AmountOfCandidatesDividedByFourEqualsAmountDicesTest : BehaviorSpec({

	Given("list of candidates provided") {
		`when`("nextCandidate") {
			then("good stuff happens") {
				val nextCandidate = AmountOfCandidatesDividedByFourEqualsAmountDices().nextCandidate(candidates)

				nextCandidate shouldBeOneOf candidates
			}

			and( "repeating this a lot of times") {
				then("average out the results") {
					var currentTopCandidate = margaret
					repeat(300) {
						val index = candidates.indexOf(currentTopCandidate)
						candidates.removeAt(index)
						candidates.add(0, currentTopCandidate)

						val nextCandidate = AmountOfCandidatesDividedByFourEqualsAmountDices().nextCandidate(candidates)

						nextCandidate shouldBeOneOf candidates
						nextCandidate shouldNotBe currentTopCandidate

						currentTopCandidate = nextCandidate
					}
				}
			}
		}
	}
}) {
	companion object {
		private val margaret = Candidate(name = "Margaret")
		private val candidates = mutableListOf(
				Candidate(name = "Rose"),
				Candidate(name = "Abbie"),
				Candidate(name = "Tommy"),
				Candidate(name = "Joseph"),
				Candidate(name = "Fani"),
				Candidate(name = "Eden"),
				Candidate(name = "Tiffany"),
				Candidate(name = "Aisha"),
				Candidate(name = "Elsa"),
				Candidate(name = "Ellen"),
				Candidate(name = "Cerys"),
				Candidate(name = "James"),
				Candidate(name = "Kevin"),
				Candidate(name = "William"),
				Candidate(name = "Elle"),
				Candidate(name = "Lois"),
				Candidate(name = "Alexa"),
				Candidate(name = "Kimberley"),
				Candidate(name = "Saffron"),
				Candidate(name = "Penny"),
				Candidate(name = "George"),
				margaret
		)
	}
}