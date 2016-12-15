package io.pivotal.broker.service

import io.pivotal.broker.config.ServiceConfig
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.*
import org.springframework.cloud.servicebroker.service.ServiceInstanceService
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager

@Service
open class InstanceService
constructor(private val config: ServiceConfig) : ServiceInstanceService {

    companion object {
        val log = LogFactory.getLog(InstanceService::class.java)
    }

    override fun createServiceInstance(request: CreateServiceInstanceRequest): CreateServiceInstanceResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("creating service instance id=$instanceId")
        createDb(instanceId)
        return CreateServiceInstanceResponse()
    }

    private fun createDb(database: String) {
        execute { connection ->
            connection.createStatement().executeUpdate("CREATE DATABASE $database")
        }
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
        execute { connection ->
            connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS $database CASCADE")
        }
    }

    private fun execute(action: (Connection) -> Unit) {
        val connection = DriverManager.getConnection("jdbc:hive2://${config.host}/default;transportMode=http;httpPath=simple-hive", "hive-user", "hive-password")
        try {
            action(connection)
        } finally {
            connection.close()
        }
    }

    private fun formatInstanceId(instanceId: String) = instanceId.replace('-', '_')

}