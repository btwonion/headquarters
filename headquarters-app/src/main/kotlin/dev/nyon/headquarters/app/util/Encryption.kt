package dev.nyon.headquarters.app.util

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun encryptBlowfish(key: String, input: String): String {
    val keySpec = SecretKeySpec(key.toByteArray(), "Blowfish")
    val cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
    return Base64.getEncoder().encodeToString(encrypted)
}

fun decryptBlowfish(key: String, encrypted: String): String {
    val keySpec = SecretKeySpec(key.toByteArray(), "Blowfish")
    val cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    val decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted))
    return String(decrypted, Charsets.UTF_8)
}