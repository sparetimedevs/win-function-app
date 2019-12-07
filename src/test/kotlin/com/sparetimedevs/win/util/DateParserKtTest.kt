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

package com.sparetimedevs.win.util

import com.sparetimedevs.win.model.DomainError
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class DateParserKtTest : BehaviorSpec({
    
    given("parse date is called") {
        `when`("operating on a valid date string") {
            then("returns correct date object") {
                "20190831".parseDate().fold(
                        {
                            fail("This test case should yield a Right.")
                        },
                        {
                            it.toInstant().epochSecond shouldBe 1567202400L
                        }
                )
            }
        }
        
        `when`("operating on an invalid date string") {
            then("returns correct date object") {
                "boom".parseDate().fold(
                        {
                            it.shouldBeInstanceOf<DomainError.DateParseError>()
                        },
                        {
                            fail("This test case should yield a Left.")
                        }
                )
            }
        }
    }
})
