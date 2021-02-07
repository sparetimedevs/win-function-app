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

package com.sparetimedevs.win.util

import arrow.core.Either
import arrow.core.Left
import arrow.core.left
import arrow.core.right
import com.sparetimedevs.test.data.candidateTiffany
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockkStatic

class TransformToResponseKtTest : BehaviorSpec({
    
    given("a call toResponse") {
        `when`("the Either contains a List of Candidates") {
            then("the Candidates get transformed to CandidateResponses") {
                val eitherContainingCandidates = candidates.right()
                
                val result = eitherContainingCandidates.toResponse()
                
                result.fold(
                    {
                        fail("This test case should yield a Right.")
                    },
                    {
                        it.forEachIndexed { index, candidateResponse ->
                            candidateResponse.name shouldBe candidates[index].name
                            candidateResponse.firstAttendance shouldBe candidates[index].firstAttendanceAndTurns.last().toDateFormattedString()
                            candidateResponse.turns shouldBe candidates[index].firstAttendanceAndTurns.dropLast(1).map { turn -> turn.toDateFormattedString() }
                        }
                    }
                )
            }
            
            and("one of the Candidate to CandidateResponse transformations results in a ToResponseError") {
                then("the ToResponseError is returned") {
                    mockkStatic("com.sparetimedevs.win.util.TransformToResponseKt")
                    val eitherContainingCandidates = candidates.right()
                    
                    coEvery { candidateTiffany.toResponse() } returns Left(DomainError.ToResponseError())
                    
                    val result = eitherContainingCandidates.toResponse()
                    
                    result.fold(
                        {
                            it.shouldBeInstanceOf<DomainError.ToResponseError>()
                        },
                        {
                            fail("This test case should yield a Left.")
                        }
                    )
                }
            }
        }
        
        `when`("the Either contains a ServiceUnavailable") {
            then("the ServiceUnavailable is returned") {
                val eitherContainingDomainError: Either<DomainError, List<Candidate>> = DomainError.ServiceUnavailable().left()
                
                val result = eitherContainingDomainError.toResponse()
                
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
