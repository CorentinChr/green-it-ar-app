package devkit.blade.vuzix.com.sae_app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // Toutes les questions reçues depuis le backend (avant filtrage)
    private List<QuizItem> allQuiz;

    // Liste des questions filtrées selon le thème choisi
    private List<QuizItem> quiz;

    // Liste des thèmes disponibles (extraites de allQuiz)
    private List<String> themes;

    // Mode courant : true = on attend que l'utilisateur choisisse un thème
    private boolean choosingTheme = true;

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

        // Indicateur visuel initial
        questionText.setText("Chargement des thèmes...");

        // Lance la requête réseau pour charger le quiz
        loadQuiz();
    }

    /**
     * Charge le quiz depuis le backend via Retrofit.
     * - En cas de succès : stocke la liste, construit la liste des thèmes et invalide le menu.
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

                // On récupère la liste complète des questions
                allQuiz = response.body();

                // Extrait les thèmes distincts
                Set<String> themeSet = new HashSet<>();
                for (QuizItem item : allQuiz) {
                    // Supposition : QuizItem a un champ public `theme` (String). Adapter si nécessaire.
                    String t = null;
                    try {
                        t = item.getTheme();
                    } catch (Exception ignored) {
                        // Si le champ n'existe pas, on ignore et utilisera une valeur par défaut plus bas
                    }

                    if (t == null || t.trim().isEmpty()) {
                        t = "Sans thème";
                    }
                    themeSet.add(t);
                }

                themes = new ArrayList<>(themeSet);
                Collections.sort(themes);

                // Indique que les données sont prêtes et qu'on doit afficher la sélection de thème
                quizLoaded = true;
                choosingTheme = true;

                // Met à jour le texte central pour inviter à choisir un thème
                if (themes.isEmpty()) {
                    questionText.setText("Aucun thème disponible");
                } else {
                    questionText.setText("Choisissez un thème avec le pad");
                }

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
        if (quiz == null || quiz.isEmpty() || currentQuestion < 0 || currentQuestion >= quiz.size()) {
            questionText.setText("Aucune question disponible");
            return;
        }
        questionText.setText(quiz.get(currentQuestion).question);
    }

    // Affiche l'information liée à la question actuelle (après bonne réponse)
    private void showInfo() {
        showingInfo = true;
        if (quiz == null || quiz.isEmpty() || currentQuestion < 0 || currentQuestion >= quiz.size()) {
            questionText.setText("Bonne réponse !");
            return;
        }
        String info = quiz.get(currentQuestion).infos;
        if (info == null || info.trim().isEmpty()) {
            info = "Bonne réponse !"; // fallback
        }
        questionText.setText(info);
    }

    /**
     * Méthode appelée par le framework Vuzix pour (re)créer le menu ActionMenu.
     * Ici nous construisons dynamiquement les items soit pour la sélection de thème, soit
     * pour les réponses de la question courante.
     */
    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);

        // Nettoie les anciens items avant de recréer
        menu.clear();

        // Tant que les données ne sont pas chargées, affiche un item non cliquable pour forcer l'affichage
        if (!quizLoaded) {
            menu.add(0, 0, 0, "Chargement...").setEnabled(false);
            return true;
        }

        // Si nous sommes en train de choisir un thème, afficher la liste des thèmes
        if (choosingTheme) {
            if (themes == null || themes.isEmpty()) {
                menu.add(0, 0, 0, "Aucun thème").setEnabled(false);
                return true;
            }

            for (int i = 0; i < themes.size(); i++) {
                final String theme = themes.get(i);
                menu.add(0, i, 0, theme)
                        .setOnMenuItemClickListener(item -> {
                            // Filtre les questions selon le thème choisi
                            quiz = new ArrayList<>();
                            for (QuizItem it : allQuiz) {
                                String t = null;
                                try {
                                    t = it.getTheme();
                                } catch (Exception ignored) {
                                }
                                if (t == null || t.trim().isEmpty()) {
                                    t = "Sans thème";
                                }
                                if (t.equals(theme)) {
                                    quiz.add(it);
                                }
                            }

                            if (quiz.isEmpty()) {
                                Toast.makeText(QuizActivity.this, "Aucune question pour ce thème", Toast.LENGTH_SHORT).show();
                                return true;
                            }

                            // Lance le quiz
                            Collections.shuffle(quiz);
                            currentQuestion = 0;
                            choosingTheme = false;
                            setQuestion();
                            invalidateActionMenu();
                            return true;
                        });
            }

            return true;
        }

        // Si on affiche l'info après bonne réponse
        if (showingInfo) {
            // Affiche un seul item Continuer pendant que l'utilisateur lit l'info
            menu.add(0, 0, 0, "Continuer")
                    .setOnMenuItemClickListener(item -> {
                        // Passe à la question suivante
                        currentQuestion++;

                        if (quiz != null && currentQuestion < quiz.size()) {
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
        if (quiz == null || quiz.isEmpty() || currentQuestion < 0 || currentQuestion >= quiz.size()) {
            menu.add(0, 0, 0, "Aucune question").setEnabled(false);
            return true;
        }

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
