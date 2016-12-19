package io.pivotal.broker.service

import io.pivotal.broker.config.ServiceConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test

import org.junit.Before
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest

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
        val request = CreateServiceInstanceBindingRequest().withServiceInstanceId("test-instance-123")

        val response = instanceBindingService.createServiceInstanceBinding(request)

        assertThat(response).isEqualTo(
                CreateServiceInstanceAppBindingResponse().withCredentials(
                        mapOf("uri" to "hive2://admin-user:admin-secret@a-sample-host:8008/test_instance_123;transportMode=http;httpPath=simple-path")))
    }

}