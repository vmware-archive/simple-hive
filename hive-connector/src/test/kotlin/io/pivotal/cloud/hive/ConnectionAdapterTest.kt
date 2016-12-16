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