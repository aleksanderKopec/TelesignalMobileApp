package com.example.telesignal.data.cipher

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.*
import java.security.KeyStore.PrivateKeyEntry
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class CipherUtils {

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    private val rsaKeyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)

    private val rsaKpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE
    )
    private val aesKpg =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)

    private val rsaPublicKey: PublicKey
    private val rsaPrivateKey: PrivateKey
    private val aesKey: SecretKey = aesKpg.generateKey()

    init {
        keyStore.load(null)
        rsaKpg.initialize(
                KeyGenParameterSpec.Builder(
                        RSA_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                        .setDigests(KeyProperties.DIGEST_SHA512)
                        .setKeySize(RSA_KEY_SIZE)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build()
        )
        aesKpg.init(AES_KEY_SIZE)
    }

    init {
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            rsaKpg.generateKeyPair()
        }
        val entry = keyStore.getEntry(RSA_KEY_ALIAS, null)
        rsaPublicKey = keyStore.getCertificate(RSA_KEY_ALIAS).publicKey
        rsaPrivateKey = (entry as PrivateKeyEntry).privateKey
    }

    fun decryptFromBase64(data: String, key: Key, algorithm: String): String {
        val cipher: Cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    fun encryptToBase64(data: String, key: Key, algorithm: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun getRsaKeyPair(): RsaKeyPair {
        return RsaKeyPair(
                rsaPublicKey,
                rsaPrivateKey
        )
    }

    fun getAesKey(): SecretKey {
        return aesKey
    }

    fun getAesKeyAsString(): String {
        return Base64.encodeToString(aesKey.encoded, Base64.DEFAULT)
    }

    fun parsePublicKey(key: String): PublicKey {
        val encodedKey = Base64.decode(key, Base64.DEFAULT)
        val publicKeySpec = X509EncodedKeySpec(encodedKey)
        return rsaKeyFactory.generatePublic(publicKeySpec);
    }

    fun parseAesKey(key: String): SecretKey {
        val encodedKey = Base64.decode(key, Base64.DEFAULT)
        return SecretKeySpec(encodedKey, KeyProperties.KEY_ALGORITHM_AES)
    }


    companion object {

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val RSA_KEY_ALIAS = "RSA_KEY"
        private const val RSA_KEY_SIZE = 4096
        private const val AES_KEY_SIZE = 256
        const val AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING"
        const val RSA_CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
    }

    data class RsaKeyPair(val publicKey: PublicKey, val privateKey: PrivateKey)
}