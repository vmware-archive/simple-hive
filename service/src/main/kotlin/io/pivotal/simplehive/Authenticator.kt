package io.pivotal.simplehive

import org.apache.hive.service.auth.PasswdAuthenticationProvider
import javax.security.sasl.AuthenticationException

class Authenticator : PasswdAuthenticationProvider {

    override fun Authenticate(user: String?, password: String?) {
        val adminConfig = SpringContextBridge.springContextBridge().adminConfig

        if (user != adminConfig.username || password != adminConfig.password) {
            throw AuthenticationException()
        }
    }
}