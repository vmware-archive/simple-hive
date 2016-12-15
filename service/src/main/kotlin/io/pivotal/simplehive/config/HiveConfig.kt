package io.pivotal.simplehive.config

import io.pivotal.simplehive.HiveFileSystem
import io.pivotal.simplehive.config.ServiceConfig
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hadoop.hive.conf.HiveConf.ConfVars
import org.hsqldb.jdbc.JDBCDriver
import java.util.*

class HiveConfig(private val hiveFileSystem: HiveFileSystem,
                 private val serviceConfig: ServiceConfig) {

    private val hiveConf = HiveConf()

    fun build(): HiveConf = this.hiveConf.configure()

    private fun HiveConf.configure(): HiveConf =
            this.configureMiscHiveSettings()
                    .configureAuth()
                    .configureTransportMode()
                    .configureMetaStore()
                    .configureMrExecutionEngine()
                    .configureTezExecutionEngine()
                    .configureJavaSecurityRealm()
                    .configureSupportConcurrency()
                    .configureFileSystem()
                    .configureAssertionStatus()

    private fun HiveConf.configureTransportMode(): HiveConf {
        setVar(ConfVars.HIVE_SERVER2_TRANSPORT_MODE, "http")
        setIntVar(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PORT, serviceConfig.port.toInt())
        setVar(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH, serviceConfig.httpPath)

        return this
    }

    private fun HiveConf.configureAuth(): HiveConf {
        setVar(ConfVars.HIVE_SERVER2_AUTHENTICATION, "CUSTOM")
        setVar(ConfVars.HIVE_SERVER2_CUSTOM_AUTHENTICATION_CLASS, "io.pivotal.simplehive.Authenticator")
        return this
    }

    private fun HiveConf.configureMiscHiveSettings(): HiveConf {
        setBoolVar(ConfVars.HIVESTATSAUTOGATHER, false)
        setBoolVar(ConfVars.HIVE_CBO_ENABLED, false)
        setBoolVar(ConfVars.HIVE_SERVER2_LOGGING_OPERATION_ENABLED, false)
        setVar(ConfVars.HADOOPBIN, "NO_BIN!")

        return this
    }

    private fun HiveConf.configureMrExecutionEngine(): HiveConf {
        setBoolVar(ConfVars.HIVE_INFER_BUCKET_SORT, false)
        setBoolVar(ConfVars.HIVEMETADATAONLYQUERIES, false)
        setBoolVar(ConfVars.HIVEOPTINDEXFILTER, false)
        setBoolVar(ConfVars.HIVECONVERTJOIN, false)
        setBoolVar(ConfVars.HIVESKEWJOIN, false)
        setLongVar(ConfVars.HIVECOUNTERSPULLINTERVAL, 1L)
        setBoolVar(ConfVars.HIVE_RPC_QUERY_PLAN, true)

        return this
    }

    private fun HiveConf.configureTezExecutionEngine(): HiveConf {
        setBoolean("tez.local.mode", true)
        set("fs.defaultFS", "file:///")
        setBoolean("tez.runtime.optimize.local.fetch", true)
        set("tez.am.disable.client-version-check", "true")
        set("tez.am.use.concurrent-dispatcher", "false")
        set("tez.am.container.reuse.enabled", "false")
        set("tez.dag.recovery.enabled", "false")
        set("tez.task.get-task.sleep.interval-ms.max", "1")
        set("tez.am.tez-ui.webservice.enable", "false")
        set("tez.dag.recovery.enabled", "false")
        set("tez.am.node-blacklisting.enabled", "false")
        return this
    }

    private fun HiveConf.configureJavaSecurityRealm(): HiveConf {
        System.setProperty("java.security.krb5.realm", "")
        System.setProperty("java.security.krb5.kdc", "")
        System.setProperty("java.security.krb5.conf", "/dev/null")
        return this
    }

    private fun HiveConf.configureAssertionStatus(): HiveConf {
        ClassLoader.getSystemClassLoader().setPackageAssertionStatus("org.apache.hadoop.hive.serde2.objectinspector", false)
        return this
    }

    private fun HiveConf.configureSupportConcurrency(): HiveConf {
        this.setBoolVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.HIVE_SUPPORT_CONCURRENCY, false)
        return this
    }

    private fun HiveConf.configureMetaStore(): HiveConf {
        val jdbcDriver = JDBCDriver::class.java.name

        try {
            Class.forName(jdbcDriver)
        } catch (var4: ClassNotFoundException) {
            throw RuntimeException(var4)
        }

        this.set("datanucleus.connectiondrivername", jdbcDriver)
        this.set("javax.jdo.option.ConnectionDriverName", jdbcDriver)
        this.set("datanucleus.connectionPoolingType", "None")
        setBoolVar(ConfVars.METASTORE_VALIDATE_CONSTRAINTS, true)
        setBoolVar(ConfVars.METASTORE_VALIDATE_COLUMNS, true)
        setBoolVar(ConfVars.METASTORE_VALIDATE_TABLES, true)

        return this
    }

    private fun HiveConf.configureFileSystem(): HiveConf {
        val metaStorageUrl = "jdbc:hsqldb:mem:" + UUID.randomUUID().toString()

        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.METASTORECONNECTURLKEY, metaStorageUrl + ";create=true")
        this.setBoolVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS, true)

        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.HIVE_JAR_DIRECTORY, hiveFileSystem.tezInstallation)
        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.HIVE_USER_INSTALL_DIR, hiveFileSystem.tezInstallation)

        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.METASTOREWAREHOUSE, hiveFileSystem.warehouse)
        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.SCRATCHDIR, hiveFileSystem.scratch)
        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.LOCALSCRATCHDIR, hiveFileSystem.localScratch)
        this.setVar(org.apache.hadoop.hive.conf.HiveConf.ConfVars.HIVEHISTORYFILELOC, hiveFileSystem.history)
        this.set("hadoop.tmp.dir", hiveFileSystem.hadoopTmp)
        this.set("test.log.dir", hiveFileSystem.testLogs)

        return this
    }
}

