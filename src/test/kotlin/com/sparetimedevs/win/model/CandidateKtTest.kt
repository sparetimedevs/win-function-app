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

import com.sparetimedevs.test.data.candidates
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class CandidateKtTest : BehaviorSpec({

	given("a list of candidates") {
		`when`("toViewModel") {
			then("returns a list of candidate view models") {
				val candidateViewModels = candidates.toViewModel()
				candidateViewModels[0].name shouldBe candidates[0].name
				candidateViewModels[0].turns shouldBe candidates[0].turns
				candidateViewModels[1].name shouldBe candidates[1].name
				candidateViewModels[1].turns shouldBe candidates[1].turns
				candidateViewModels[2].name shouldBe candidates[2].name
				candidateViewModels[2].turns shouldBe candidates[2].turns
				candidateViewModels[3].name shouldBe candidates[3].name
				candidateViewModels[3].turns shouldBe candidates[3].turns
			}
		}
	}
})
