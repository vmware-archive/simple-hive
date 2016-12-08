package io.pivotal.simplehive

import org.apache.hadoop.hive.ql.exec.tez.TezJobMonitor
import org.apache.hive.service.cli.thrift.ThriftHttpCLIService
import org.apache.hive.service.server.HiveServer2
import org.slf4j.LoggerFactory

class Container(private val context: Context) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(Container::class.java)
    }

    lateinit var hiveServer: HiveServer2

    fun init() {
        try {
            this.hiveServer = HiveServer2()
            this.hiveServer.init(this.context.build())
        } catch (exc: Exception) {
            throw IllegalStateException("Failed to create HiveServer :" + exc.message, exc)
        }

    }

    fun start() {
        val service = hiveServer.services?.find { it is ThriftHttpCLIService } ?:
                throw IllegalStateException("ThriftHttpCLIService service is not available. make sure http mode is enabled.")

        (service as ThriftHttpCLIService).run()
    }

    fun stop() {
        try {
            TezJobMonitor.killRunningJobs()
        } catch (var5: Throwable) {
            LOGGER.warn("Failed to kill tez session: " + var5.message + ". Turn on log level debug for stacktrace")
            LOGGER.debug(var5.message, var5)
        }

        try {
            this.hiveServer.stop()
        } catch (var2: Throwable) {
            LOGGER.warn("Failed to stop HiveServer2: " + var2.message + ". Turn on log level debug for stacktrace")
            LOGGER.debug(var2.message, var2)
        }

        this.hiveServer.stop()
        LOGGER.info("Tore down HiveServer instance")
    }

}
