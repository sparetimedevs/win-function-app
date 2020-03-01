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

import arrow.core.Left
import arrow.fx.IO
import com.sparetimedevs.test.data.candidateTiffany
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockkStatic

class ToViewModelKtTest : BehaviorSpec({
    
    given("a call toViewModel") {
        `when`("the IO contains a List of Candidates") {
            then("the Candidates get transformed to CandidateViewModels") {
                val ioContainingCandidates = IO.just(candidates)
                
                val result = ioContainingCandidates.toViewModels().unsafeRunSyncEither()
                
                result.fold(
                        {
                            fail("This test case should yield a Right.")
                        },
                        {
                            it.forEachIndexed { index, candidateViewModel ->
                                candidateViewModel.name shouldBe candidates[index].name
                                candidateViewModel.firstAttendance shouldBe candidates[index].firstAttendanceAndTurns.last()
                                candidateViewModel.turns shouldBe candidates[index].firstAttendanceAndTurns.dropLast(1)
                            }
                        }
                )
            }
            
            and("one of the Candidate to CandidateViewModel transformations results in a ToViewModelError") {
                then("the ToViewModelError is returned") {
                    mockkStatic("com.sparetimedevs.win.util.ToViewModelKt")
                    val ioContainingCandidates = IO.just(candidates)
                    
                    coEvery { candidateTiffany.toViewModel() } returns Left(DomainError.ToViewModelError())
    
                    val result = ioContainingCandidates.toViewModels().unsafeRunSyncEither()
                    
                    result.fold(
                            {
                                it.shouldBeInstanceOf<DomainError.ToViewModelError>()
                            },
                            {
                                fail("This test case should yield a Left.")
                            }
                    )
                }
            }
        }
        
        `when`("the Either in the IO contains a ServiceUnavailable") {
            then("the ServiceUnavailable is returned") {
                val ioContainingDomainError = IO.raiseError<DomainError, List<Candidate>>(DomainError.ServiceUnavailable())
                
                val result = ioContainingDomainError.toViewModels().unsafeRunSyncEither()
                
                result.fold(
                        {
                            it.shouldBeInstanceOf<DomainError.ServiceUnavailable>()
                        },
                        {
                            fail("This test case should yield a Left.")
                        }
                )
            }
        }
    }
})
