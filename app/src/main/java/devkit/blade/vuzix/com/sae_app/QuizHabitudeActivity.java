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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizHabitudeActivity extends ActionMenuActivity {

    private TextView questionText;
    private TextView scoreText;

    private List<QuizHabitudesItem> questions;
    private int currentIndex = 0;
    private int cumulativeScore = 0;
    // Indique si la question a été répondue et si on affiche l'écran d'info
    private boolean showingInfo = false;

    // Nouveaux champs pour gestion réseau
    private boolean loading = false;
    private boolean loadError = false;
    private String errorMessage = null;
    private QuizHabitudeApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_habitude);

        questionText = findViewById(R.id.qt_question);
        scoreText = findViewById(R.id.qt_score);

        // Initialisation
        questions = new ArrayList<>();
        cumulativeScore = 0;
        scoreText.setText("Score: " + cumulativeScore);
        questionText.setText("Chargement...");

        // Prépare le service Retrofit
        apiService = RetrofitClient.getInstance().create(QuizHabitudeApi.class);

        // Lance le chargement depuis le backend au lieu de données hardcodées
        loadQuestionsFromBackend();
    }

    private void loadQuestionsFromBackend() {
        loading = true;
        loadError = false;
        errorMessage = null;

        // Mise à jour de l'UI
        questionText.setText("Chargement...");
        invalidateActionMenu();

        Call<List<QuizHabitudesItem>> call = apiService.getQuizHabitude();
        call.enqueue(new Callback<List<QuizHabitudesItem>>() {
            @Override
            public void onResponse(Call<List<QuizHabitudesItem>> call, Response<List<QuizHabitudesItem>> response) {
                loading = false;

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

    private void displayQuestion() {
        if (questions == null || questions.isEmpty() || currentIndex < 0 || currentIndex >= questions.size()) {
            questionText.setText("Aucune question disponible");
            scoreText.setText("Score: " + cumulativeScore);
            return;
        }

        QuizHabitudesItem q = questions.get(currentIndex);
        questionText.setText(q.question);
        scoreText.setText("Score: " + cumulativeScore);
        showingInfo = false;

        // Met à jour le menu (onCreateActionMenu sera appelé)
        invalidateActionMenu();
    }

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
                    Toast.makeText(QuizHabitudeActivity.this, "Quiz terminé! Score final: " + cumulativeScore, Toast.LENGTH_LONG).show();
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
}
