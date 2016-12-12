package io.pivotal

import org.springframework.cloud.servicebroker.model.Catalog
import org.springframework.cloud.servicebroker.model.Plan
import org.springframework.cloud.servicebroker.model.ServiceDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class CatalogConfig {

    @Bean
    open fun catalog(): Catalog {
        return Catalog(Collections.singletonList(
                ServiceDefinition(
                        "simple-hive-service-broker",
                        "simple-hive",
                        "A simple hive service broker implementation",
                        true,
                        false,
                        listOf(
                                Plan("simple-hive-plan",
                                        "default",
                                        "This is a default plan. All services are created equally.",
                                        getPlanMetadata())),
                        listOf("tag-1", "tag-2"),
                        getServiceDefinitionMetadata(),
                        null,
                        null))
        )
    }

    private fun getServiceDefinitionMetadata(): Map<String, Any> {
        return HashMap()
//        sdMetadata.put("displayName", "MongoDB")
//        sdMetadata.put("imageUrl", "http://info.mongodb.com/rs/mongodb/images/MongoDB_Logo_Full.png")
//        sdMetadata.put("longDescription", "MongoDB Service")
//        sdMetadata.put("providerDisplayName", "Pivotal")
//        sdMetadata.put("documentationUrl", "https://github.com/spring-cloud-samples/cloudfoundry-mongodb-service-broker")
//        sdMetadata.put("supportUrl", "https://github.com/spring-cloud-samples/cloudfoundry-mongodb-service-broker")
//        return sdMetadata
    }

    private fun getPlanMetadata(): Map<String, Any> {
        val planMetadata = HashMap<String, Any>()
        planMetadata.put("costs", getCosts())
        planMetadata.put("bullets", getBullets())
        return planMetadata
    }

    private fun getCosts(): List<Map<String, Any>> {
        val costsMap = HashMap<String, Any>()

        val amount = HashMap<String, Any>()
        amount.put("usd", 0.0)

        costsMap.put("amount", amount)
        costsMap.put("unit", "MONTHLY")

        return Collections.singletonList(costsMap)
    }

    private fun getBullets(): List<String> {
        return Arrays.asList("Shared MongoDB server",
                "100 MB Storage (not enforced)",
                "40 concurrent connections (not enforced)")
    }
}