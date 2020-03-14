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

package com.sparetimedevs

import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.HttpStatusType

/**
 * The mock for HttpResponseMessage, can be used in unit tests to verify if the
 * returned response by HTTP trigger function is correct or not.
 * This class is derived from:
 * azure-maven-archetypes/azure-functions-kotlin-archetype/
 * src/main/resources/archetype-resources/src/test/kotlin/HttpResponseMessageMock.kt.
 */
class HttpResponseMessageMock(
    private val httpStatus: HttpStatusType,
    private val headers: Map<String, String>,
    private val body: String
) : HttpResponseMessage {
    
    private val httpStatusCode: Int
    
    init {
        this.httpStatusCode = httpStatus.value()
    }
    
    override fun getStatus(): HttpStatusType {
        return this.httpStatus
    }
    
    override fun getStatusCode(): Int {
        return httpStatusCode
    }
    
    override fun getHeader(key: String): String? {
        return this.headers[key]
    }
    
    override fun getBody(): String {
        return this.body
    }
    
    class HttpResponseMessageBuilderMock(status: HttpStatus) : HttpResponseMessage.Builder {
        
        private var httpStatusCode: Int = 0
        private var httpStatus: HttpStatusType
        private val headers: MutableMap<String, String> = mutableMapOf()
        private var body: Any? = null
        
        init {
            this.httpStatusCode = status.value()
            this.httpStatus = status
        }
        
        override fun status(httpStatusType: HttpStatusType): HttpResponseMessage.Builder {
            this.httpStatusCode = httpStatusType.value()
            this.httpStatus = httpStatusType
            return this
        }
        
        override fun header(key: String, value: String): HttpResponseMessage.Builder {
            this.headers[key] = value
            return this
        }
        
        override fun body(body: Any): HttpResponseMessage.Builder {
            this.body = body
            return this
        }
        
        override fun build(): HttpResponseMessage {
            return HttpResponseMessageMock(this.httpStatus, this.headers, this.body.toString())
        }
    }
}
