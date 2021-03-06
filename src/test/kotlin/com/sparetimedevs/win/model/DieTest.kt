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

package com.sparetimedevs.win.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeInRange

class DieTest : BehaviorSpec({
    
    given("a D6") {
        `when`("roll") {
            then("returns a number in range of one to six") {
                repeat(300) {
                    D6.roll() shouldBeInRange IntRange(1, 6)
                }
            }
        }
    }
})
