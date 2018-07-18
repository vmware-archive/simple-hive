/*
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under
 *
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package io.pivotal.broker.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.cloud.servicebroker.model.instance.*
import org.springframework.jdbc.core.JdbcTemplate

@RunWith(MockitoJUnitRunner::class)
class InstanceServiceTest {

    private lateinit var instanceService: InstanceService

    @Mock
    private lateinit var jdbcTemplate: JdbcTemplate

    @Before
    fun setup() {
        instanceService = InstanceService(jdbcTemplate)
    }

    @Test
    fun `should create a database when creating a service instance`() {
        val request = CreateServiceInstanceRequest.builder().serviceInstanceId("test-instance-id-123").build()

        instanceService.createServiceInstance(request)

        verifyDatabaseIsCreated("test_instance_id_123")
    }

    @Test
    fun `should drop database when deleting service instance`() {
        val request = DeleteServiceInstanceRequest.builder()
                .planId("default")
                .serviceDefinitionId("test-service")
                .serviceInstanceId("test-instance-id-456")
                .build()

        instanceService.deleteServiceInstance(request)

        verifyDatabaseIsDropped("test_instance_id_456")
    }

    @Test
    fun `should recreate database when updating service instance`() {
        val request = UpdateServiceInstanceRequest.builder()
                .serviceDefinitionId("test-service-def-id")
                .planId("plan-id")
                .serviceInstanceId("test-instance-id-890")
                .build()

        instanceService.updateServiceInstance(request)

        verifyDatabaseIsRecreated("test_instance_id_890")
    }

    @Test
    fun `should return success when getting the last operation`() {
        val request = GetLastServiceOperationRequest.builder().serviceInstanceId("any-service-instance-id").build()

        val response = instanceService.getLastOperation(request)

        assertThat(response.state).isEqualTo(OperationState.SUCCEEDED)
    }

    private fun verifyDatabaseIsCreated(name: String) {
        verify(jdbcTemplate).update("CREATE DATABASE $name")
    }

    private fun verifyDatabaseIsDropped(name: String) {
        verify(jdbcTemplate).update("DROP DATABASE IF EXISTS $name CASCADE")
    }

    private fun verifyDatabaseIsRecreated(name: String) {
        val inOrder = inOrder(jdbcTemplate)

        inOrder.verify(jdbcTemplate).update("DROP DATABASE IF EXISTS $name CASCADE")
        inOrder.verify(jdbcTemplate).update("CREATE DATABASE $name")
    }
}