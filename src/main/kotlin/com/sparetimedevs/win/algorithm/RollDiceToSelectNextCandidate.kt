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

package com.sparetimedevs.win.algorithm

import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.D6
import kotlin.math.ceil

class RollDiceToSelectNextCandidate : CandidateAlgorithm {
    
    override fun nextCandidate(candidates: List<Candidate>): Pair<Candidate, DetailsOfRolledDice> {
        val amountOfDice = ceil(candidates.size.toDouble().div(4)).toInt()
        val diceEyes = List(amountOfDice) { D6.roll() }
        val resultOfRolledDice = DetailsOfRolledDice(diceEyes)
        
        return when {
            resultOfRolledDice.totalEyes < candidates.size -> candidates[resultOfRolledDice.totalEyes - 1] to resultOfRolledDice
            else -> candidates.last() to resultOfRolledDice
        }
    }
}

data class DetailsOfRolledDice(val diceEyes: List<Int>, val totalEyes: Int = diceEyes.sum()) : DetailsOfAlgorithm
