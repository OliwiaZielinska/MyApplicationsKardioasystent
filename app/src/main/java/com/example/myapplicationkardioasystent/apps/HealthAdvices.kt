package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.firebase.auth.FirebaseAuth
/**
 * HealthAdvices - Aktywność odpowiedzialna za wyświetlanie porad zdrowotnych dla użytkowników
 * w formie losowej porady na temat zdrowia serca oraz zdrowego trybu życia.
 * Użytkownik może losować kolejne porady i wrócić do głównej aktywności aplikacji.
 */
class HealthAdvices : AppCompatActivity() {
    private lateinit var adviceTextView: TextView
    private val healthTips = listOf(
        "Pij co najmniej 2 litry wody dziennie, aby utrzymać odpowiednie nawodnienie organizmu.",
        "Codziennie spaceruj przez minimum 30 minut, aby poprawić krążenie krwi.",
        "Zrezygnuj z palenia tytoniu - to największy wróg dlla serca",
        "Regularnie monitoruj poziom cholesterolu we krwi.",
        "Zadbaj o zdrową dietę bogatą w błonnik, owoce i warzywa.",
        "Ogranicz spożycie soli, aby zmniejszyć ryzyko nadciśnienia.",
        "Unikaj stresu - relaks i odpoczynek są kluczowe dla serca.",
        "Wprowadź ćwiczenia aerobowe, np. jogging, pływanie lub jazdę na rowerze.",
        "Ogranicz spożycie alkoholu do minimalnych ilości.",
        "Pamiętaj o regularnym badaniu poziomu cukru we krwi.",
        "Jedz tłuste ryby, takie jak łosoś, które zawierają korzystne kwasy tłuszczowe omega-3.",
        "Śpij co najmniej 7 godzin dziennie dla pełnej regeneracji organizmu.",
        "Unikaj przetworzonych produktów spożywczych.",
        "Utrzymuj zdrową wagę ciała - otyłość jest jednym z czynników ryzyka chorób serca.",
        "Dodaj do diety orzechy i nasiona, które są bogate w zdrowe tłuszcze.",
        "Sprawdzaj regularnie ciśnienie krwi i tętno.",
        "Zastąp smażenie gotowaniem lub pieczeniem potraw.",
        "Wykonuj ćwiczenia oddechowe, aby obniżyć poziom stresu.",
        "Unikaj napojów gazowanych i słodkich.",
        "Zadbaj o zdrowy poziom witaminy D, wystawiając się na słońce lub suplementując ją.",
        "Praktykuj medytację lub jogę dla poprawy zdrowia psychicznego i fizycznego.",
        "Pamiętaj o regularnym odpoczynku i krótkich przerwach w pracy.",
        "Unikaj siedzącego trybu życia - poruszaj się co godzinę.",
        "Spożywaj pokarmy bogate w potas, jak banany i pomidory.",
        "Wybieraj chude mięsa, takie jak kurczak lub indyk.",
        "Ogranicz spożycie tłuszczów nasyconych, np. masła.",
        "Jedz dużo warzyw liściastych, np. szpinaku.",
        "Ogranicz kawę i inne napoje z kofeiną.",
        "Regularnie sprawdzaj poziom elektrolitów we krwi.",
        "Pij zieloną herbatę, która ma właściwości przeciwutleniające.",
        "Zawsze czytaj etykiety i unikaj produktów z dużą ilością konserwantów.",
        "Używaj oliwy z oliwek zamiast olejów roślinnych.",
        "Dodaj awokado do swojej diety - jest źródłem zdrowych tłuszczów.",
        "Wybieraj produkty pełnoziarniste zamiast przetworzonych.",
        "Pamiętaj o dobrym śnie, bo niedosypianie wpływa na zdrowie serca.",
        "Jedz jagody i inne owoce leśne bogate w antyoksydanty.",
        "Ćwicz codziennie rozciąganie - wpływa to korzystnie na krążenie.",
        "Unikaj dużych ilości cukru - prowadzi do otyłości i cukrzycy.",
        "Utrzymuj aktywność fizyczną w codziennym harmonogramie.",
        "Zadbaj o regularne spożywanie zdrowych tłuszczów.",
        "Dbaj o higienę jamy ustnej - zdrowe dziąsła to także zdrowe serce.",
        "Ogranicz spożywanie przetworzonych mięs, jak kiełbasy i parówki.",
        "Zastąp słodycze owocami.",
        "Znajdź czas na aktywność fizyczną z rodziną lub przyjaciółmi.",
        "Regularnie badaj się u lekarza.",
        "Dla serca polecana jest dieta śródziemnomorska i jej odmiana – dieta DASH.",
        "Nie podjadaj między posiłkami.",
        "Unikaj produktów typu fast-food.",
        "Pij wystarczającą ilość płynów, zwłaszcza w upalne dni."
    )
    /**
     * Metoda onCreate inicjalizuje komponenty widoku oraz ustawia przyciski losujące porady
     * i powracające do głównego widoku aplikacji.
     * @param savedInstanceState - Zapisany stan instancji aktywności.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_advices)

        adviceTextView = findViewById(R.id.adviceTextView)

        val drawAdviceButton = findViewById<Button>(R.id.drawAdviceButton)
        drawAdviceButton.setOnClickListener {
            val randomAdvice = healthTips.random()
            adviceTextView.text = randomAdvice
        }

        val userId = FirebaseAuth.getInstance().currentUser!!.email

        val backGuideButton = findViewById<Button>(R.id.backGuideButton)
        backGuideButton.setOnClickListener {
            openMainActivity(userId.toString())
        }
    }
    /**
     * Metoda openMainActivity otwiera główną aktywność aplikacji i przekazuje identyfikator użytkownika.
     * @param userID - Adres e-mail użytkownika aktualnie zalogowanego.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}