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

import io.pivotal.broker.config.ServiceConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest

class InstanceBindingServiceTest {
    private lateinit var instanceBindingService: InstanceBindingService

    @Before
    fun setup() {
        val config = ServiceConfig()
        config.apply {
            host = "a-sample-host"
            port = "8008"
            httpPath = "simple-path"

            config.admin.apply {
                username = "admin-user"
                password = "admin-secret"
            }
        }

        instanceBindingService = InstanceBindingService(config)
    }

    @Test
    fun `should return database uri when binding to service instance`() {
        val request = CreateServiceInstanceBindingRequest.builder().serviceInstanceId("test-instance-123").build()

        val response = instanceBindingService.createServiceInstanceBinding(request)

        assertThat(response).isEqualTo(
                CreateServiceInstanceAppBindingResponse.builder()
                        .credentials(mapOf("uri" to "hive2://admin-user:admin-secret@a-sample-host:8008/test_instance_123;transportMode=http;httpPath=simple-path"))
                        .build())
    }

}