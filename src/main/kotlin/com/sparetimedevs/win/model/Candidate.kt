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

package com.sparetimedevs.win.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.util.Date

typealias Name = String

data class Candidate @BsonCreator constructor(
		@BsonId val id: ObjectId = ObjectId(),
		@BsonProperty("name") val name: Name,
		@BsonProperty("turns") val turns: List<Date> = emptyList()
) {
	fun toViewModel(): CandidateViewModel {
		return CandidateViewModel(name, turns)
	}
}

fun List<Candidate>.toViewModel(): List<CandidateViewModel> =
		this.map { candidate -> candidate.toViewModel() }.toCollection(ArrayList())
