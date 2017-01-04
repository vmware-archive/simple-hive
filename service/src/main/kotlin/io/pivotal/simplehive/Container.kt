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
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create HiveServer :" + e.message, e)
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
