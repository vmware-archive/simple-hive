package io.pivotal.broker.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.cloud.servicebroker.model.*
import org.springframework.jdbc.core.JdbcTemplate

@RunWith(MockitoJUnitRunner::class)
class InstanceServiceTest {

    private lateinit var instanceService: InstanceService

    @Mock
    private lateinit var jdbcTemplate: JdbcTemplate

    @Before
    fun setup() {
        instanceService = InstanceService(jdbcTemplate)
    }

    @Test
    fun `should create a database when creating a service instance`() {
        val request = CreateServiceInstanceRequest().withServiceInstanceId("test-instance-id-123")

        instanceService.createServiceInstance(request)

        verifyDatabaseIsCreated("test_instance_id_123")
    }

    @Test
    fun `should drop database when deleting service instance`() {
        val request = DeleteServiceInstanceRequest("test-instance-id-456", "test-service", "default", null)

        instanceService.deleteServiceInstance(request)

        verifyDatabaseIsDropped("test_instance_id_456")
    }

    @Test
    fun `should recreate database when updating service instance`() {
        val request = UpdateServiceInstanceRequest("test-service-def-id", "plan-id")
                .withServiceInstanceId("test-instance-id-890")

        instanceService.updateServiceInstance(request)

        verifyDatabaseIsRecreated("test_instance_id_890")
    }

    @Test
    fun `should return success when getting the last operation`() {
        val request = GetLastServiceOperationRequest("any-service-instance-id")

        val response = instanceService.getLastOperation(request)

        assertThat(response.state).isEqualTo(OperationState.SUCCEEDED)
    }

    private fun verifyDatabaseIsCreated(name: String) {
        verify(jdbcTemplate).update("CREATE DATABASE $name")
    }

    private fun verifyDatabaseIsDropped(name: String) {
        verify(jdbcTemplate).update("DROP DATABASE IF EXISTS $name CASCADE")
    }

    private fun verifyDatabaseIsRecreated(name: String) {
        val inOrder = inOrder(jdbcTemplate)

        inOrder.verify(jdbcTemplate).update("DROP DATABASE IF EXISTS $name CASCADE")
        inOrder.verify(jdbcTemplate).update("CREATE DATABASE $name")
    }
}