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

package com.sparetimedevs.win.model

sealed class DomainError(open val message: String) {
    
    data class ToViewModelError(override val message: String = "An exception was thrown while converting to view model.") : DomainError(message)
    
    data class DateParseError(override val message: String = "An exception was thrown while parsing the date string.") : DomainError(message)
    
    data class EntityNotFound(override val message: String = "Entity not found.") : DomainError(message)
    
    data class ServiceUnavailable(override val message: String = "The service is unavailable.") : DomainError(message)
    
    data class UnknownError(override val message: String = "An unknown error occurred.") : DomainError(message)
}
