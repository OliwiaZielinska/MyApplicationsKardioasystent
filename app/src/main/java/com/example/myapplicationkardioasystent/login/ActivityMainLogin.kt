package com.example.myapplicationkardioasystent.login
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.apps.MainViewApp
import com.example.myapplicationkardioasystent.registation.BaseActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Aktywność obsługująca logowanie użytkownika do aplikacji.
 * Umozliwia użytkownikowi wprowadzenie danych logowania i autoryzacji za pomocą Firebase Authentication.
 */

open class ActivityMainLogin : BaseActivity(){

    private var loginEmailInput: EditText? = null
    private var loginPasswordInput: EditText? = null
    private var loginLogInButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        loginEmailInput = findViewById(R.id.LoginEmailInput)
        loginPasswordInput = findViewById(R.id.LoginPasswordInput)
        loginLogInButton = findViewById(R.id.LoginLogInButton)

        // Setting click listener to this class
        loginLogInButton?.setOnClickListener{
            logInRegisteredUser()
        }
    }

    /**
     * Walidacja danych logowania
     * @return True, jeśli dane logowania są poprawne, w przeciwnym razie False.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(loginEmailInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(loginPasswordInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Wprowadzone dane logowania są poprawne", false)
                true
            }
        }
    }

    /**
     * Logowanie zarejestrowanego użytkownika
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = loginEmailInput?.text.toString().trim { it <= ' ' }
            val password = loginPasswordInput?.text.toString().trim { it <= ' ' }

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar(resources.getString(R.string.login_successfull), false) // text zdefiniowany w res -> values -> strings.xml
                        goToMainActivity(email)
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    // Przejście do aktywności głównej
    open fun goToMainActivity(email: String) {
        val user = FirebaseAuth.getInstance().currentUser

        //Przekazanie wartości uid
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", email)
        startActivity(intent)
    }
}