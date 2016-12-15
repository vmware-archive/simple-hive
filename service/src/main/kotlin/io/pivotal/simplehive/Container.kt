package io.pivotal.simplehive

import io.pivotal.simplehive.config.HiveConfig
import org.apache.hive.service.cli.thrift.ThriftHttpCLIService
import org.apache.hive.service.server.HiveServer2
import kotlin.concurrent.thread

class Container(private val hiveConfig: HiveConfig) {
    val hiveServer: HiveServer2 = HiveServer2()

    fun init() {
        try {
            this.hiveServer.init(this.hiveConfig.build())
        } catch (exc: Exception) {
            throw IllegalStateException("Failed to create HiveServer :" + exc.message, exc)
        }
    }

    fun start() {
        thread {
            getService().run()
        }
    }

    private fun getService(): ThriftHttpCLIService {
        val service = hiveServer.services?.find { it is ThriftHttpCLIService } ?:
                throw IllegalStateException("ThriftHttpCLIService service is not available. Make sure http mode is enabled.")
        return service as ThriftHttpCLIService
    }
}
