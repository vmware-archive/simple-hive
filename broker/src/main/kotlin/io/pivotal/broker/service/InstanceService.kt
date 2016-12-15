package io.pivotal.broker.service

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.*
import org.springframework.cloud.servicebroker.service.ServiceInstanceService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
open class InstanceService
constructor(private val jdbcTemplate: JdbcTemplate) : ServiceInstanceService {

    companion object {
        val log: Log = LogFactory.getLog(InstanceService::class.java)
    }

    override fun createServiceInstance(request: CreateServiceInstanceRequest): CreateServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("creating service instance id=$instanceId")
        createDb(instanceId)
        return CreateServiceInstanceResponse()
    }

    private fun createDb(database: String) {
        jdbcTemplate.update("CREATE DATABASE $database")
    }

    override fun updateServiceInstance(request: UpdateServiceInstanceRequest): UpdateServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("updating service instance id=$instanceId")
        deleteDb(instanceId)
        createDb(instanceId)
        return UpdateServiceInstanceResponse()
    }

    override fun getLastOperation(request: GetLastServiceOperationRequest): GetLastServiceOperationResponse {
        log.info("getting last operation")
        return GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED)
    }

    override fun deleteServiceInstance(request: DeleteServiceInstanceRequest): DeleteServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("deleting service instance id=$instanceId")
        deleteDb(instanceId)
        return DeleteServiceInstanceResponse()
    }

    private fun deleteDb(database: String) {
        jdbcTemplate.update("DROP DATABASE IF EXISTS $database CASCADE")
    }

    private fun formatInstanceId(instanceId: String) = instanceId.replace('-', '_')

}