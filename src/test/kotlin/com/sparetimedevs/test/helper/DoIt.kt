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

package com.sparetimedevs.test.helper

import io.kotest.core.spec.style.BehaviorSpec

class DoIt : BehaviorSpec({
    
    // Don't do it unless you want to.
    // In that case, uncomment the next line and run the test case.
    // val dataInitializer = DataInitializer(dependencyModule.candidateRepository)
    
    given("I wan't some candidates") {
        `when`("I manually and locally test") {
            then("inserts them in the database") {
                // Don't do it unless you want to.
                // In that case, uncomment the next line and run the test case.
                // dataInitializer.initCandidates(candidates)
            }
        }
    }
})
