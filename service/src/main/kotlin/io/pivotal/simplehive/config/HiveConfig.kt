/*
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under
 *
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package io.pivotal.simplehive.config

import io.pivotal.simplehive.HiveFileSystem
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hadoop.hive.conf.HiveConf.ConfVars.*
import org.apache.tez.dag.api.TezConfiguration.*
import org.apache.tez.runtime.library.api.TezRuntimeConfiguration.TEZ_RUNTIME_OPTIMIZE_LOCAL_FETCH
import org.hsqldb.jdbc.JDBCDriver
import java.util.*

class HiveConfig(hiveFileSystem: HiveFileSystem, serviceConfig: ServiceConfig) {

    private val hiveConfDefaults = mapOf(
            // Transport Mode
            HIVE_SERVER2_TRANSPORT_MODE to "http",
            HIVE_SERVER2_THRIFT_HTTP_PORT to serviceConfig.port.toInt(),
            HIVE_SERVER2_THRIFT_HTTP_PATH to serviceConfig.httpPath,

            // Auth
            HIVE_SERVER2_AUTHENTICATION to "CUSTOM",
            HIVE_SERVER2_CUSTOM_AUTHENTICATION_CLASS to "io.pivotal.simplehive.Authenticator",

            // Misc Hive Settings
            HIVESTATSAUTOGATHER to false,
            HIVE_CBO_ENABLED to false,
            HIVE_SERVER2_LOGGING_OPERATION_ENABLED to false,
            HADOOPBIN to "/dev/null",

            // Optimizations
            HIVE_INFER_BUCKET_SORT to false,
            HIVEMETADATAONLYQUERIES to false,
            HIVEOPTINDEXFILTER to false,
            HIVECONVERTJOIN to false,
            HIVESKEWJOIN to false,
            HIVECOUNTERSPULLINTERVAL to 1L,
            HIVE_RPC_QUERY_PLAN to true,

            // File System
            METASTORECONNECTURLKEY to "jdbc:hsqldb:mem:${UUID.randomUUID()};create=true",
            HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS to true,
            HIVE_JAR_DIRECTORY to hiveFileSystem.tezInstallation,
            HIVE_USER_INSTALL_DIR to hiveFileSystem.tezInstallation,
            METASTOREWAREHOUSE to hiveFileSystem.warehouse,
            SCRATCHDIR to hiveFileSystem.scratch,
            LOCALSCRATCHDIR to hiveFileSystem.localScratch,
            HIVEHISTORYFILELOC to hiveFileSystem.history
    )

    private val otherDefaults = mapOf(
            // TEZ Configuration
            TEZ_IGNORE_LIB_URIS to true,
            TEZ_LOCAL_MODE to true,
            "fs.defaultFS" to "file:///",
            TEZ_RUNTIME_OPTIMIZE_LOCAL_FETCH to true,

            // Set to be able to run tests offline
            TEZ_AM_DISABLE_CLIENT_VERSION_CHECK to "true",

            // General attempts to strip of unnecessary functionality to speed up test execution and increase stability
            TEZ_AM_USE_CONCURRENT_DISPATCHER to "false",
            TEZ_AM_CONTAINER_REUSE_ENABLED to "false",
            TEZ_TASK_GET_TASK_SLEEP_INTERVAL_MS_MAX to "1",
            TEZ_AM_WEBSERVICE_ENABLE to "false",
            DAG_RECOVERY_ENABLED to "false",
            TEZ_AM_NODE_BLACKLISTING_ENABLED to "false"
    )

    fun build(): HiveConf {
        assertDriver()
        return applyConfigurations()
    }

    private fun applyConfigurations(): HiveConf {
        val hiveConf = HiveConf()

        hiveConfDefaults.entries.forEach { entry ->
            val value = entry.value

            when (value) {
                is Int -> hiveConf.setIntVar(entry.key, value)
                is Long -> hiveConf.setLongVar(entry.key, value)
                is Boolean -> hiveConf.setBoolVar(entry.key, value)
                is String -> hiveConf.setVar(entry.key, value.toString())
                else -> throw IllegalStateException("Configuration value of unhandled type ${value.javaClass.simpleName}.")
            }
        }

        otherDefaults.entries.forEach { entry ->
            val value = entry.value

            when (value) {
                is Int -> hiveConf.setInt(entry.key, value)
                is Long -> hiveConf.setLong(entry.key, value)
                is Boolean -> hiveConf.setBoolean(entry.key, value)
                is String -> hiveConf.set(entry.key, value.toString())
                else -> throw IllegalStateException("Configuration value of unhandled type ${value.javaClass.simpleName}.")
            }
        }

        return hiveConf
    }

    private fun assertDriver() {
        try {
            Class.forName(JDBCDriver::class.java.name)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }
}

