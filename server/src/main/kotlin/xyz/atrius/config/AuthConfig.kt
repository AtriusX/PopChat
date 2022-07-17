package xyz.atrius.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

@Configuration
class AuthConfig {

    @Bean
    fun argon2Auth(): Argon2PasswordEncoder =
        Argon2PasswordEncoder()
}