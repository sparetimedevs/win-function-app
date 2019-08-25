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

package com.sparetimedevs.win.trigger

import com.mongodb.client.model.Sorts
import org.bson.conversions.Bson

const val CONTENT_TYPE = "Content-Type"
const val CONTENT_TYPE_APPLICATION_JSON = "application/json"
const val ERROR_MESSAGE = "An error occurred while find all candidates. The error is: "
const val TURNS_FIELD = "turns"
const val NAME_FIELD = "name"
private val sortByTurnsFieldDescending: Bson = Sorts.descending(TURNS_FIELD)
private val sortByNameFieldAscending: Bson = Sorts.ascending(NAME_FIELD)
val defaultSorting: Bson = Sorts.orderBy(sortByTurnsFieldDescending, sortByNameFieldAscending)
