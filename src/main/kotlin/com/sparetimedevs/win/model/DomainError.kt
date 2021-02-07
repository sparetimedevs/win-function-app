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

sealed class DomainError(open val message: String) {
    
    data class ToResponseError(override val message: String = "An exception was thrown while converting to a response model.") : DomainError(message)
    
    data class DateParseError(override val message: String = "An exception was thrown while parsing the date string.") : DomainError(message)
    
    data class ValidationError(override val message: String = "There was an error while validating the input.") : DomainError(message)
    
    data class JsonError(override val message: String = "There was an error while (de)serializing JSON.") : DomainError(message)
    
    data class EntityNotFound(override val message: String = "Entity not found.") : DomainError(message)
    
    data class ServiceUnavailable(override val message: String = "The service is unavailable.") : DomainError(message)
    
    data class UnknownError(override val message: String = "An unknown error occurred.") : DomainError(message)
}
