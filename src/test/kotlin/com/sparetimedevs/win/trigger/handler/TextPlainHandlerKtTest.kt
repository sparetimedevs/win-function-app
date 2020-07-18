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

import arrow.core.getOrHandle
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.pofpaf.http.CONTENT_TYPE
import com.sparetimedevs.test.data.candidates
import com.sparetimedevs.win.model.CandidateViewModel
import com.sparetimedevs.win.util.toViewModel
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

class TextPlainHandlerKtTest : BehaviorSpec({
    
    given("a list of CandidateViewModels") {
        `when`("handleSuccessWithTextPlainHandler") {
            then("returns HttpResponseMessage with status code OK, content type header text/plain and response body in expected format") {
                val request = mockk<HttpRequestMessage<String?>>()
                val context = mockk<ExecutionContext>()
                val candidates: List<CandidateViewModel> = candidates.map { it.toViewModel().getOrHandle { throw Exception("test failed because of setup.") } }
                
                every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)
                
                val result = handleSuccessWithTextPlainHandler(request, context, candidates)
                
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
        private const val EXPECTED_BODY = """| Number in list | Name | Dates | First attendance |
|--|--|--|--|
| 1 | Rose | 2020-06-29, 2020-05-08, 2020-03-25 | 2020-02-28 |
| 2 | Abbie | 2020-06-11 | 2020-02-28 |
| 3 | Tommy |  | 2020-02-28 |
| 4 | Joseph | 2020-07-27, 2020-05-28 | 2020-02-28 |
| 5 | Fani |  | 2020-02-28 |
| 6 | Eden |  | 2020-02-28 |
| 7 | Tiffany |  | 2020-02-28 |
| 8 | Aisha |  | 2020-02-28 |
| 9 | Elsa |  | 2020-06-11 |
| 10 | Ellen |  | 2020-02-28 |
| 11 | Cerys |  | 2020-02-28 |
| 12 | James |  | 2020-06-29 |
| 13 | Kevin |  | 2020-02-28 |
| 14 | William |  | 2020-02-28 |
| 15 | Elle |  | 2020-02-28 |
| 16 | Lois |  | 2020-02-28 |
| 17 | Alexa |  | 2020-02-28 |
| 18 | Kimberley |  | 2020-02-28 |
| 19 | Saffron |  | 2020-02-28 |
| 20 | Penny |  | 2020-02-28 |
| 21 | George |  | 2020-02-28 |
| 22 | Margaret |  | 2020-02-28 |"""
    }
}
