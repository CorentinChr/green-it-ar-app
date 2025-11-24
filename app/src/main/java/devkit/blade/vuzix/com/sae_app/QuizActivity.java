package devkit.blade.vuzix.com.sae_app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.ArrayList;
import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizItem;
import devkit.blade.vuzix.com.sae_app.retrofit.QuizApi;
import devkit.blade.vuzix.com.sae_app.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends ActionMenuActivity {

    private TextView questionText;
    private boolean quizLoaded = false;

    private List<QuizItem> quiz;
    private int currentQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.quiz_question);

        loadQuiz();
    }

    private void loadQuiz() {

        QuizApi api = RetrofitClient.getInstance().create(QuizApi.class);

        api.getQuiz().enqueue(new Callback<List<QuizItem>>() {
            @Override
            public void onResponse(Call<List<QuizItem>> call, Response<List<QuizItem>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(QuizActivity.this, "Erreur API", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                quiz = response.body();
                quizLoaded = true;

                setQuestion();
                invalidateActionMenu();
            }

            @Override
            public void onFailure(Call<List<QuizItem>> call, Throwable t) {
                Log.e("QUIZ", "Erreur réseau", t);
                Toast.makeText(QuizActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                finish();
            }

        });
    }

    private void setQuestion() {
        questionText.setText(quiz.get(currentQuestion).question);
    }

    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);

        // Si le quiz n'est pas encore chargé, on s'assure que le menu est vide mais on retourne true
        if (!quizLoaded) {
            menu.clear();
            return true;
        }

        menu.clear();

        List<String> answers = quiz.get(currentQuestion).answers;

        for (int i = 0; i < answers.size(); i++) {

            final int answerIndex = i;

            menu.add(0, i, 0, answers.get(i))
                    .setOnMenuItemClickListener(item -> {

                        if (answerIndex == quiz.get(currentQuestion).correctIndex) {
                            currentQuestion++;

                            if (currentQuestion < quiz.size()) {
                                setQuestion();
                                invalidateActionMenu();
                            } else {
                                Toast.makeText(QuizActivity.this, "Quiz terminé !", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } else {
                            Toast.makeText(QuizActivity.this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
                        }

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
