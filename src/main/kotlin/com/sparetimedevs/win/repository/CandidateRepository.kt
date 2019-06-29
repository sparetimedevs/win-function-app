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

package com.sparetimedevs.win.repository

import arrow.core.Either
import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.crud.createOne
import com.sparetimedevs.suspendmongo.crud.deleteAll
import com.sparetimedevs.suspendmongo.crud.deleteOne
import com.sparetimedevs.suspendmongo.crud.readAll
import com.sparetimedevs.suspendmongo.crud.readOne
import com.sparetimedevs.suspendmongo.crud.updateOne
import com.sparetimedevs.suspendmongo.getCollection
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.win.model.Candidate
import org.bson.types.ObjectId

class CandidateRepository(database: Database) {

	private val collection = getCollection<Candidate>(database)

	suspend fun findAll(): Either<Error, List<Candidate>> = collection.readAll().toEither()

	suspend fun findOneById(id: ObjectId): Either<Error, Candidate> = collection.readOne(id).toEither()

	suspend fun deleteAll(): Either<Error, Boolean> = collection.deleteAll().toEither()

	suspend fun deleteOneById(id: ObjectId): Either<Error, Candidate> = collection.deleteOne(id).toEither()

	suspend fun save(candidate: Candidate): Either<Error, Candidate> = collection.createOne(candidate).toEither()

	suspend fun update(id: ObjectId, candidate: Candidate): Either<Error, Candidate> = collection.updateOne(id, candidate).toEither()
}
