// java
package devkit.blade.vuzix.com.sae_app.qrcode;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import devkit.blade.vuzix.com.sae_app.R;

public class MainMenuActivity extends ActionMenuActivity {
    private TextView questionText;
    private int currentQuestion = 0;
    private final String[] questions = {
            "Quelle est la capitale de la France?",
            "Combien y a-t-il de continents?",
            "Quel est le plus grand océan?"
    };
    private final String[][] answers = {
            {"Paris", "Londres", "Berlin"},
            {"5", "6", "7"},
            {"Atlantique", "Indien", "Pacifique"}
    };
    private final int[] correctAnswers = {0, 2, 2};

    private static final int MENU_ID_SCAN_QR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        questionText = findViewById(R.id.quiz_question);
        setQuestion();
    }

    private void setQuestion() {
        questionText.setText(questions[currentQuestion]);
        invalidateActionMenu(); // Force la reconstruction du menu
    }

    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        // Item d'accès au lecteur QR
        menu.add(Menu.NONE, MENU_ID_SCAN_QR, Menu.NONE, "Scanner QR")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Démarre MainActivity (scanner)
                        MainActivity.startScanner(MainMenuActivity.this);
                        return true;
                    }
                });

        // Ajoute dynamiquement les réponses comme items du menu avec un listener pour chaque
        for (int i = 0; i < 3; i++) {
            final int answerIndex = i;
            menu.add(Menu.NONE, i, Menu.NONE, answers[currentQuestion][i])
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (answerIndex == correctAnswers[currentQuestion]) {
                                currentQuestion++;
                                if (currentQuestion < questions.length) {
                                    setQuestion();
                                } else {
                                    Toast.makeText(MainMenuActivity.this, "Quiz terminé !", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(MainMenuActivity.this, "Mauvaise réponse !", Toast.LENGTH_SHORT).show();
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
