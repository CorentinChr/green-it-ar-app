package devkit.blade.vuzix.com.sae_app.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import devkit.blade.vuzix.com.sae_app.R;
import devkit.blade.vuzix.com.sae_app.data.retrofit.RetrofitClient;
import devkit.blade.vuzix.com.sae_app.data.retrofit.UserApi;
import devkit.blade.vuzix.com.sae_app.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité affichant le profil utilisateur : nom, niveau, dernier score et historique.
 */
public class ProfilActivity extends AppCompatActivity {

    private TextView nameView;
    private TextView levelView;
    private TextView lastScoreView;
    private TextView historyView;
    private TextView qhHistoryView;
    private TextView arHistoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameView = findViewById(R.id.profile_name);
        levelView = findViewById(R.id.profile_level);
        lastScoreView = findViewById(R.id.profile_last_score);
        historyView = findViewById(R.id.profile_history);
        qhHistoryView = findViewById(R.id.profile_qh_history);
        arHistoryView = findViewById(R.id.profile_ar_history);

        loadProfileFromPrefs();
        fetchProfileFromBackend();
    }

    private void loadProfileFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Utilisateur");
        String levelText = prefs.getString("user_level_text", "beginner");
        int last = prefs.getInt("user_last_score", 0);
        String historyCsv = prefs.getString("user_score_history", "");

        nameView.setText("Nom : " + name);
        levelView.setText("Niveau : " + levelText);
        lastScoreView.setText("Dernier score au quiz test: " + last);
        // Valeurs par défaut pour les historiques QH/AR (prefs peuvent ne pas contenir ces clés)
        String qhCsv = prefs.getString("user_qh_score_history", "");
        String arCsv = prefs.getString("user_ar_score_history", "");

        if (qhHistoryView != null) {
            if (qhCsv == null || qhCsv.isEmpty()) {
                qhHistoryView.setText("Historique QH : Aucun historique");
            } else {
                qhHistoryView.setText("Historique Quiz Habitudes : " + formatCsv(historyCsvToList(qhCsv)));
            }
        }

        if (arHistoryView != null) {
            if (arCsv == null || arCsv.isEmpty()) {
                arHistoryView.setText("Historique AR : Aucun historique");
            } else {
                arHistoryView.setText("Historique Quiz AR : " + formatCsv(historyCsvToList(arCsv)));
            }
        }

        if (historyCsv == null || historyCsv.isEmpty()) {
            historyView.setText("Historique des scores au quiz test : Aucun historique");
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

    // Aide : transforme une chaîne CSV en liste de chaînes (garde l'ordre original)
    private java.util.List<String> historyCsvToList(String csv) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return out;
        String[] parts = csv.split(",");
        for (String p : parts) {
            out.add(p.trim());
        }
        return out;
    }

    // Aide : formate une liste en représentation [a, b, c]
    private String formatCsv(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    private void fetchProfileFromBackend() {
        try {
            UserApi api = RetrofitClient.getInstance().create(UserApi.class);
            api.getUser().enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.w("ProfilActivity", "API getUser non réussie");
                        return;
                    }

                    User user = response.body();

                    if (user.getName() != null) {
                        nameView.setText("Nom : " + user.getName());
                    }
                    if (user.getLevel() != null) {
                        levelView.setText("Niveau : " + user.getLevel());
                    }

                    // dernier score classique (champ score)
                    lastScoreView.setText("Dernier score au quiz test: " + user.score);

                    // Remplit les historiques QH et AR (liste complète, du plus ancien au plus récent)
                    if (qhHistoryView != null) {
                        if (user.qhScoreHistory == null || user.qhScoreHistory.isEmpty()) {
                            qhHistoryView.setText("Historique QH : Aucun historique");
                        } else {
                            java.util.List<String> strList = new java.util.ArrayList<>();
                            for (Integer v : user.qhScoreHistory) strList.add(String.valueOf(v));
                            qhHistoryView.setText("Historique QH : " + formatCsv(strList));
                        }
                    }

                    if (arHistoryView != null) {
                        if (user.arScoreHistory == null || user.arScoreHistory.isEmpty()) {
                            arHistoryView.setText("Historique AR : Aucun historique");
                        } else {
                            java.util.List<String> strList = new java.util.ArrayList<>();
                            for (Integer v : user.arScoreHistory) strList.add(String.valueOf(v));
                            arHistoryView.setText("Historique AR : " + formatCsv(strList));
                        }
                    }

                    // Log des historiques récupérés (utile pour debug)
                    Log.i("ProfilActivity", "qhScoreHistory=" + user.qhScoreHistory + " arScoreHistory=" + user.arScoreHistory);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.w("ProfilActivity", "Impossible de récupérer l'utilisateur", t);
                }
            });
        } catch (Exception e) {
            Log.e("ProfilActivity", "Erreur lors de la requête getUser", e);
        }
    }
}


