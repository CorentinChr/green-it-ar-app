package devkit.blade.vuzix.com.sae_app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.FicheInfoItem;
import devkit.blade.vuzix.com.sae_app.retrofit.FicheApi;
import devkit.blade.vuzix.com.sae_app.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité affichant une fiche d'information récupérée depuis l'API.
 * Affiche le titre en haut et le texte occupant le reste de l'écran.
 */
public class FicheInfoActivity extends ActionMenuActivity {

    private TextView titreView;
    private TextView texteView;
    // Liste des fiches récupérées depuis l'API
    private List<FicheInfoItem> ficheList;
    // Index de la fiche actuellement affichée
    private int currentFicheIndex = 0;
    // Indicateur que les fiches ont été chargées
    private boolean fichesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_info);

        titreView = findViewById(R.id.fiche_titre);
        texteView = findViewById(R.id.fiche_texte);

        titreView.setText("Chargement...");
        texteView.setText("");

        loadFiches();
    }

    // Met à jour l'affichage pour la fiche courante
    private void setFiche() {
        if (ficheList == null || ficheList.isEmpty() || currentFicheIndex < 0 || currentFicheIndex >= ficheList.size()) {
            titreView.setText("Aucune fiche disponible");
            texteView.setText("");
            return;
        }

        FicheInfoItem item = ficheList.get(currentFicheIndex);
        titreView.setText(item.getTitre() != null ? item.getTitre() : "");
        texteView.setText(item.getTexte() != null ? item.getTexte() : "");
    }

    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);

        menu.clear();

        if (!fichesLoaded) {
            menu.add(0, 0, 0, "Chargement...").setEnabled(false);
            return true;
        }

        if (ficheList == null || ficheList.isEmpty()) {
            menu.add(0, 0, 0, "Aucune fiche").setEnabled(false);
            return true;
        }

        // Bouton Suivant pour passer à la fiche suivante
        menu.add(0, 0, 0, "Suivant")
                .setOnMenuItemClickListener(item -> {
                    if (currentFicheIndex < ficheList.size() - 1) {
                        currentFicheIndex++;
                        setFiche();
                        invalidateActionMenu();
                    } else {
                        Toast.makeText(FicheInfoActivity.this, "Plus de fiches", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });

        return true;
    }

    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    private void loadFiches() {
        FicheApi api = RetrofitClient.getInstance().create(FicheApi.class);

        api.getFiches().enqueue(new Callback<List<FicheInfoItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FicheInfoItem>> call, @NonNull Response<List<FicheInfoItem>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(FicheInfoActivity.this, "Erreur API fiches", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                List<FicheInfoItem> fiches = response.body();

                // Stocke la liste et initialise l'affichage
                ficheList = fiches;
                fichesLoaded = true;
                currentFicheIndex = 0;
                setFiche();
                invalidateActionMenu();

                if (fiches.isEmpty()) {
                    titreView.setText("Aucune fiche disponible");
                    texteView.setText("");
                }

                // Affiche la première fiche par défaut
                // (déjà affichée via setFiche)
            }

            @Override
            public void onFailure(@NonNull Call<List<FicheInfoItem>> call, @NonNull Throwable t) {
                Log.e("FICHE", "Erreur réseau fiches", t);
                Toast.makeText(FicheInfoActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
