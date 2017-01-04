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
import org.hsqldb.jdbc.JDBCDriver
import java.util.*

class HiveConfig(hiveFileSystem: HiveFileSystem, serviceConfig: ServiceConfig) {

    val configurations = mapOf(
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

            // File System
            METASTORECONNECTURLKEY to "jdbc:hsqldb:mem:${UUID.randomUUID()};create=true",
            HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS to true,
            HIVE_JAR_DIRECTORY to hiveFileSystem.tezInstallation,
            HIVE_USER_INSTALL_DIR to hiveFileSystem.tezInstallation,
            METASTOREWAREHOUSE to hiveFileSystem.warehouse,
            SCRATCHDIR to hiveFileSystem.scratch,
            LOCALSCRATCHDIR to hiveFileSystem.localScratch,
            HIVEHISTORYFILELOC to hiveFileSystem.history)

    fun build(): HiveConf {
        assertDriver()
        return applyConfigurations()
    }

    private fun applyConfigurations(): HiveConf {
        val hiveConf = HiveConf()

        configurations.entries.forEach { entry ->

            val value = entry.value
            when (value) {
                is Int -> hiveConf.setIntVar(entry.key, value)
                is String -> hiveConf.setVar(entry.key, value.toString())
                is Boolean -> hiveConf.setBoolVar(entry.key, value)
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

