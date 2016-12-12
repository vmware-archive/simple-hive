package io.pivotal.broker.service

import io.pivotal.broker.config.BrokerConfig
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.*
import org.springframework.cloud.servicebroker.service.ServiceInstanceService
import org.springframework.stereotype.Service
import java.sql.DriverManager

@Service
open class InstanceService
constructor(private val config: BrokerConfig) : ServiceInstanceService {

    companion object {
        val log = LogFactory.getLog(InstanceService::class.java)
    }

    override fun createServiceInstance(request: CreateServiceInstanceRequest): CreateServiceInstanceResponse {
        log.warn(">>> CREATING SERVICE INSTANCE")
        createDb(request.serviceInstanceId.replace('-', '_'))
        return CreateServiceInstanceResponse()
    }

    private fun createDb(serviceInstanceId: String) {
        val connection = DriverManager.getConnection("jdbc:hive2://${config.serviceHost}/default;transportMode=http;httpPath=simple-hive")
        connection.createStatement().executeUpdate("CREATE DATABASE $serviceInstanceId")
    }

    override fun getLastOperation(request: GetLastServiceOperationRequest): GetLastServiceOperationResponse {
        log.warn(">>> GETTING LAST OPERATION")
        return GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED)
    }

    override fun deleteServiceInstance(request: DeleteServiceInstanceRequest): DeleteServiceInstanceResponse {
        log.warn(">>> DELETING SERVICE INSTANCE")
        return DeleteServiceInstanceResponse()
    }

    override fun updateServiceInstance(request: UpdateServiceInstanceRequest): UpdateServiceInstanceResponse {
        log.warn(">>> UPDATING SERVICE INSTANCE")
        return UpdateServiceInstanceResponse()
    }

}