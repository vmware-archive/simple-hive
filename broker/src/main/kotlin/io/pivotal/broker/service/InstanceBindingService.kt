package io.pivotal.broker.service

import org.apache.commons.logging.LogFactory
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService
import org.springframework.stereotype.Service

@Service
open class InstanceBindingService : ServiceInstanceBindingService {

    companion object {
        val log = LogFactory.getLog(InstanceBindingService::class.java)
    }

    override fun deleteServiceInstanceBinding(request: DeleteServiceInstanceBindingRequest) {
        log.warn(">>> UNBINDING SERVICE INSTANCE")
    }

    override fun createServiceInstanceBinding(request: CreateServiceInstanceBindingRequest): CreateServiceInstanceBindingResponse {
        log.warn(">>> BINDING SERVICE INSTANCE")
        return CreateServiceInstanceBindingResponse()
    }
}