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

internal val date1: Date = Date.from(Instant.now().plus(16L, ChronoUnit.DAYS))
internal val date2: Date = Date.from(Instant.now().minus(12L, ChronoUnit.DAYS))
internal val date3: Date = Date.from(Instant.now().minus(30L, ChronoUnit.DAYS))
internal val date4: Date = Date.from(Instant.now().minus(44L, ChronoUnit.DAYS))
internal val date5: Date = Date.from(Instant.now().minus(64L, ChronoUnit.DAYS))
internal val date6: Date = Date.from(Instant.now().minus(108L, ChronoUnit.DAYS))

internal val candidateRose = Candidate(name = "Rose", turns = listOf(date2, date5, date6))
internal val candidateAbbie = Candidate(name = "Abbie", turns = listOf(date3))
internal val candidateTommy = Candidate(name = "Tommy")
internal val candidateJoseph = Candidate(name = "Joseph", turns = listOf(date1, date4))
internal val candidateFani = Candidate(name = "Fani")
internal val candidateEden = Candidate(name = "Eden")
internal val candidateTiffany = Candidate(name = "Tiffany")
internal val candidateAisha = Candidate(name = "Aisha")
internal val candidateElsa = Candidate(name = "Elsa")
internal val candidateEllen = Candidate(name = "Ellen")
internal val candidateCerys = Candidate(name = "Cerys")
internal val candidateJames = Candidate(name = "James")
internal val candidateKevin = Candidate(name = "Kevin")
internal val candidateWilliam = Candidate(name = "William")
internal val candidateElle = Candidate(name = "Elle")
internal val candidateLois = Candidate(name = "Lois")
internal val candidateAlexa = Candidate(name = "Alexa")
internal val candidateKimberley = Candidate(name = "Kimberley")
internal val candidateSaffron = Candidate(name = "Saffron")
internal val candidatePenny = Candidate(name = "Penny")
internal val candidateGeorge = Candidate(name = "George")
internal val candidateMargaret = Candidate(name = "Margaret")

internal val candidates = listOf(
		candidateRose,
		candidateAbbie,
		candidateTommy,
		candidateJoseph,
		candidateFani,
		candidateEden,
		candidateTiffany,
		candidateAisha,
		candidateElsa,
		candidateEllen,
		candidateCerys,
		candidateJames,
		candidateKevin,
		candidateWilliam,
		candidateElle,
		candidateLois,
		candidateAlexa,
		candidateKimberley,
		candidateSaffron,
		candidatePenny,
		candidateGeorge,
		candidateMargaret
)
