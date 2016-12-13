package io.pivotal.sampleclient.cloudconnector

import org.springframework.cloud.service.relational.DataSourceCreator

class HiveDataSourceCreator :
        DataSourceCreator<HiveServiceInfo>(
                "spring-cloud.hive.driver",
                HiveDataSourceCreator.DRIVERS,
                HiveDataSourceCreator.VALIDATION_QUERY) {

    companion object {
        val DRIVERS = arrayOf("org.apache.hive.jdbc.HiveDriver")

        val VALIDATION_QUERY = "SELECT 1"
    }

}
