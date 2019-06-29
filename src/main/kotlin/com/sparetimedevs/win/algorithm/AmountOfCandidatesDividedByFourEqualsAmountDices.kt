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
import com.sparetimedevs.win.model.D6
import java.lang.Math.ceil

class AmountOfCandidatesDividedByFourEqualsAmountDices : CandidateAlgorithm {

	override fun nextCandidate(candidates: List<Candidate>): Candidate {
		val amountOfDices = ceil(candidates.size.toDouble().div(4L)).toInt()
		var countEyes = 0
		for (i: Int in 0 until amountOfDices) {
			countEyes = countEyes.plus(D6.roll())
		}
		return when {
			countEyes < candidates.size -> candidates[countEyes - 1]
			else -> candidates.last()
		}
	}
}
