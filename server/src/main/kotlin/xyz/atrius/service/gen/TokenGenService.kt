package xyz.atrius.service.gen

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*

@Service
class TokenGenService(
    private val secureRandom: SecureRandom,
    @Qualifier("URLEncoder")
    private val encoder: Base64.Encoder,
) {

    fun generateToken(length: Int = 32): String {
        if (length <= 0) {
            return ""
        }
        val size = length / 4 * 3
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return encoder.encodeToString(bytes)
    }
}