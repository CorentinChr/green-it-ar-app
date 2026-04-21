package devkit.blade.vuzix.com.sae_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

/**
 * Activité affichant le profil utilisateur : nom, niveau, dernier score et historique.
 */
public class ProfilActivity extends AppCompatActivity {

    private TextView nameView;
    private TextView levelView;
    private TextView lastScoreView;
    private TextView historyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameView = findViewById(R.id.profile_name);
        levelView = findViewById(R.id.profile_level);
        lastScoreView = findViewById(R.id.profile_last_score);
        historyView = findViewById(R.id.profile_history);

        loadProfileFromPrefs();
    }

    private void loadProfileFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Utilisateur");
        String levelText = prefs.getString("user_level_text", "beginner");
        int last = prefs.getInt("user_last_score", 0);
        String historyCsv = prefs.getString("user_score_history", "");

        nameView.setText("Nom : " + name);
        levelView.setText("Niveau : " + levelText);
        lastScoreView.setText("Dernier score : " + String.valueOf(last));

        if (historyCsv == null || historyCsv.isEmpty()) {
            historyView.setText("Historique des scores : Aucun historique");
        } else {
            // Construire une représentation simple sans utiliser Streams (compatibilité SDK)
            String[] parts = historyCsv.split(",");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(parts[i].trim());
            }
            sb.append("]");
            historyView.setText("Historique des scores : " + sb.toString());
        }
    }
}


