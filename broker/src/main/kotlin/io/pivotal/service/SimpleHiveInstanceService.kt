package io.pivotal.service

import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.*
import org.springframework.cloud.servicebroker.service.ServiceInstanceService
import org.springframework.stereotype.Service
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse
import org.springframework.cloud.servicebroker.model.OperationState
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse
import java.sql.DriverManager

@Service
open class SimpleHiveInstanceService : ServiceInstanceService {
    companion object {
        val log = LogFactory.getLog(SimpleHiveInstanceService::class.java)
        val simpleHiveHost = "simple-hive-service.local.pcfdev.io"
    }

    override fun createServiceInstance(request: CreateServiceInstanceRequest): CreateServiceInstanceResponse {
        log.warn(">>> CREATING SERVICE INSTANCE")
        createDb(request.serviceInstanceId.replace('-', '_'))
        return CreateServiceInstanceResponse()
    }

    private fun createDb(serviceInstanceId: String) {
        val connection = DriverManager.getConnection("jdbc:hive2://$simpleHiveHost/default;transportMode=http;httpPath=simple-hive")
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