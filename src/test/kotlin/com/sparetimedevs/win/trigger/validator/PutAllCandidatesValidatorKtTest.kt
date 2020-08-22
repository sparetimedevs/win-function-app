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

import arrow.core.Validated
import com.microsoft.azure.functions.HttpRequestMessage
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.DomainError
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class PutAllCandidatesValidatorKtTest : BehaviorSpec({
    
    given("valid input") {
        `when`("validateAllCandidatesInput") {
            then("returns valid candidates") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_AS_JSON_STRING
                
                val result: Validated<DomainError, List<Candidate>> = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        fail("Test case should yield a valid result.")
                    },
                    {
                        it.size shouldBe 22
                    }
                )
            }
        }
    }
    
    given("some candidates without a name") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_SOME_NAMES_AS_JSON_STRING
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("One or more fields were not supplied correctly.")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
    
    given("some candidates without a first attendance date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_SOME_FIRST_ATTENDANCES_MISSING_AS_JSON_STRING
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("One or more fields were not supplied correctly.")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
    
    given("some candidates with a wrong date format as first attendance date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_FIRST_ATTENDANCE_JSON_STRING
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("One or more fields were not supplied correctly.")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
    
    given("some candidates without turns") {
        `when`("validateAllCandidatesInput") {
            then("returns valid candidates") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITHOUT_TURNS_AS_JSON_STRING
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        fail("Test case should yield a valid result.")
                    },
                    {
                        it.size shouldBe 22
                    }
                )
            }
        }
    }
    
    given("some candidates with a wrong date format as turn date") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns CANDIDATES_WITH_WRONG_DATE_FORMAT_AS_TURN_JSON_STRING
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("One or more fields were not supplied correctly.")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
    
    given("null as request body") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns null
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("The body was empty")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
    
    given("empty string as request body") {
        `when`("validateAllCandidatesInput") {
            then("returns invalid with ValidationError") {
                val request = mockk<HttpRequestMessage<String?>>()
                every { request.body } returns ""
                
                val result = request.validateAllCandidatesInput()
                
                result.fold(
                    {
                        it shouldBe DomainError.ValidationError("The body was empty")
                    },
                    {
                        fail("Test case should yield an invalid result.")
                    }
                )
            }
        }
    }
}) {
    companion object {
        
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
