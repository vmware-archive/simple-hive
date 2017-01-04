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

import io.pivotal.simplehive.config.AdminConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class SpringContextBridge
constructor(val adminConfig: AdminConfig) : ApplicationContextAware {

    companion object {
        private lateinit var applicationContext: ApplicationContext

        fun springContextBridge(): SpringContextBridge = applicationContext.getBean(SpringContextBridge::class.java)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContextBridge.applicationContext = applicationContext
    }
}