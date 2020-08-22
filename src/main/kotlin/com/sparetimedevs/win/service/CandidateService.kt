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

package com.sparetimedevs.win.service

import arrow.core.Either
import arrow.core.extensions.either.functor.unit
import arrow.core.flatMap
import arrow.fx.coroutines.parTraverse
import com.sparetimedevs.win.algorithm.CandidateAlgorithm
import com.sparetimedevs.win.algorithm.DetailsOfAlgorithm
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.model.addTurn
import com.sparetimedevs.win.repository.CandidateRepository
import com.sparetimedevs.win.trigger.defaultSorting
import java.time.OffsetDateTime
import kotlin.coroutines.EmptyCoroutineContext

class CandidateService(
    private val candidateAlgorithm: CandidateAlgorithm,
    private val candidateRepository: CandidateRepository
) {
    
    suspend fun getAllCandidates(): Either<DomainError, List<Candidate>> =
        candidateRepository.findAll(defaultSorting)
    
    suspend fun determineNextCandidate(): Either<DomainError, Pair<Candidate, DetailsOfAlgorithm>> =
        getAllCandidates()
            .map {
                candidateAlgorithm.nextCandidate(it)
            }
    
    suspend fun addDateToCandidate(name: String, date: OffsetDateTime): Either<DomainError, Candidate> =
        candidateRepository.findOneByName(name)
            .flatMap {
                candidateRepository.update(it.id, it.addTurn(date))
            }
    
    suspend fun addAll(candidates: List<Candidate>): List<Either<DomainError, Candidate>> =
        candidates.parTraverse(EmptyCoroutineContext, candidateRepository::save)
    
    suspend fun deleteAll(): Either<DomainError, Unit> =
        candidateRepository.deleteAll().unit()
}
