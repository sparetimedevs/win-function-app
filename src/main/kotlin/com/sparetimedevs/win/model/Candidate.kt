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

@file:JvmName("UniqueCandidateFileName")

package com.sparetimedevs.win.model

import arrow.optics.extensions.list.cons.cons
import arrow.optics.optics
import com.sparetimedevs.win.util.fromDatabaseDateFormat
import org.bson.types.ObjectId
import java.time.OffsetDateTime

typealias Name = String

@optics
data class Candidate(
    val id: ObjectId = ObjectId(),
    val name: Name,
    val firstAttendanceAndTurns: List<OffsetDateTime>
) {
    companion object
}

private val candidateFirstAttendanceAndTurns = Candidate.firstAttendanceAndTurns

private fun getCandidateFirstAttendanceAndTurns(candidate: Candidate) = candidateFirstAttendanceAndTurns.get(candidate)

fun Candidate.addTurn(date: OffsetDateTime): Candidate =
    candidateFirstAttendanceAndTurns.modify(this) { date.cons(getCandidateFirstAttendanceAndTurns(this)) }

fun CandidateEntity.toCandidate(): Candidate =
    Candidate(id = id, name = name, firstAttendanceAndTurns = firstAttendanceAndTurns.map { it.fromDatabaseDateFormat() })
