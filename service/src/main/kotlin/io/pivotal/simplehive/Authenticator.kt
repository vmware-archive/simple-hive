package io.pivotal.simplehive

import org.apache.hive.service.auth.PasswdAuthenticationProvider
import javax.security.sasl.AuthenticationException

class Authenticator : PasswdAuthenticationProvider {

    override fun Authenticate(user: String?, password: String?) {
        if("hive-user" != user || "hive-password" != password) {
            throw AuthenticationException()
        }
    }

}