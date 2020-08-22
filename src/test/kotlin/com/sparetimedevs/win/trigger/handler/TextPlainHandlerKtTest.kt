/*
 * Copyright (c) 2020 sparetimedevs and respective authors and developers.
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

import arrow.core.Either
import arrow.core.getOrHandle
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.test.data.date1
import com.sparetimedevs.test.data.date2
import com.sparetimedevs.test.data.date3
import com.sparetimedevs.test.data.date4
import com.sparetimedevs.test.data.date5
import com.sparetimedevs.test.data.date6
import com.sparetimedevs.test.data.date7
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.util.toViewModel
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.text.SimpleDateFormat

class TextPlainHandlerKtTest : BehaviorSpec({
    
    given("a list of CandidateViewModels") {
        `when`("handleSuccessWithTextPlainHandler") {
            then("returns HttpResponseMessage with status code OK, content type header text/plain and response body in expected format") {
                clearAllMocks()
                val request = mockk<HttpRequestMessage<String?>>()
                val log = mockk<suspend (level: Level, message: String) -> Either<Throwable, Unit>>()
                val candidates: List<CandidateViewModel> = candidates.map { it.toViewModel().getOrHandle { throw Exception("test failed because of setup.") } }
                
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                
                val result = handleSuccessWithTextPlainHandler(request, log, candidates)
                
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
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat(DATE_FORMAT_PATTERN)
        private val EXPECTED_BODY = """| Number in list | Name | Dates | First attendance |
|--|--|--|--|
| 1 | Rose | ${SIMPLE_DATE_FORMAT.format(date2)}, ${SIMPLE_DATE_FORMAT.format(date5)}, ${SIMPLE_DATE_FORMAT.format(date6)} | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 2 | Abbie | ${SIMPLE_DATE_FORMAT.format(date3)} | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 3 | Tommy |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 4 | Joseph | ${SIMPLE_DATE_FORMAT.format(date1)}, ${SIMPLE_DATE_FORMAT.format(date4)} | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 5 | Fani |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 6 | Eden |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 7 | Tiffany |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 8 | Aisha |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 9 | Elsa |  | ${SIMPLE_DATE_FORMAT.format(date3)} |
| 10 | Ellen |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 11 | Cerys |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 12 | James |  | ${SIMPLE_DATE_FORMAT.format(date2)} |
| 13 | Kevin |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 14 | William |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 15 | Elle |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 16 | Lois |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 17 | Alexa |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 18 | Kimberley |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 19 | Saffron |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 20 | Penny |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 21 | George |  | ${SIMPLE_DATE_FORMAT.format(date7)} |
| 22 | Margaret |  | ${SIMPLE_DATE_FORMAT.format(date7)} |"""
    }
}
