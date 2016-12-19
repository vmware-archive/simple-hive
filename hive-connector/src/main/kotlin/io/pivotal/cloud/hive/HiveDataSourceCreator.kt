package io.pivotal.cloud.hive

import org.springframework.cloud.service.relational.DataSourceCreator

class HiveDataSourceCreator :
        DataSourceCreator<HiveServiceInfo>(
                "spring-cloud.hive.driver",
                DRIVERS,
                VALIDATION_QUERY) {

    companion object {
        val DRIVERS = arrayOf("io.pivotal.cloud.hive.HiveDriverAdapter")

        val VALIDATION_QUERY = "SELECT 1"
    }
}
