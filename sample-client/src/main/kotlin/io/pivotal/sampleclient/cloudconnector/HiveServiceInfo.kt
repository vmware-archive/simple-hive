package io.pivotal.sampleclient.cloudconnector

import org.springframework.cloud.service.ServiceInfo
import org.springframework.cloud.service.common.RelationalServiceInfo

@ServiceInfo.ServiceLabel("hive")
open class HiveServiceInfo(id: String, url: String)
    : RelationalServiceInfo(id, url, null, HiveServiceInfo.HIVE_SCHEME) {

    companion object {
        val HIVE_SCHEME = "hive2"
    }
}
