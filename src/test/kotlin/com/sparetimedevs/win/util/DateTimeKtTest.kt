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

package com.sparetimedevs.win.util

import com.sparetimedevs.win.model.DomainError
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

class DateTimeKtTest : BehaviorSpec({
    
    given("parse date is called") {
        `when`("operating on a valid date string") {
            then("returns correct date object") {
                "2019-08-31".parseDate().fold(
                    {
                        fail("This test case should yield a Right.")
                    },
                    {
                        it.toInstant().epochSecond shouldBe 1567252800L
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
    
    given("toDateFormattedString is called") {
        `when`("operating on a OffsetDateTime") {
            then("returns correct formatted date string") {
                val result = OFFSET_DATE_TIME.toDateFormattedString()
                
                result shouldBe "2021-01-31"
            }
        }
    }
    
    given("toDatabaseDateFormat is called") {
        `when`("operating on a OffsetDateTime") {
            then("returns correct Date") {
                val result = OFFSET_DATE_TIME.toDatabaseDateFormat()
                
                result shouldBe Date(EPOCH_SECOND * 1000)
            }
        }
    }
    
    given("fromDatabaseDateFormat is called") {
        `when`("operating on a OffsetDateTime") {
            then("returns correct OffsetDateTime") {
                val result = Date(EPOCH_SECOND * 1000).fromDatabaseDateFormat()
                
                result shouldBe OFFSET_DATE_TIME
            }
        }
    }
}) {
    companion object {
        private const val EPOCH_SECOND = 1612106767L
        private val OFFSET_DATE_TIME: OffsetDateTime = Instant.ofEpochSecond(EPOCH_SECOND).atOffset(ZoneOffset.UTC)
        
    }
}
