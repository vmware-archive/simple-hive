package io.pivotal.broker.service

import io.pivotal.broker.config.ServiceConfig
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
        val log = LogFactory.getLog(InstanceBindingService::class.java)
        val httpPath = "simple-hive"
        val transportMode = "http"
    }

    override fun deleteServiceInstanceBinding(request: DeleteServiceInstanceBindingRequest) {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("unbinding service instance id=$instanceId")
    }

    override fun createServiceInstanceBinding(request: CreateServiceInstanceBindingRequest): CreateServiceInstanceBindingResponse {
        val instanceId = formatInstanceId(request.serviceInstanceId)
        log.info("binding service instance id=$instanceId")
        return CreateServiceInstanceAppBindingResponse().withCredentials(
                mapOf("uri" to formatUri(instanceId)))
    }

    private fun formatUri(instanceId: String) =
            "hive2://${config.admin.username}:${config.admin.password}@${config.host}:${config.port}/$instanceId;transportMode=$transportMode;httpPath=$httpPath"

    private fun formatInstanceId(instanceId: String) = instanceId.replace('-', '_')

}