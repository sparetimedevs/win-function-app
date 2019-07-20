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

package com.sparetimedevs.test.data

import com.sparetimedevs.win.model.Candidate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

private val date1: Date = Date.from(Instant.now().plus(16L, ChronoUnit.DAYS))
private val date2: Date = Date.from(Instant.now().minus(12L, ChronoUnit.DAYS))
private val date3: Date = Date.from(Instant.now().minus(30L, ChronoUnit.DAYS))
private val date4: Date = Date.from(Instant.now().minus(44L, ChronoUnit.DAYS))
private val date5: Date = Date.from(Instant.now().minus(64L, ChronoUnit.DAYS))
private val date6: Date = Date.from(Instant.now().minus(108L, ChronoUnit.DAYS))
private val candidate1 = Candidate(name = "Rose", turns = listOf(date2, date5, date6))
private val candidate2 = Candidate(name = "Joseph", turns = listOf(date1, date4))
private val candidate3 = Candidate(name = "Tommy")
private val candidate4 = Candidate(name = "Abbie", turns = listOf(date3))
internal val candidates = listOf(candidate1, candidate2, candidate3, candidate4)
