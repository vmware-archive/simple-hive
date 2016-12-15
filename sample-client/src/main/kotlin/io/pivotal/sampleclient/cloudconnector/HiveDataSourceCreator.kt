package io.pivotal.sampleclient.cloudconnector

import org.springframework.cloud.service.relational.DataSourceCreator

class HiveDataSourceCreator :
        DataSourceCreator<HiveServiceInfo>(
                "spring-cloud.hive.driver",
                HiveDataSourceCreator.DRIVERS,
                HiveDataSourceCreator.VALIDATION_QUERY) {

    companion object {
        val DRIVERS = arrayOf("io.pivotal.sampleclient.cloudconnector.HiveDriverDelegator")

        val VALIDATION_QUERY = "SELECT 1"
    }

}
