package xyz.atrius.config

import io.lettuce.core.RedisClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import java.security.SecureRandom
import java.util.*
import java.util.Base64.Encoder

@Configuration
class AuthConfig {

    @Value("\${spring.redis.url}")
    private lateinit var redisURI: String

    @Bean
    fun argon2Auth(): Argon2PasswordEncoder =
        Argon2PasswordEncoder()

    @Bean
    fun redisClient(): RedisClient =
        RedisClient.create(redisURI)

    @Bean
    fun secureRandom(): SecureRandom =
        SecureRandom()

    @Bean
    @Qualifier("URLEncoder")
    fun base64Encoder(): Encoder =
        Base64.getUrlEncoder()
}