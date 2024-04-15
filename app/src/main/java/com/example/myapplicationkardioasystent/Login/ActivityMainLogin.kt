package com.example.myapplicationkardioasystent.Login
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.Apps.MainViewApp
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.Registation.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class ActivityMainLogin : BaseActivity(){

    private var LoginNickInput: EditText? = null
    private var LoginHasloInput: EditText? = null
    private var LoginZalogujSieButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        LoginNickInput = findViewById(R.id.LoginEmailInput)
        LoginHasloInput = findViewById(R.id.LoginHasloInput)
        LoginZalogujSieButton = findViewById(R.id.LoginZalogujSieButton)

        // Setting click listener to this class
        LoginZalogujSieButton?.setOnClickListener{
            logInRegisteredUser()
        }
    }

    // Walidacja danych logowania
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(LoginNickInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(LoginHasloInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    // Logowanie zarejestrowanego użytkownika
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = LoginNickInput?.text.toString().trim() { it <= ' ' }
            val password = LoginHasloInput?.text.toString().trim() { it <= ' ' }

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar(resources.getString(R.string.login_successfull), false) // text zdefiniowany w res -> values -> strings.xml
                        goToMainActivity()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    // Przejście do aktywności głównej
    open fun goToMainActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()

        //Przekazanie wartości uid
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }
}