package com.example.myapplicationkardioasystent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.apps.Raports
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Klasa Chatbot odpowiedzialna za wyświetlanie chatu ze sztuczną inteligencją ChatGPT
 * do analizowania pomiarów tętna użytkownika.
 */
class Chatbot : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var txtResponse: TextView
    private lateinit var idTVQuestion: TextView
    private lateinit var etQuestion: TextInputEditText
    private lateinit var backIntoRaports: Button
    /**
     * Metoda wykonywana podczas tworzenia aktywności.
     * Inicjalizuje widoki, pobiera dane użytkownika i dane z Firestore oraz wywołuje metodę do utworzenia chata.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbot)

        etQuestion=findViewById(R.id.etQuestion)
        idTVQuestion=findViewById(R.id.idTVQuestion)
        txtResponse=findViewById(R.id.txtResponse)
        backIntoRaports=findViewById(R.id.backIntoRaports)
        val userId = FirebaseAuth.getInstance().currentUser!!.email

        // Nasłuchiwanie aż użytkownik kliknie przycisk "Wyślij"
        etQuestion.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEND) {
                txtResponse.text = "Proszę czekać.."

                // Usunięcie zbędnych spacji na początku i końcu pytania
                val question = etQuestion.text.toString().trim()

                // Sprawdzenie, czy pytanie nie jest puste
                if (question.isNotEmpty()) {
                    getResponse(question) { response ->
                        runOnUiThread {
                            txtResponse.text = response
                        }
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        // Obsługa przycisku powrót
        backIntoRaports.setOnClickListener {
            openRaportsView(userId.toString())
        }
    }

    /**
     * Metoda do obsługi przycisku "POWRÓT" umożliwiającego wrócenie do okna Raports.
     *
     * @param userID identyfikator użytkownika, który jest przekazywany do głównej aktywności.
     */
    private fun openRaportsView(userID: String) {
        val intent = Intent(this, Raports::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }

    /**
     * Metoda do uzyskiwania odpowiedzi od sztucznej inteligencji ChatGPT.
     *
     * @param question pytanie użytkownika, na które chce on uzyskać odpowiedź
     * @param callback funkcja zwrotna zwierająca odpowiedź na zadane pytanie
     */
    fun getResponse(question: String, callback: (String) -> Unit) {
        idTVQuestion.text = question
        etQuestion.setText("")

        // Klucz API do autoryzacji OpenAI API
        val apiKey = "API_KEY"
        val url = "https://api.openai.com/v1/chat/completions"

        val requestBody = """
            {
            "model": "gpt-3.5-turbo",
            "messages": [
            {"role": "user", "content": "$question"}
            ],
            "max_tokens": 500,
            "temperature": 0
            }
            """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        // Wysłanie żądania asynchronicznego przy użyciu klienta HTTP
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "Żądanie sieciowe nie powiodło się", e)
                runOnUiThread {
                    txtResponse.text = "Błąd sieci: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("error", "Nieoczekiwany kod odpowiedzi: ${response.code}")
                    val errorBody = response.body?.string()
                    Log.e("error", "Treść odpowiedzi: $errorBody")
                    runOnUiThread {
                        txtResponse.text = "Nieoczekiwana odpowiedź: ${response.message}\nKod: ${response.code}\nTreść: $errorBody"
                    }
                    return
                }

                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    Log.e("error", "Odpowiedź jest pusta")
                    runOnUiThread {
                        txtResponse.text = "Otrzymano pustą odpowiedź z serwera"
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(body)
                    val choicesArray = jsonObject.getJSONArray("choices")
                    val message = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")
                    runOnUiThread {
                        callback(message)
                    }
                } catch (e: Exception) {
                    Log.e("error", "Błąd parsowania JSON", e)
                    runOnUiThread {
                        txtResponse.text = "Błąd parsowania odpowiedzi: ${e.message}"
                    }
                }
            }
        })
    }
}