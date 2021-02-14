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

package com.sparetimedevs.win.trigger.validator

import com.microsoft.azure.functions.HttpRequestMessage
import com.sparetimedevs.test.helper.shouldBeInvalidAndContain
import com.sparetimedevs.test.helper.shouldBeValidAndContainExactly
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class PutAllCandidatesValidatorKtTest : BehaviorSpec({
    
    given("valid input") {
        `when`("validateAllCandidatesInput") {
            then("returns valid candidates") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_AS_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeValidAndContainExactly EXPECTED_CANDIDATES
            }
        }
    }
    
    given("some candidates without a name") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_SOME_NAMES_AS_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("One or more fields were not supplied correctly.")
            }
        }
    }
    
    given("some candidates without a first attendance date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_SOME_FIRST_ATTENDANCES_MISSING_AS_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("One or more fields were not supplied correctly.")
            }
        }
    }
    
    given("some candidates with a wrong date format as first attendance date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_FIRST_ATTENDANCE_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("One or more fields were not supplied correctly.")
            }
        }
    }
    
    given("some candidates without turns") {
        `when`("validateAllCandidatesInput") {
            then("returns valid candidates") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITHOUT_TURNS_AS_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeValidAndContainExactly EXPECTED_CANDIDATES
            }
        }
    }
    
    given("some candidates with a wrong date format as turn date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_TURN_JSON_STRING
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("One or more fields were not supplied correctly.")
            }
        }
    }
    
    given("null as request body") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns null
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("The body was empty")
            }
        }
    }
    
    given("empty string as request body") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns ""
                
                request.validateAllCandidatesInput() shouldBeInvalidAndContain DomainError.ValidationError("The body was empty")
            }
        }
    }
}) {
    companion object {
        
        private val date1: OffsetDateTime =
            LocalDate.parse("2020-09-11", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date2: OffsetDateTime =
            LocalDate.parse("2020-10-07", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date3: OffsetDateTime =
            LocalDate.parse("2020-11-20", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date4: OffsetDateTime =
            LocalDate.parse("2020-12-10", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date5: OffsetDateTime =
            LocalDate.parse("2020-12-24", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date6: OffsetDateTime =
            LocalDate.parse("2021-01-11", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        private val date7: OffsetDateTime =
            LocalDate.parse("2021-02-08", DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))).atStartOfDay().atOffset(ZoneOffset.UTC).plusHours(12)
        
        private val EXPECTED_CANDIDATES = listOf(
            Candidate(name = "Joseph", firstAttendanceAndTurns = listOf(date7, date4, date1)),
            Candidate(name = "James", firstAttendanceAndTurns = listOf(date6)),
            Candidate(name = "Rose", firstAttendanceAndTurns = listOf(date6, date3, date2, date1)),
            Candidate(name = "Abbie", firstAttendanceAndTurns = listOf(date5, date1)),
            Candidate(name = "Elsa", firstAttendanceAndTurns = listOf(date5)),
            Candidate(name = "Aisha", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Alexa", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Cerys", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Eden", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Elle", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Ellen", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Fani", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "George", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Kevin", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Kimberley", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Lois", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Margaret", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Penny", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Saffron", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Tiffany", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "Tommy", firstAttendanceAndTurns = listOf(date1)),
            Candidate(name = "William", firstAttendanceAndTurns = listOf(date1))
        )
        
        private const val CANDIDATES_AS_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "2020-12-10"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "2021-01-11",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "2020-11-20",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "name": "Elsa",
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tommy",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
        
        private const val CANDIDATES_WITH_SOME_NAMES_AS_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "2020-12-10"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "2021-01-11",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "2020-11-20",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
        
        private const val CANDIDATES_WITH_SOME_FIRST_ATTENDANCES_MISSING_AS_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "2020-12-10"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "2021-01-11",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "2020-11-20",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "name": "Elsa",
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tommy",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
        
        private const val CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_FIRST_ATTENDANCE_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "2020-12-10"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "11-01-2021",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "2020-11-20",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "name": "Elsa",
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "11-09-2020",
        "turns": []
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tommy",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
        
        private const val CANDIDATES_WITHOUT_TURNS_AS_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "2020-12-10"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "2021-01-11",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "2020-11-20",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "name": "Elsa",
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "2020-09-11"
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": null
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tommy",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
        
        private const val CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_TURN_JSON_STRING =
            """[
    {
        "name": "Joseph",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-02-08",
            "10-12-2020"
        ]
    },
    {
        "name": "James",
        "firstAttendance": "2021-01-11",
        "turns": []
    },
    {
        "name": "Rose",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2021-01-11",
            "20-11-2020",
            "2020-10-07"
        ]
    },
    {
        "name": "Abbie",
        "firstAttendance": "2020-09-11",
        "turns": [
            "2020-12-24"
        ]
    },
    {
        "name": "Elsa",
        "firstAttendance": "2020-12-24",
        "turns": []
    },
    {
        "name": "Aisha",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Alexa",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Cerys",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Eden",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Elle",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Ellen",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Fani",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "George",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kevin",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Kimberley",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Lois",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Margaret",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Penny",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Saffron",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tiffany",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "Tommy",
        "firstAttendance": "2020-09-11",
        "turns": []
    },
    {
        "name": "William",
        "firstAttendance": "2020-09-11",
        "turns": []
    }
]"""
    }
}
