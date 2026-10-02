package coach.wiring

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import coach.ai.ApiKeySource
import coach.ai.ConnectionCheck
import coach.ai.ConnectionResult
import coach.ai.OpenAiResponses
import coach.ai.ProviderConfig
import coach.presentation.AiCheckState
import coach.presentation.AiKeyState
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * The learner's API key on this device (14G; `AIAX-v0` §10).
 *
 * The key is encrypted with an AES-GCM key that lives in the Android Keystore and never leaves it; only the ciphertext
 * is written, to the app's no-backup directory, so the key is not in plain preferences or files, not in a backup
 * (`android:allowBackup="false"` as well), and not in the `LFPS-v0` export, which reads the store only. Nothing here
 * logs. Removing the key deletes the ciphertext and the Keystore entry, which returns the app to the null-evaluator
 * path; no learner data is touched.
 */
internal class KeystoreKeySource(directory: File) : ApiKeySource {

    private val file = File(directory, FILE_NAME)

    @Volatile
    private var cached: String? = null

    @Volatile
    private var present = false

    @Volatile
    private var loaded = false

    /** Read once on the store thread at startup, so the main thread only ever reads [present]. */
    fun prime() {
        synchronized(this) {
            present = file.exists()
            loaded = false
        }
    }

    fun isPresent(): Boolean = present

    override fun current(): String? = synchronized(this) {
        if (!loaded) {
            cached = if (file.exists()) runCatching { decrypt(file.readBytes()) }.getOrNull() else null
            loaded = true
        }
        cached
    }

    fun save(key: String) = synchronized(this) {
        require(key.isNotBlank()) { "an empty key is not saved" }
        val temporary = File(file.parentFile, "$FILE_NAME.tmp")
        temporary.writeBytes(encrypt(key))
        if (!temporary.renameTo(file)) {
            temporary.delete()
            error("the key could not be stored")
        }
        cached = key
        loaded = true
        present = true
    }

    fun remove() = synchronized(this) {
        file.delete()
        keyStore().deleteEntry(ALIAS)
        cached = null
        loaded = true
        present = false
    }

    private fun encrypt(key: String): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        val sealed = cipher.doFinal(key.toByteArray(Charsets.UTF_8))
        return byteArrayOf(iv.size.toByte()) + iv + sealed
    }

    private fun decrypt(stored: ByteArray): String {
        val ivLength = stored[0].toInt()
        val iv = stored.copyOfRange(1, 1 + ivLength)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(stored.copyOfRange(1 + ivLength, stored.size)), Charsets.UTF_8)
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun secretKey(): SecretKey {
        (keyStore().getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val ALIAS = "coach.ai.key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val FILE_NAME = "ai_key.bin"
    }
}

/** The AI build's process-wide pieces: the key, the one client every call uses, and the settings behind Profile. */
internal object AiWiring {

    private lateinit var keys: KeystoreKeySource

    val config = ProviderConfig()

    val client: OpenAiResponses by lazy { OpenAiResponses(config, keys) }

    /** Called from `CoachApplication.onCreate`; only resolves the no-backup directory, reads nothing. */
    fun init(context: Context) {
        keys = KeystoreKeySource(context.noBackupFilesDir)
    }

    /** Called on the store thread, where disk reads belong. */
    fun prime() = keys.prime()

    fun keySource(): KeystoreKeySource = keys

    val settings: AiSettings = object : AiSettings {
        override fun providerName(): String = config.displayName
        override fun state(): AiKeyState = if (keys.isPresent()) AiKeyState.KEY_SAVED else AiKeyState.NO_KEY
        override fun save(key: String) = keys.save(key)
        override fun remove() = keys.remove()
        override fun check(): AiCheckState = when (ConnectionCheck(client).run()) {
            ConnectionResult.WORKS -> AiCheckState.WORKS
            ConnectionResult.NO_KEY -> AiCheckState.NO_KEY
            ConnectionResult.KEY_REJECTED -> AiCheckState.KEY_REJECTED
            ConnectionResult.UNREACHABLE -> AiCheckState.UNREACHABLE
            ConnectionResult.TIMED_OUT -> AiCheckState.TIMED_OUT
            ConnectionResult.UNEXPECTED_REPLY -> AiCheckState.UNEXPECTED_REPLY
        }
    }
}

internal fun initAi(context: Context) = AiWiring.init(context)
internal fun primeAi() = AiWiring.prime()
internal fun provideAiSettings(): AiSettings = AiWiring.settings
