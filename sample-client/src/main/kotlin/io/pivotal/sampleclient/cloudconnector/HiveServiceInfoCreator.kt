package io.pivotal.sampleclient.cloudconnector

import org.springframework.cloud.cloudfoundry.RelationalServiceInfoCreator
import org.springframework.cloud.cloudfoundry.Tags

open class HiveServiceInfoCreator :
        RelationalServiceInfoCreator<HiveServiceInfo>(Tags("hive"), HiveServiceInfo.HIVE_SCHEME) {

    override fun createServiceInfo(id: String, url: String, jdbcUrl: String?): HiveServiceInfo =
            HiveServiceInfo(id, url)
}
