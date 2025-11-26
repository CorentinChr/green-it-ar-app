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
 *
 * Comportement principal :
 * - Initialise les vues (titre / texte)
 * - Charge la liste des fiches via Retrofit
 * - Affiche la fiche courante et met à jour le menu d'action (bouton \"Suivant\")
 */
public class FicheInfoActivity extends ActionMenuActivity {

    private TextView titreView;
    private TextView texteView;
    // Liste des fiches récupérées depuis l'API
    private List<FicheInfoItem> ficheList;
    // Index de la fiche actuellement affichée
    private int currentFicheIndex = 0;
    // Indicateur que les fiches ont été chargées (permet d'afficher un état dans le menu)
    private boolean fichesLoaded = false;

    /**
     * Initialisation de l'activité : liaison vues et démarrage du chargement des fiches.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_info);

        titreView = findViewById(R.id.fiche_titre);
        texteView = findViewById(R.id.fiche_texte);

        titreView.setText("Chargement...");
        texteView.setText("");

        // Démarre la requête réseau asynchrone
        loadFiches();
    }

    /**
     * Met à jour l'affichage pour la fiche courante (`currentFicheIndex`).
     * Gère les cas où la liste est vide ou non initialisée.
     */
    private void setFiche() {
        if (ficheList == null || ficheList.isEmpty() || currentFicheIndex < 0 || currentFicheIndex >= ficheList.size()) {
            titreView.setText("Aucune fiche disponible");
            texteView.setText("");
            return;
        }

        // Récupère l'élément courant et affiche ses champs (protection null)
        FicheInfoItem item = ficheList.get(currentFicheIndex);
        titreView.setText(item.getTitre() != null ? item.getTitre() : "");
        texteView.setText(item.getTexte() != null ? item.getTexte() : "");
    }

    /**
     * Construction du menu d'action spécifique à l'Activity Vuzix.
     * - Si les fiches ne sont pas encore chargées : affiche un item désactivé "Chargement..."
     * - Si la liste est vide : affiche "Aucune fiche"
     * - Sinon : ajoute un bouton \"Suivant\" pour naviguer entre les fiches
     */
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

        // Bouton Suivant : incrémente l'index et met à jour l'affichage
        menu.add(0, 0, 0, "Suivant")
                .setOnMenuItemClickListener(item -> {
                    if (currentFicheIndex < ficheList.size() - 1) {
                        currentFicheIndex++;
                        setFiche();
                        // Force la reconstruction du menu si besoin (pour désactiver/activer items)
                        invalidateActionMenu();
                    } else {
                        // Fin de la liste
                        Toast.makeText(FicheInfoActivity.this, "Plus de fiches", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });

        return true;
    }

    /**
     * Indique que le menu d'action doit toujours être affiché sur le dispositif Vuzix.
     */
    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    /**
     * Lance la requête Retrofit pour récupérer la liste des fiches.
     * Met à jour l'état local (`ficheList`, `fichesLoaded`, `currentFicheIndex`) à la réponse.
     */
    private void loadFiches() {
        // Récupération de l'API Retrofit
        FicheApi api = RetrofitClient.getInstance().create(FicheApi.class);

        // Appel asynchrone
        api.getFiches().enqueue(new Callback<List<FicheInfoItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FicheInfoItem>> call, @NonNull Response<List<FicheInfoItem>> response) {
                // Vérifie que la réponse est correcte et que le corps n'est pas null
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(FicheInfoActivity.this, "Erreur API fiches", Toast.LENGTH_LONG).show();
                    // On termine l'activité si l'API est indisponible
                    finish();
                    return;
                }

                List<FicheInfoItem> fiches = response.body();

                // Stocke la liste et initialise l'affichage
                ficheList = fiches;
                fichesLoaded = true;
                currentFicheIndex = 0;
                setFiche();
                // Met à jour le menu d'action pour refléter l'état chargé
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
