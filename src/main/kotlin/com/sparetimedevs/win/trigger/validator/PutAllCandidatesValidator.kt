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

package com.sparetimedevs.win.trigger.validator

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.fix
import arrow.core.invalid
import arrow.core.valid
import com.google.gson.reflect.TypeToken
import com.microsoft.azure.functions.HttpRequestMessage
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.CandidateInput
import com.sparetimedevs.win.model.DomainError
import com.sparetimedevs.win.util.flatMap
import com.sparetimedevs.win.util.fromJson
import com.sparetimedevs.win.util.parallelValidate
import com.sparetimedevs.win.util.parseDate
import java.time.OffsetDateTime

suspend fun HttpRequestMessage<String?>.validateAllCandidatesInput(): Validated<DomainError, List<Candidate>> =
    if (this.body.isNullOrBlank()) DomainError.ValidationError("The body was empty").invalid()
    else {
        CandidatesToken().fromJson(this.body)
            .flatMap { candidatesInput: List<CandidateInput> ->
                candidatesInput.map { candidateInput: CandidateInput ->
                    parallelValidate(
                        validateName(candidateInput.name),
                        validateFirstAttendance(candidateInput.firstAttendance),
                        validateTurns(candidateInput.turns)
                    ) { name: String, firstAttendance: OffsetDateTime, turns: List<OffsetDateTime> ->
                        Candidate(name = name, firstAttendanceAndTurns = turns + listOf(firstAttendance))
                    }
                }
                    .sequence(Validated.applicative(NonEmptyList.semigroup<DomainError.ValidationError>())).fix()
                    .map { it.fix() }
                    .mapLeft { DomainError.ValidationError("One or more fields were not supplied correctly.") }
            }
    }

private fun validateName(name: String?): Validated<DomainError.ValidationError, String> =
    if (name.isNullOrBlank()) DomainError.ValidationError("Name should not be null or blank.").invalid()
    else name.valid()

private suspend fun validateFirstAttendance(firstAttendance: String?): Validated<DomainError.ValidationError, OffsetDateTime> =
    if (firstAttendance.isNullOrBlank()) DomainError.ValidationError("First attendance date was not supplied.").invalid()
    else {
        firstAttendance.parseDate()
            .fold(
                {
                    DomainError.ValidationError("First attendance date was not supplied in the right format.").invalid()
                },
                {
                    it.valid()
                }
            )
    }

private suspend fun validateTurns(turns: List<String>?): Validated<DomainError.ValidationError, List<OffsetDateTime>> =
    if (turns.isNullOrEmpty()) emptyList<OffsetDateTime>().valid()
    else {
        turns.map { firstAttendance ->
            firstAttendance.parseDate()
                .fold(
                    {
                        DomainError.ValidationError("Turn date was not supplied in the right format.").invalid().toValidatedNel()
                    },
                    {
                        it.valid()
                    }
                )
        }
            .sequence(Validated.applicative(NonEmptyList.semigroup<DomainError.ValidationError>())).fix()
            .map { it.fix() }
            .mapLeft { DomainError.ValidationError("One or turn dates were not supplied in the right format.") }
    }

private class CandidatesToken : TypeToken<List<CandidateInput>>()
