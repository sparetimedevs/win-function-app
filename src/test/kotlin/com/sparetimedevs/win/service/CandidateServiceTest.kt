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

import arrow.core.Left
import arrow.core.Right
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.test.data.candidateLois
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.algorithm.CandidateAlgorithm
import com.sparetimedevs.win.algorithm.DetailsOfRolledDice
import com.sparetimedevs.win.repository.CandidateRepository
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.Date

class CandidateServiceTest : BehaviorSpec({

	val candidateAlgorithm = mockk<CandidateAlgorithm>()
	val candidateRepository = mockk<CandidateRepository>()
	val candidateService = CandidateService(candidateAlgorithm, candidateRepository)

	given("get all candidates is called") {
		`when`("database is reachable") {
			then( "returns all candidates") {
				val eitherContainingCandidates = Right(candidates)

				coEvery { candidateRepository.findAll(any()) } returns eitherContainingCandidates

				unsafe { runBlocking { candidateService.getAllCandidates() } }.fold(
						{ fail("This test case should yield a Right.") },
						{ it shouldContainAll candidates }
				)
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				val eitherContainingError = Left(Error.ServiceUnavailable())

				coEvery { candidateRepository.findAll(any()) } returns eitherContainingError

				unsafe { runBlocking { candidateService.getAllCandidates() } }.fold(
						{ it.message shouldBe Error.ServiceUnavailable().message },
						{ fail("This test case should yield a Left.") }
				)
			}
		}
	}

	given("determine next candidate is called") {
		`when`("database is reachable") {
			then( "returns next candidate's name") {
				val eitherContainingCandidates = Right(candidates)
				val detailsOfRolledDice = DetailsOfRolledDice(listOf(3, 1, 5, 1, 2, 4))

				coEvery { candidateRepository.findAll(any()) } returns eitherContainingCandidates
				every { candidateAlgorithm.nextCandidate(any()) } returns (candidateLois to detailsOfRolledDice)

				unsafe { runBlocking { candidateService.determineNextCandidate() } }.fold(
						{ fail("This test case should yield a Right.") },
						{ it shouldBe (candidateLois to detailsOfRolledDice) }
				)
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				val eitherContainingError = Left(Error.ServiceUnavailable())

				coEvery { candidateRepository.findAll(any()) } returns eitherContainingError

				unsafe { runBlocking { candidateService.getAllCandidates() } }.fold(
						{ it.message shouldBe Error.ServiceUnavailable().message },
						{ fail("This test case should yield a Left.") }
				)
			}
		}
	}

	given("add date to candidate is called") {
		`when`("database is reachable") {
			then( "returns candidate") {
				val eitherContainingCandidateLois = Right(candidateLois)
				val date = Date.from(Instant.ofEpochSecond(1567202400L))

				coEvery { candidateRepository.findOneByName(any()) } returns eitherContainingCandidateLois
				coEvery { candidateRepository.update(any(), any()) } returns eitherContainingCandidateLois

				unsafe { runBlocking { candidateService.addDateToCandidate(candidateLois.name, date) } }.fold(
						{ fail("This test case should yield a Right.") },
						{ it shouldBe  candidateLois }
				)
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				val eitherContainingError = Left(Error.ServiceUnavailable())

				coEvery { candidateRepository.findOneByName(any()) } returns eitherContainingError

				unsafe { runBlocking { candidateService.getAllCandidates() } }.fold(
						{ it.message shouldBe Error.ServiceUnavailable().message },
						{ fail("This test case should yield a Left.") }
				)
			}
		}
	}
})
