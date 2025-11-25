package devkit.blade.vuzix.com.sae_app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.Collections;
import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizItem;
import devkit.blade.vuzix.com.sae_app.retrofit.QuizApi;
import devkit.blade.vuzix.com.sae_app.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activité qui affiche un quiz en utilisant le système d'ActionMenu de Vuzix
// Nous étendons ActionMenuActivity pour réutiliser le menu/actionmenu déjà présent
public class QuizActivity extends ActionMenuActivity {

    // Vue qui affiche le texte de la question ou des infos
    private TextView questionText;

    // Indique si les données du quiz ont été chargées depuis l'API
    private boolean quizLoaded = false;

    // Liste des questions reçues depuis le backend
    private List<QuizItem> quiz;

    // Index de la question courante
    private int currentQuestion = 0;

    // Indique si nous sommes en train d'afficher l'écran d'info après une bonne réponse
    private boolean showingInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Récupération de la vue de la question dans le layout
        questionText = findViewById(R.id.quiz_question);

        // Lance la requête réseau pour charger le quiz
        loadQuiz();
    }

    /**
     * Charge le quiz depuis le backend via Retrofit.
     * - En cas de succès : stocke la liste, met à jour l'affichage et invalide le menu (recréé par onCreateActionMenu).
     * - En cas d'échec : affiche un Toast et ferme l'activité.
     */
    private void loadQuiz() {

        // Création de l'API Retrofit (singleton défini dans RetrofitClient)
        QuizApi api = RetrofitClient.getInstance().create(QuizApi.class);

        // Appel asynchrone
        api.getQuiz().enqueue(new Callback<List<QuizItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<QuizItem>> call, @NonNull Response<List<QuizItem>> response) {

                // Vérification de la réponse
                if (!response.isSuccessful() || response.body() == null) {
                    // Erreur côté API / données manquantes -> on avertit et on ferme
                    Toast.makeText(QuizActivity.this, "Erreur API", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // On récupère la liste des questions
                quiz = response.body();
                Collections.shuffle(quiz);
                currentQuestion = 0; // repartir de la première question du nouvel ordre
                quizLoaded = true;

                // Affiche la première question
                setQuestion();

                // Demande la reconstruction du menu d'action : onCreateActionMenu sera appelé
                invalidateActionMenu();
            }

            @Override
            public void onFailure(@NonNull Call<List<QuizItem>> call, @NonNull Throwable t) {
                // Erreur réseau
                Log.e("QUIZ", "Erreur réseau", t);
                Toast.makeText(QuizActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                finish();
            }

        });
    }

    // Met à jour le TextView avec la question courante
    private void setQuestion() {
        showingInfo = false;
        questionText.setText(quiz.get(currentQuestion).question);
    }

    // Affiche l'information liée à la question actuelle (après bonne réponse)
    private void showInfo() {
        showingInfo = true;
        String info = quiz.get(currentQuestion).infos;
        if (info == null || info.trim().isEmpty()) {
            info = "Bonne réponse !"; // fallback
        }
        questionText.setText(info);
    }

    /**
     * Méthode appelée par le framework Vuzix pour (re)créer le menu ActionMenu.
     * Ici nous construisons dynamiquement les items du menu à partir des réponses de la question courante.
     * Chaque item utilise setOnMenuItemClickListener pour gérer la sélection via le trackpad.
     *
     * Comportement modifié :
     * - Si `showingInfo` est vrai : afficher un seul item « Continuer »
     *   qui, lorsqu'il est sélectionné, passe à la question suivante.
     */
    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);

        // Si le quiz n'est pas encore chargé, on s'assure que le menu est vide mais on retourne true
        if (!quizLoaded) {
            menu.clear();
            return true;
        }

        // Nettoie les anciens items avant de recréer ceux de la question courante
        menu.clear();

        if (showingInfo) {
            // Affiche un seul item Continuer pendant que l'utilisateur lit l'info
            menu.add(0, 0, 0, "Continuer")
                    .setOnMenuItemClickListener(item -> {
                        // Passe à la question suivante
                        currentQuestion++;

                        if (currentQuestion < quiz.size()) {
                            setQuestion();
                            invalidateActionMenu();
                        } else {
                            Toast.makeText(QuizActivity.this, "Quiz terminé !", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        return true;
                    });

            return true;
        }

        // Récupère la liste des réponses pour la question actuelle
        List<String> answers = quiz.get(currentQuestion).answers;

        for (int i = 0; i < answers.size(); i++) {

            final int answerIndex = i; // utilisé dans le listener lambda

            // Ajoute un item de menu pour chaque réponse
            menu.add(0, i, 0, answers.get(i))
                    .setOnMenuItemClickListener(item -> {

                        // Si l'index sélectionné correspond à la bonne réponse
                        if (answerIndex == quiz.get(currentQuestion).correctIndex) {
                            // Affiche l'information associée à la question et propose Continuer
                            showInfo();
                            invalidateActionMenu();

                        } else {
                            // Mauvaise réponse -> petit feedback
                            Toast.makeText(QuizActivity.this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
                        }

                        // Indique que l'événement a été consumé
                        return true;
                    });
        }

        return true;
    }


    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }
}
