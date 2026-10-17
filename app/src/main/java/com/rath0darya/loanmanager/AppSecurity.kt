package com.rath0darya.loanmanager

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.MessageDigest

internal fun hashAppPin(pin: String, salt: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest((salt + ":" + pin).toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

internal fun canUseFingerprint(activity: FragmentActivity): Boolean =
    BiometricManager.from(activity).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
        BiometricManager.BIOMETRIC_SUCCESS

internal fun requestFingerprintUnlock(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onFailureCount: (Int) -> Unit,
    onError: (String) -> Unit
) {
    val manager = BiometricManager.from(activity)
    if (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
        onError("Fingerprint is not set up or is unavailable. Enter your 6-digit PIN instead.")
        return
    }
    var failures = 0
    lateinit var prompt: BiometricPrompt
    prompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onSuccess()
            override fun onAuthenticationFailed() {
                failures += 1
                onFailureCount(failures)
                if (failures >= 3) prompt.cancelAuthentication()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_CANCELED
                ) onError(errString.toString())
            }
        }
    )
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock FundNiti")
        .setSubtitle("Touch the fingerprint sensor")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .setNegativeButtonText("Use PIN")
        .build()
    prompt.authenticate(info)
}
