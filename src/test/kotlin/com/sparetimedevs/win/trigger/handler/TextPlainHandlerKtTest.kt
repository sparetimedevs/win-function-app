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

package com.sparetimedevs.win.trigger.handler

import arrow.core.getOrHandle
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.test.data.date1
import com.sparetimedevs.test.data.date2
import com.sparetimedevs.test.data.date3
import com.sparetimedevs.test.data.date4
import com.sparetimedevs.test.data.date5
import com.sparetimedevs.test.data.date6
import com.sparetimedevs.test.data.date7
import com.sparetimedevs.win.model.CandidateResponse
import com.sparetimedevs.win.util.toResponse
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TextPlainHandlerKtTest : BehaviorSpec({
    
    given("a list of CandidateResponse") {
        `when`("handleSuccessWithTextPlainHandler") {
            then("returns HttpResponseMessage with status code OK, content type header text/plain and response body in expected format") {
                clearAllMocks()
                val request = mockk<HttpRequestMessage<String?>>()
                val candidates: List<CandidateResponse> = candidates.map { it.toResponse().getOrHandle { throw Exception("test failed because of setup.") } }
                
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                
                val result = handleSuccessWithTextPlainHandler(request, candidates)
                
                result.fold(
                    {
                        fail("Test case should yield a Right.")
                    },
                    {
                        it.status shouldBe HttpStatus.OK
                        it.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_TEXT_PLAIN_UTF_8
                        it.body shouldBe EXPECTED_BODY
                    }
                )
            }
        }
    }
}) {
    companion object {
        private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).withZone(ZoneId.from(ZoneOffset.UTC))
        private val EXPECTED_BODY = """| Number in list | Name | Dates | First attendance |
|--|--|--|--|
| 1 | Rose | ${DATE_TIME_FORMATTER.format(date2)}, ${DATE_TIME_FORMATTER.format(date5)}, ${DATE_TIME_FORMATTER.format(date6)} | ${DATE_TIME_FORMATTER.format(date7)} |
| 2 | Abbie | ${DATE_TIME_FORMATTER.format(date3)} | ${DATE_TIME_FORMATTER.format(date7)} |
| 3 | Tommy |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 4 | Joseph | ${DATE_TIME_FORMATTER.format(date1)}, ${DATE_TIME_FORMATTER.format(date4)} | ${DATE_TIME_FORMATTER.format(date7)} |
| 5 | Fani |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 6 | Eden |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 7 | Tiffany |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 8 | Aisha |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 9 | Elsa |  | ${DATE_TIME_FORMATTER.format(date3)} |
| 10 | Ellen |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 11 | Cerys |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 12 | James |  | ${DATE_TIME_FORMATTER.format(date2)} |
| 13 | Kevin |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 14 | William |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 15 | Elle |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 16 | Lois |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 17 | Alexa |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 18 | Kimberley |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 19 | Saffron |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 20 | Penny |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 21 | George |  | ${DATE_TIME_FORMATTER.format(date7)} |
| 22 | Margaret |  | ${DATE_TIME_FORMATTER.format(date7)} |"""
    }
}
