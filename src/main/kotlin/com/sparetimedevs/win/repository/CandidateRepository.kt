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

import arrow.fx.IO
import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.crud.createOne
import com.sparetimedevs.suspendmongo.crud.deleteAll
import com.sparetimedevs.suspendmongo.crud.deleteOne
import com.sparetimedevs.suspendmongo.crud.readAll
import com.sparetimedevs.suspendmongo.crud.readOne
import com.sparetimedevs.suspendmongo.crud.updateOne
import com.sparetimedevs.suspendmongo.getCollection
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.util.flattenRaisingError
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class CandidateRepository(database: Database) {
    
    private val collection = getCollection<Candidate>(database)
    
    fun findAll(): IO<List<Candidate>> =
            IO.effect {
                collection.readAll().toEither()
            }.flattenRaisingError()
    
    fun findAll(sort: Bson): IO<List<Candidate>> =
            IO.effect {
                collection.readAll(sort).toEither()
            }.flattenRaisingError()
    
    fun findOneByName(name: String): IO<Candidate> =
            IO.effect {
                collection.readOne("name" to name).toEither()
            }.flattenRaisingError()
    
    fun deleteAll(): IO<Boolean> =
            IO.effect {
                collection.deleteAll().toEither()
            }.flattenRaisingError()
    
    fun deleteOneById(id: ObjectId): IO<Candidate> =
            IO.effect {
                collection.deleteOne(id).toEither()
            }.flattenRaisingError()
    
    fun save(candidate: Candidate): IO<Candidate> =
            IO.effect {
                collection.createOne(candidate).toEither()
            }.flattenRaisingError()
    
    fun update(id: ObjectId, candidate: Candidate): IO<Candidate> =
            IO.effect {
                collection.updateOne(id, candidate).toEither()
            }.flattenRaisingError()
}
