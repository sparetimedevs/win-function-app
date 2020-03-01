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

import arrow.core.extensions.list.traverse.traverse
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import arrow.fx.followedBy
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.repository.CandidateRepository

class DataInitializer(private val candidateRepository: CandidateRepository) {
    
    fun initCandidates(candidates: List<Candidate>): IO<DomainError, List<Candidate>> {
        return candidateRepository.deleteAll()
                .followedBy(insert(candidates))
    }
    
    private fun insert(candidates: List<Candidate>): IO<DomainError, List<Candidate>> =
            candidates.traverse(IO.applicative(), ::save).fix()
                    .map {
                        it.fix()
                    }
    
    private fun save(candidate: Candidate): IO<DomainError, Candidate> =
            candidateRepository.save(candidate)
}
