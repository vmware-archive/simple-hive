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
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService
import org.springframework.stereotype.Service

@Service
open class InstanceBindingService
constructor(private val config: ServiceConfig) : ServiceInstanceBindingService {

    companion object {
        val log: Log = LogFactory.getLog(InstanceBindingService::class.java)
    }

    override fun deleteServiceInstanceBinding(request: DeleteServiceInstanceBindingRequest) {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("unbinding service instance id=$instanceId")
    }

    override fun createServiceInstanceBinding(request: CreateServiceInstanceBindingRequest): CreateServiceInstanceBindingResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("binding service instance id=$instanceId")
        return CreateServiceInstanceAppBindingResponse().withCredentials(mapOf("uri" to formatUri(instanceId)))
    }

    private fun formatUri(instanceId: String) =
            "hive2://${config.admin.username}:${config.admin.password}@${config.host}:${config.port}/" +
            "$instanceId;transportMode=http;httpPath=${config.httpPath}"

    private fun formatInstanceId(instanceId: String) = instanceId.replace('-', '_')

}