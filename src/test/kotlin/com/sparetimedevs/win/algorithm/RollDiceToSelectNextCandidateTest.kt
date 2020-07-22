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

import com.sparetimedevs.test.data.candidates
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe


class RollDiceToSelectNextCandidateTest : BehaviorSpec({
    
    Given("list of candidates provided") {
        `when`("nextCandidate") {
            then("one candidate from the list is returned") {
                val nextCandidate = RollDiceToSelectNextCandidate().nextCandidate(candidates)
                
                nextCandidate.first shouldBeOneOf candidates
            }
            
            and("repeating this a lot of times") {
                then("average out the results") {
                    val mutableListOfCandidates = candidates.toMutableList()
                    var currentTopCandidate = mutableListOfCandidates.first()
                    repeat(300) {
                        val index = mutableListOfCandidates.indexOf(currentTopCandidate)
                        mutableListOfCandidates.removeAt(index)
                        mutableListOfCandidates.add(0, currentTopCandidate)
                        
                        val nextCandidate = RollDiceToSelectNextCandidate().nextCandidate(mutableListOfCandidates)
                        
                        nextCandidate.first shouldBeOneOf mutableListOfCandidates
                        nextCandidate.first shouldNotBe currentTopCandidate
                        
                        currentTopCandidate = nextCandidate.first
                    }
                }
            }
        }
    }
    
    Given("rolling dice") {
        `when`("nextCandidate") {
            then("every dice rolled should have a number in range of one to six") {
                val nextCandidate = RollDiceToSelectNextCandidate().nextCandidate(candidates)
                
                nextCandidate.second.diceEyes.forEach {
                    it shouldBeInRange IntRange(1, 6)
                }
            }
            
            then("the total amount of eyes is equal to the sum of the eyes on all dice") {
                val nextCandidate = RollDiceToSelectNextCandidate().nextCandidate(candidates)
                
                nextCandidate.second.totalEyes shouldBe nextCandidate.second.diceEyes.sum()
            }
        }
    }
})
