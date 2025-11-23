package devkit.blade.vuzix.com.sae_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuizActivity extends ActionMenuActivity {

    private TextView questionText;

    private ArrayList<String> questions = new ArrayList<>();
    private ArrayList<ArrayList<String>> answers = new ArrayList<>();
    private ArrayList<Integer> correct = new ArrayList<>();

    private int currentQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.quiz_question);

        new LoadQuizTask().execute("http://10.0.2.2:8080/quiz");
        // ↑ mets ton IP ici
    }

    // ------------------------------
    // 1) Charger le JSON en tâche secondaire
    // ------------------------------
    private class LoadQuizTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();

                parseJson(sb.toString());
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("QUIZ", "Erreur chargement", e);  // <-- AJOUT SUPER IMPORTANT
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(QuizActivity.this, "Erreur lors du chargement du quiz", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            setQuestion();
        }
    }

    // ------------------------------
    // 2) Parser le JSON manuellement
    // ------------------------------
    private void parseJson(String json) throws Exception {
        JSONArray array = new JSONArray(json);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            questions.add(obj.getString("question"));

            JSONArray ansArray = obj.getJSONArray("answers");
            ArrayList<String> ansList = new ArrayList<>();

            for (int j = 0; j < ansArray.length(); j++) {
                ansList.add(ansArray.getString(j));
            }

            answers.add(ansList);
            correct.add(obj.getInt("correctIndex"));
        }
    }

    // ------------------------------
    // 3) Définir la question actuelle
    // ------------------------------
    private void setQuestion() {
        questionText.setText(questions.get(currentQuestion));
        invalidateActionMenu(); // recharge le menu
    }

    // ------------------------------
    // 4) Construire le menu Vuzix
    // ------------------------------
    @Override
    protected boolean onCreateActionMenu(Menu menu) {

        ArrayList<String> ansList = answers.get(currentQuestion);

        for (int i = 0; i < ansList.size(); i++) {
            final int index = i;

            menu.add(Menu.NONE, i, Menu.NONE, ansList.get(i))
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (index == correct.get(currentQuestion)) {
                                currentQuestion++;

                                if (currentQuestion < questions.size()) {
                                    setQuestion();
                                } else {
                                    Toast.makeText(QuizActivity.this, "Quiz terminé !", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(QuizActivity.this, "Mauvaise réponse !", Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        }
                    });
        }

        return true;
    }

    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }
}
