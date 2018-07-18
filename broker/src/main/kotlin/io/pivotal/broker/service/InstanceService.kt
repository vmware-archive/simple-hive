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

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.instance.*
import org.springframework.cloud.servicebroker.service.ServiceInstanceService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class InstanceService(private val jdbcTemplate: JdbcTemplate) : ServiceInstanceService {

    companion object {
        val log: Log = LogFactory.getLog(InstanceService::class.java)
    }

    override fun createServiceInstance(request: CreateServiceInstanceRequest): CreateServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("creating service instance id=$instanceId")
        createDb(instanceId)
        return CreateServiceInstanceResponse.builder().build()
    }

    private fun createDb(database: String) {
        jdbcTemplate.update("CREATE DATABASE $database")
    }

    override fun updateServiceInstance(request: UpdateServiceInstanceRequest): UpdateServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("updating service instance id=$instanceId")
        deleteDb(instanceId)
        createDb(instanceId)
        return UpdateServiceInstanceResponse.builder().build()
    }

    override fun getLastOperation(request: GetLastServiceOperationRequest): GetLastServiceOperationResponse {
        log.info("getting last operation")
        return GetLastServiceOperationResponse.builder().operationState(OperationState.SUCCEEDED).build()
    }

    override fun deleteServiceInstance(request: DeleteServiceInstanceRequest): DeleteServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("deleting service instance id=$instanceId")
        deleteDb(instanceId)
        return DeleteServiceInstanceResponse.builder().build()
    }

    private fun deleteDb(database: String) {
        jdbcTemplate.update("DROP DATABASE IF EXISTS $database CASCADE")
    }

    private fun formatInstanceId(instanceId: String) = instanceId.replace('-', '_')

}