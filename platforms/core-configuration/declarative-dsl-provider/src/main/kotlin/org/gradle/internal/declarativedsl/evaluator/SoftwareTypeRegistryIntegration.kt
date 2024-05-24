/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.declarativedsl.evaluator

import org.gradle.internal.declarativedsl.analysis.AssignmentRecord
import org.gradle.internal.declarativedsl.analysis.DataAdditionRecord
import org.gradle.internal.declarativedsl.analysis.NestedObjectAccessRecord
import org.gradle.internal.declarativedsl.conventions.AdditionRecordConvention
import org.gradle.internal.declarativedsl.conventions.AssignmentRecordConvention
import org.gradle.internal.declarativedsl.conventions.ConventionDefinitionRegistrar
import org.gradle.internal.declarativedsl.conventions.NestedObjectAccessConvention
import org.gradle.internal.declarativedsl.conventions.SoftwareTypeConventionRepository
import org.gradle.internal.declarativedsl.conventions.SoftwareTypeConventionResolutionResults
import org.gradle.plugin.software.internal.SoftwareTypeRegistry


internal
fun softwareTypeRegistryBasedConventionRepository(softwareTypeRegistry: SoftwareTypeRegistry): SoftwareTypeConventionRepository = object : SoftwareTypeConventionRepository {
    override fun findConventions(softwareTypeName: String): SoftwareTypeConventionResolutionResults? =
        // TODO: optimize O(n) lookup
        softwareTypeRegistry.softwareTypeImplementations.find { it.softwareType == softwareTypeName }?.let { softwareType ->
            val assignments = buildList<AssignmentRecord> {
                softwareType.conventions.filterIsInstance<AssignmentRecordConvention>().forEach { it.apply(::add) }
            }
            val additions = buildList<DataAdditionRecord> {
                softwareType.conventions.filterIsInstance<AdditionRecordConvention>().forEach { it.apply(::add) }
            }
            val nestedObjectAccess = buildList<NestedObjectAccessRecord> {
                softwareType.conventions.filterIsInstance<NestedObjectAccessConvention>().forEach { it.apply(::add) }
            }
            SoftwareTypeConventionResolutionResults(softwareTypeName, assignments, additions, nestedObjectAccess)
        }
}


internal
fun softwareTypeRegistryBasedConventionRegistrar(softwareTypeRegistry: SoftwareTypeRegistry): ConventionDefinitionRegistrar = object : ConventionDefinitionRegistrar {
    override fun addConventions(conventionsBySoftwareType: Map<String, SoftwareTypeConventionResolutionResults>) {
        softwareTypeRegistry.softwareTypeImplementations.forEach { softwareTypeImplementation ->
            conventionsBySoftwareType[softwareTypeImplementation.softwareType]?.let { conventions ->
                val conventionRecords = conventions.additions.map(::AdditionRecordConvention) +
                    conventions.assignments.map(::AssignmentRecordConvention) +
                    conventions.nestedObjectAccess.map(::NestedObjectAccessConvention)
                conventionRecords.forEach(softwareTypeImplementation::addConvention)
            }
        }
    }
}
