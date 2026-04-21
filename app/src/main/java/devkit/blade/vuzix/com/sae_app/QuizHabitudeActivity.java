package devkit.blade.vuzix.com.sae_app;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.ArrayList;
import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizHabitudesItem;
import devkit.blade.vuzix.com.sae_app.retrofit.QuizHabitudeApi;
import devkit.blade.vuzix.com.sae_app.retrofit.RetrofitClient;
import devkit.blade.vuzix.com.sae_app.retrofit.UserApi;
import devkit.blade.vuzix.com.sae_app.model.ScorePayload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizHabitudeActivity extends ActionMenuActivity {

    // Vues
    private TextView questionText;
    private TextView scoreText;

    // Liste des questions chargées depuis l'API
    private List<QuizHabitudesItem> questions;

    // Index et score courant
    private int currentIndex = 0;
    private int cumulativeScore = 0;

    // Indique si la question a été répondue et si on affiche l'écran d'info
    private boolean showingInfo = false;

    // États liés au chargement réseau
    private boolean loading = false;
    private boolean loadError = false;
    private String errorMessage = null;

    // Service Retrofit pour l'API quiz d'habitudes
    private QuizHabitudeApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_habitude);

        // Liaison des vues
        questionText = findViewById(R.id.qt_question);
        scoreText = findViewById(R.id.qt_score);

        // Initialisation des valeurs
        questions = new ArrayList<>();
        cumulativeScore = 0;
        scoreText.setText("Score: " + cumulativeScore);
        questionText.setText("Chargement...");

        // Prépare le client Retrofit et le service API
        apiService = RetrofitClient.getInstance().create(QuizHabitudeApi.class);

        // Démarre le chargement asynchrone des questions
        loadQuestionsFromBackend();
    }

    /**
     * Lance l'appel Retrofit pour récupérer la liste des questions.
     * Met à jour les flags `loading`, `loadError` et `errorMessage`.
     * Met à jour l'UI uniquement si l'Activity n'est pas en train d'être détruite.
     */
    private void loadQuestionsFromBackend() {
        loading = true;
        loadError = false;
        errorMessage = null;

        // Mise à jour de l'UI
        questionText.setText("Chargement...");
        invalidateActionMenu();

        // Effectue un appel réseau asynchrone pour récupérer une liste de QuizHabitudesItem via l'API.
        // Utilise Retrofit pour gérer la requête et la réponse.
        // Le résultat est traité dans les callbacks `onResponse` et `onFailure`.
        Call<List<QuizHabitudesItem>> call = apiService.getQuizHabitude();
        call.enqueue(new Callback<List<QuizHabitudesItem>>() {

            /**
             * Callback appelé lorsque la réponse du serveur est reçue.
             * @param call L'objet Retrofit représentant l'appel réseau.
             * @param response La réponse reçue du serveur, contenant potentiellement les données.
             */
            @Override
            public void onResponse(Call<List<QuizHabitudesItem>> call, Response<List<QuizHabitudesItem>> response) {
                loading = false;

                // Ne pas tenter de mettre à jour l'UI si l'activité est en destruction
                if (!isFinishing() && !isDestroyed()) {
                    if (!response.isSuccessful() || response.body() == null) {
                        loadError = true;
                        errorMessage = "Erreur serveur (code " + response.code() + ")";
                        questionText.setText(errorMessage + ". Appuyez sur Réessayer.");
                        Log.e("QuizHabitude", "Erreur API, code: " + response.code());
                    } else {
                        List<QuizHabitudesItem> body = response.body();
                        if (body.isEmpty()) {
                            questions = new ArrayList<>();
                            questionText.setText("Aucune question disponible");
                        } else {
                            // On récupère directement les QuizHabitudesItem renvoyés par l'API
                            questions = body;
                            currentIndex = 0;
                            cumulativeScore = 0;
                            displayQuestion();
                        }
                    }

                    // Force la reconstruction du menu d'action pour refléter l'état courant
                    invalidateActionMenu();
                }
            }

            @Override
            public void onFailure(Call<List<QuizHabitudesItem>> call, Throwable t) {
                loading = false;
                loadError = true;
                errorMessage = "Erreur réseau. Vérifiez votre connexion.";
                Log.e("QuizHabitude", "Erreur réseau", t);

                if (!isFinishing() && !isDestroyed()) {
                    questionText.setText(errorMessage + " Appuyez sur Réessayer.");
                    invalidateActionMenu();
                }
            }
        });
    }

    /**
     * Affiche la question courante et met à jour le score affiché.
     * Si aucune question n'est disponible, affiche un message adapté.
     */
    private void displayQuestion() {
        if (questions == null || questions.isEmpty() || currentIndex < 0 || currentIndex >= questions.size()) {
            questionText.setText("Aucune question disponible");
            scoreText.setText("Score: " + cumulativeScore);
            return;
        }

        QuizHabitudesItem q = questions.get(currentIndex);
        // Accès direct aux champs du modèle (vérifier null au besoin)
        questionText.setText(q.question);
        scoreText.setText("Score: " + cumulativeScore);
        showingInfo = false;

        // Met à jour le menu (onCreateActionMenu sera appelé)
        invalidateActionMenu();
    }

    /**
     * Affiche l'information associée à la réponse sélectionnée.
     * Si l'information est vide, affiche un texte générique.
     */
    private void showInfo() {
        showingInfo = true;
        if (questions == null || questions.isEmpty() || currentIndex < 0 || currentIndex >= questions.size()) {
            questionText.setText("Réponse enregistrée");
            return;
        }
        String info = questions.get(currentIndex).infos;
        if (info == null || info.trim().isEmpty()) info = "Réponse enregistrée";
        questionText.setText(info);

        // Met à jour le menu pour afficher l'item Continuer
        invalidateActionMenu();
    }

    /**
     * Construit dynamiquement le menu d'action en fonction de l'état :
     * - chargement : item désactivé \"Chargement...\"
     * - erreur : item \"Réessayer\"
     * - affichage info : item \"Continuer\" pour passer à la question suivante
     * - sinon : trois réponses actives (A/B/C) avec gestion du score
     */
    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);
        menu.clear();

        // Si en cours de chargement, afficher un item non cliquable
        if (loading) {
            menu.add(0, 0, 0, "Chargement...").setEnabled(false);
            return true;
        }

        // Si erreur de chargement, proposer Réessayer
        if (loadError) {
            menu.add(0, 0, 0, "Réessayer").setOnMenuItemClickListener(item -> {
                loadQuestionsFromBackend();
                return true;
            });
            return true;
        }

        if (questions == null || questions.isEmpty()) {
            menu.add(0, 0, 0, "Aucune question").setEnabled(false);
            return true;
        }

        if (currentIndex < 0 || currentIndex >= questions.size()) {
            menu.add(0, 0, 0, "Aucune question").setEnabled(false);
            return true;
        }

        // Si on affiche l'info après une réponse, proposer Continuer
        if (showingInfo) {
            menu.add(0, 0, 0, "Continuer").setOnMenuItemClickListener(item -> {
                // Passe à la question suivante
                currentIndex++;
                if (currentIndex < questions.size()) {
                    displayQuestion();
                } else {
                    // Envoie le score final au backend (liste qh_score_history)
                    // Calcul du nombre de points maximum possible (somme des meilleures réponses pour chaque question)
                    int maxPoints = 0;
                    if (questions != null) {
                        for (QuizHabitudesItem qi : questions) {
                            if (qi != null) {
                                int m = Math.max(qi.scoreA, Math.max(qi.scoreB, qi.scoreC));
                                maxPoints += m;
                            }
                        }
                    }

                    double scaled = 0.0;
                    if (maxPoints > 0) {
                        scaled = ((double) cumulativeScore / (double) maxPoints) * 10.0;
                    }
                    int scaledInt = (int) Math.ceil(scaled);
                    String s = String.format(java.util.Locale.getDefault(), "%d/10", scaledInt);

                    // Envoie du score converti (entier, arrondi vers le haut) au backend
                    sendScoreToBackend("qh_score_history", scaledInt);

                    Toast.makeText(QuizHabitudeActivity.this, "Quiz terminé! Score final: " + s + " (" + cumulativeScore + "/" + maxPoints + ")", Toast.LENGTH_LONG).show();
                    finish();
                }
                return true;
            });
            return true;
        }

        // Sinon, afficher les 3 réponses comme items d'action menu
        QuizHabitudesItem q = questions.get(currentIndex);

        String ansA = q.answerA != null ? q.answerA : "Réponse A";
        String ansB = q.answerB != null ? q.answerB : "Réponse B";
        String ansC = q.answerC != null ? q.answerC : "Réponse C";

        menu.add(0, 0, 0, ansA).setOnMenuItemClickListener(item -> {
            cumulativeScore += q.scoreA;
            scoreText.setText("Score: " + cumulativeScore);
            Toast.makeText(QuizHabitudeActivity.this, "Vous avez obtenu " + q.scoreA + " points", Toast.LENGTH_SHORT).show();
            showInfo();
            return true;
        });

        menu.add(0, 1, 0, ansB).setOnMenuItemClickListener(item -> {
            cumulativeScore += q.scoreB;
            scoreText.setText("Score: " + cumulativeScore);
            Toast.makeText(QuizHabitudeActivity.this, "Vous avez obtenu " + q.scoreB + " points", Toast.LENGTH_SHORT).show();
            showInfo();
            return true;
        });

        menu.add(0, 2, 0, ansC).setOnMenuItemClickListener(item -> {
            cumulativeScore += q.scoreC;
            scoreText.setText("Score: " + cumulativeScore);
            Toast.makeText(QuizHabitudeActivity.this, "Vous avez obtenu " + q.scoreC + " points", Toast.LENGTH_SHORT).show();
            showInfo();
            return true;
        });

        return true;
    }

    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    /**
     * Envoie un score au backend en postant un objet JSON via l'API utilisateur.
     * Ne bloque pas l'UI : la requête est asynchrone et les erreurs sont simplement loggées.
     */
    private void sendScoreToBackend(String listName, int score10) {
        try {
            UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
            ScorePayload payload = new ScorePayload(listName, score10, System.currentTimeMillis());
            userApi.postScore(payload).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.w("QuizHabitude", "Envoi du score non réussi, code=" + response.code());
                    } else {
                        Log.i("QuizHabitude", "Score envoyé: " + listName + "=" + score10);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("QuizHabitude", "Erreur envoi score", t);
                }
            });
        } catch (Exception e) {
            Log.e("QuizHabitude", "Impossible d'envoyer le score", e);
        }
    }
}
