package com.jaco.cc3d.data.local.preferences

//import com.jaco.cc3d.data.model.AuthTokens
import com.jaco.cc3d.data.network.apiAuth.UserData
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// ====================================================================================
// CLASE AUXILIAR: EncryptionUtil para reemplazar EncryptedSharedPreferences (JVM Secure Store)
// NOTA: En un entorno de producción, la clave AES debería obtenerse de forma más segura
// (ej. de la KeyStore de Java o de un archivo de configuración cifrado).
// ====================================================================================

private class EncryptionUtil(keyBase64: String) {
    private val key: SecretKey
    private val algorithm = "AES/CBC/PKCS5Padding"

    init {
        // La clave real se pasa en Base64.
        //val decodedKey = Base64.getDecoder().decode(keyBase64)
        //this.key = SecretKeySpec(decodedKey, "AES")
        //val decodedKey = Base64.getUrlDecoder().decode(keyBase64)
        //this.key = SecretKeySpec(decodedKey, "AES")
        val decodedKey = Base64.getDecoder().decode(keyBase64) // ¡CORREGIDO!
        this.key = SecretKeySpec(decodedKey, "AES")
    }

    /** Cifra el texto con la clave AES, devolviendo IV (Base64) + Cifrado (Base64) */
    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.parameters.getParameterSpec(IvParameterSpec::class.java).iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))

        // Almacenamos el IV y el texto cifrado juntos, ya que el IV es necesario para descifrar.
        val ivBase64 = Base64.getEncoder().encodeToString(iv)
        val encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes)

        return "$ivBase64:$encryptedBase64"
    }

    /** Descifra el texto, esperando el formato "IV_Base64:EncryptedData_Base64" */
    fun decrypt(encryptedData: String): String {
        val parts = encryptedData.split(":")
        if (parts.size != 2) throw IllegalArgumentException("Formato de datos cifrados inválido.")

        val iv = Base64.getDecoder().decode(parts[0])
        val encryptedBytes = Base64.getDecoder().decode(parts[1])

        val cipher = Cipher.getInstance(algorithm)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    companion object {
        /** Genera una clave AES de 256 bits para inicialización (solo para primera ejecución) */
        fun generateKey(): String {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256, SecureRandom())
            return Base64.getEncoder().encodeToString(keyGen.generateKey().encoded)
        }
    }
}


// ====================================================================================
// DATA CLASS: Representa los datos que se guardarán en el archivo JSON cifrado
// ====================================================================================

@Serializable
data class StoredData(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userData: UserData? = null,
    val lastRoleSelected: Int? = null,
    val expiresAt: Long? = null
)


// ====================================================================================
// CLASE PRINCIPAL: EncryptedDesktopTokenManager (compatible con JVM Desktop)
// ====================================================================================

/**
 * Gestor de tokens y datos de sesión diseñado para entornos JVM de escritorio.
 * Utiliza un archivo local cifrado (JSON) para reemplazar EncryptedSharedPreferences.
 */
@Singleton
class EncryptedDesktopTokenManager @Inject constructor() {

    // 🛑 ATENCIÓN: Esta clave es CRÍTICA.
    // Para producción, se debe generar y almacenar de forma segura (ej. Java KeyStore).
    // Usamos una clave fija aquí para la demostración de la lógica.
    // Clave de 256 bits en Base64.
    //private val AES_SECRET_KEY_BASE64 = "YOUR_HARDCODED_256_BIT_AES_KEY_BASE64_HERE=="
    private val AES_SECRET_KEY_BASE64 = "MioaCPIXidUM8qyWiNALs/L91Q5awWAPKILqhhzhqqs="

    private val FILE_PATH = System.getProperty("user.home") + File.separator + ".cc3d_auth.json"
    private val TAG = "DesktopTokenManager"
    private val dataFile = File(FILE_PATH)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    // Inicializamos el utilitario de cifrado.
    private val encryptionUtil = try {
        EncryptionUtil(AES_SECRET_KEY_BASE64)
    } catch (e: Exception) {
        System.err.println("$TAG: ERROR FATAL al inicializar cifrado: ${e.message}")
        throw e
    }

    // Estado en memoria para evitar leer/escribir constantemente el disco.
    private var cachedData: StoredData = loadDataFromFile()


    // --- Métodos de Lectura/Escritura en Disco (Cifrado) ---

    private fun loadDataFromFile(): StoredData {
        if (!dataFile.exists()) {
            return StoredData() // Archivo no existe, devolver estado vacío
        }
        return try {
            val encryptedContent = dataFile.readText()
            val decryptedContent = encryptionUtil.decrypt(encryptedContent)
            json.decodeFromString(decryptedContent)
        } catch (e: Exception) {
            System.err.println("$TAG: Error al leer/descifrar archivo: ${e.message}. Devolviendo datos vacíos.")
            // Si hay un error (corrupción/clave incorrecta), mejor devolver vacío.
            StoredData()
        }
    }

    private fun saveDataToFile(data: StoredData) {
        try {
            val plainContent = json.encodeToString(data)
            val encryptedContent = encryptionUtil.encrypt(plainContent)

            // Asegurarse de que el directorio existe antes de escribir
            dataFile.parentFile?.mkdirs()
            dataFile.writeText(encryptedContent)

            println("$TAG: Datos de sesión guardados exitosamente.")
        } catch (e: Exception) {
            System.err.println("$TAG: ERROR al escribir/cifrar archivo: ${e.message}")
            // Manejo de errores de IO o cifrado
        }
    }

    // --- Métodos de Interfaz (Usan el estado en memoria) ---

    // Método genérico para actualizar el caché y el disco
    private fun updateData(updateFunction: (StoredData) -> StoredData) {
        cachedData = updateFunction(cachedData)
        saveDataToFile(cachedData)
    }

    fun saveAccessToken(token: String) = updateData { it.copy(accessToken = token) }
    fun getAccessToken(): String? = cachedData.accessToken

    fun saveRefreshToken(token: String) = updateData { it.copy(refreshToken = token) }
    fun getRefreshToken(): String? = cachedData.refreshToken

    //fun saveDriverName(driverName: String) = updateData { it.copy(driverName = driverName) }
    //fun getDriverName(): String? = cachedData.driverName


    fun clearTokens() {
        updateData { StoredData() }
        println("$TAG: Tokens de sesión eliminados exitosamente.")
    }

    fun hasTokens(): Boolean {
        return getAccessToken() != null && getRefreshToken() != null
    }

    fun saveUserData(user: UserData) = updateData { it.copy(userData = user) }

    /**
     * Recupera el objeto UserData guardado
     */
    fun getUserData(): UserData? = cachedData.userData

    /**
     * Función de conveniencia: Obtiene la lista de roles directamente
     */
    fun getUserRoles(): List<Int> = cachedData.userData?.role ?: emptyList()

    /**
     * Función de conveniencia: Verifica si el usuario tiene un rol específico
     * 0: Estudiante, 1: Maestro, 2: Admin (según tu lógica)
     */
    fun hasRole(roleId: Int): Boolean = getUserRoles().contains(roleId)

    fun saveLastRole(roleId: Int) = updateData { it.copy(lastRoleSelected = roleId) }
    fun getLastRole(): Int? = cachedData.lastRoleSelected
    fun saveExpiresAt(timestamp: Long) = updateData { it.copy(expiresAt = timestamp) }
    fun getExpiresAt(): Long = cachedData.expiresAt ?: 0L

    fun isSessionValid(): Boolean {
        val expiration = getExpiresAt()
        if (expiration == 0L) return false
        // 🕒 CONVERSIÓN A FORMATO LEGIBLE
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd:MM")
            .withZone(ZoneId.systemDefault())
        val expirationTimeFormatted = formatter.format(Instant.ofEpochMilli(expiration))

        val expirationTimeFormatted2 = formatter.format(Instant.ofEpochMilli(System.currentTimeMillis() + (5 * 60 * 1000)))
        // Imprimir en consola para depuración
        println("$TAG: La sesión expira a las: $expirationTimeFormatted")
        println("$TAG: La sesión expirD a las: $expirationTimeFormatted2")
        // Si la hora actual + 5 minutos es mayor que la expiración, ya no es válida
        return System.currentTimeMillis() + (5 * 60 * 1000) < expiration
    }

    /**
     * Verifica si la PC tiene conexión a internet.
     * Intenta contactar con el DNS de Google (8.8.8.8) con un timeout de 2 segundos.
     */
    fun isOnline(): Boolean {
        return try {
            val socket = Socket()
            // Intentamos conectar al DNS de Google en el puerto 53
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            println("$TAG: [isOnline] Conexión detectada exitosamente.")
            true
        } catch (e: Exception) {
            println("$TAG: [isOnline] Sin conexión: ${e.message}")
            false
        }
    }

    /**
     * Determina si el usuario puede entrar al Dashboard.
     */
    fun canAccessDashboard(): Boolean {
        val hasUser = getUserData() != null
        val isExpired = !isSessionValid()
        val online = isOnline()
        println("isOnline "+online)
        println("isExpired "+isExpired)
        return when {
            // Caso 1: Tiene usuario y el token es vigente (Online u Offline da igual)
            hasUser && !isExpired -> true

            // Caso 2: Tiene usuario, el token expiró pero está OFFLINE
            // Permitimos entrar para ver datos locales (Modo Lectura)
            hasUser && isExpired && !online -> true

            // Caso 3: No hay usuario, o el token expiró y SÍ tiene internet
            else -> false
        }
    }

    suspend fun logout(onClearDatabase: suspend () -> Unit) {
        // PASO 1: Hace lo mismo que hacía clearTokens()
        updateData { StoredData() }

        // PASO 2: Ejecuta lo que TÚ le pidas (limpiar Room)
        onClearDatabase()

        println("$TAG: Sesión y base de datos local limpiadas.")
    }
}