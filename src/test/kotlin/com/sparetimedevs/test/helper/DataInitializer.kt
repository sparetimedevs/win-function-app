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

package com.sparetimedevs.test.helper

import arrow.core.Either
import arrow.core.extensions.list.apply.followedBy
import arrow.fx.coroutines.parTraverse
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.repository.CandidateRepository
import kotlin.coroutines.EmptyCoroutineContext

class DataInitializer(private val candidateRepository: CandidateRepository) {
    
    suspend fun initCandidates(candidates: List<Candidate>): List<Either<DomainError, Candidate>> =
        listOf(candidateRepository.deleteAll())
            .followedBy(insert(candidates))
    
    private suspend fun insert(candidates: List<Candidate>): List<Either<DomainError, Candidate>> =
        candidates.parTraverse(EmptyCoroutineContext, ::save)
    
    private suspend fun save(candidate: Candidate): Either<DomainError, Candidate> =
        candidateRepository.save(candidate)
}
