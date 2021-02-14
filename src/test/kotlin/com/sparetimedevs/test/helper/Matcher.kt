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

package com.sparetimedevs.test.helper

import arrow.core.Validated
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

infix fun <E : DomainError, A : List<Candidate>> Validated<E, A>.shouldBeValidAndContainExactly(expectedCandidates: List<Candidate>): Unit =
    this.fold(
        {
            fail("This test case should yield a Valid.")
        },
        { actualCandidates: List<Candidate> ->
            actualCandidates.map { it.name to it.firstAttendanceAndTurns }.shouldContainExactly(expectedCandidates.map { it.name to it.firstAttendanceAndTurns })
        }
    )

infix fun <E : DomainError, A> Validated<E, A>.shouldBeInvalidAndContain(expectedError: DomainError): Unit =
    this.fold(
        { actualError ->
            actualError shouldBe expectedError
        },
        {
            fail("This test case should yield an Invalid.")
        }
    )
