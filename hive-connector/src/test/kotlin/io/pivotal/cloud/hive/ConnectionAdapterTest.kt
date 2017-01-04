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

package io.pivotal.cloud.hive

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class ConnectionAdapterTest {

    @Test
    fun `handles a uri without username and password`() {
        val uri = "jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp"

        val info = ConnectionAdapter().adapt(uri)

        assertThat(info).isEqualTo(ConnectionInfo(uri, "", ""))
    }

    @Test
    fun `should extract username from uri`() {
        val uri = "jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp?user=test-user"

        val info = ConnectionAdapter().adapt(uri)

        assertThat(info).isEqualTo(ConnectionInfo(
                "jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp",
                "test-user",
                ""))
    }

    @Test
    fun `should extract username and password from uri`() {
        val uri = "jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp?user=test-user&password=test-password"

        val info = ConnectionAdapter().adapt(uri)

        assertThat(info).isEqualTo(ConnectionInfo("jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp",
                "test-user",
                "test-password"))
    }

    @Test
    fun `preserves other query parameters`() {
        val uri = "jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp?foo=bar&user=test-user&baz=bax&password=test-password"

        val info = ConnectionAdapter().adapt(uri)

        assertThat(info).isEqualTo(ConnectionInfo("jdbc:hive2://hiveservice:80/dbname;transportMode=http;httpPath=hiveHttp?foo=bar&baz=bax",
                "test-user",
                "test-password"))
    }
}