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
import io.pivotal.simplehive.config.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SimpleHiveApplication : ApplicationRunner {

    @Autowired
    private lateinit var serviceConfig: ServiceConfig

    override fun run(args: ApplicationArguments) {

        val hiveServerContainer = Container(HiveConfig(HiveFileSystem(), serviceConfig))

        hiveServerContainer.init()

        hiveServerContainer.start()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SimpleHiveApplication::class.java, *args)
}
