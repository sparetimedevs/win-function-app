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

package com.sparetimedevs.win

private const val MONGO_DB_CONNECTION_STRING = "MongoDbConnectionString"
private const val DB_NAME = "win_db"

fun getMongoDbConnectionString(): String {
	return System.getenv(MONGO_DB_CONNECTION_STRING)
}

fun getDbName(): String {
	return DB_NAME
}
