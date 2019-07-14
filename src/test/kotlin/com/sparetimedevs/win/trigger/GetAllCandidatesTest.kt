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

package com.sparetimedevs.win.trigger

import arrow.core.Left
import arrow.core.Right
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.win.ServiceLocator
import com.sparetimedevs.win.getDbName
import com.sparetimedevs.win.getMongoDbConnectionString
import com.sparetimedevs.win.model.Candidate
import com.sparetimedevs.win.model.toViewModel
import com.sparetimedevs.win.repository.CandidateRepository
import io.kotlintest.matchers.string.contain
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import java.util.Optional
import java.util.logging.Logger

class GetAllCandidatesTest : BehaviorSpec({

	given("get is called") {
		`when`("database is reachable") {
			then( "returns all candidates") {
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val context = mockk<ExecutionContext>()
				val candidateRepository = mockk<CandidateRepository>()
				mockkStatic("com.sparetimedevs.win.MongoDbConfigurationKt")
				every { getMongoDbConnectionString() } returns "mongodb://valid_connection_string"
				every { getDbName() } returns "TestDatabaseName"
				val serviceLocator = mockk<ServiceLocator>()
				mockkObject(ServiceLocator.Companion)
				every { ServiceLocator.defaultInstance } returns serviceLocator
				every { serviceLocator.candidateRepository } returns candidateRepository
				val candidate1 = Candidate(name = "A Name")
				val candidate2 = Candidate(name = "Another Name")
				val candidates = listOf(candidate1, candidate2)
				val eitherContainingCandidates = Right(candidates)
				coEvery { candidateRepository.findAll(any()) } returns eitherContainingCandidates
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.OK)

				val response = GetAllCandidates().get(request, context)

				response.status shouldBe HttpStatus.OK
				response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
				response.body shouldBe candidates.toViewModel().toString()
			}
		}

		`when`("database is unreachable") {
			then( "returns error message") {
				val request = mockk<HttpRequestMessage<Optional<String>>>()
				val context = mockk<ExecutionContext>()
				val logger = mockk<Logger>()
				val candidateRepository = mockk<CandidateRepository>()
				mockkStatic("com.sparetimedevs.win.MongoDbConfigurationKt")
				every { getMongoDbConnectionString() } returns "mongodb://valid_connection_string"
				every { getDbName() } returns "TestDatabaseName"
				val serviceLocator = mockk<ServiceLocator>()
				mockkObject(ServiceLocator.Companion)
				every { ServiceLocator.defaultInstance } returns serviceLocator
				every { serviceLocator.candidateRepository } returns candidateRepository
				every { context.logger } returns logger
				every { logger.severe(any<String>()) } just Runs
				val eitherContainingError = Left(Error.ServiceUnavailable())
				coEvery { candidateRepository.findAll(any()) } returns eitherContainingError
				every { request.createResponseBuilder(any()) } returns HttpResponseMessageMock.HttpResponseMessageBuilderMock(HttpStatus.INTERNAL_SERVER_ERROR)

				val response = GetAllCandidates().get(request, context)

				response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
				response.body shouldBe contain(Error.ServiceUnavailable().message)
			}
		}
	}
})
