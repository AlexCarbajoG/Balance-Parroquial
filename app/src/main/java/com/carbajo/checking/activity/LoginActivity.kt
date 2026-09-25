package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.carbajo.checking.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailButton: Button
    private lateinit var googleButton: Button
    private lateinit var progress: ProgressBar

    private val googleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val account = try { GoogleSignIn.getSignedInAccountFromIntent(result.data).result } catch (_: Exception) { null }
        val token = account?.idToken
        if (token == null) {
            setBusy(false)
            if (result.resultCode != RESULT_CANCELED) showError("No se pudo iniciar sesión con Google")
            return@registerForActivityResult
        }
        auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null))
            .addOnCompleteListener(this) { task ->
                setBusy(false)
                if (task.isSuccessful) openMain() else showError(task.exception?.localizedMessage ?: "No se pudo iniciar sesión con Google")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) { openMain(); return }
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        emailButton = findViewById(R.id.loginEmailButton)
        googleButton = findViewById(R.id.loginGoogleButton)
        progress = findViewById(R.id.loginProgress)

        emailButton.setOnClickListener {
            val address = email.text.toString().trim()
            val secret = password.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                email.error = "Ingresa un correo válido"
                return@setOnClickListener
            }
            if (secret.isEmpty()) {
                password.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }
            setBusy(true)
            auth.signInWithEmailAndPassword(address, secret).addOnCompleteListener(this) { task ->
                setBusy(false)
                if (task.isSuccessful) openMain() else showError("Correo o contraseña incorrectos")
            }
        }

        googleButton.setOnClickListener {
            val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
            if (resourceId == 0) {
                showError("Falta configurar Google en Firebase y actualizar google-services.json")
                return@setOnClickListener
            }
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(resourceId)).requestEmail().build()
            setBusy(true)
            googleResult.launch(GoogleSignIn.getClient(this, options).signInIntent)
        }
    }

    private fun setBusy(busy: Boolean) {
        progress.visibility = if (busy) View.VISIBLE else View.GONE
        emailButton.isEnabled = !busy
        googleButton.isEnabled = !busy
    }

    private fun showError(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }
}
