package devkit.blade.vuzix.com.sae_app.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Fournit un singleton Retrofit configuré avec le convertisseur Gson.
 * Utiliser RetrofitClient.getInstance() pour récupérer l'instance partagée.
 *
 * Note : la baseUrl active pointe vers l'environnement de production.
 *       Une URL locale pour l'émulateur est fournie en commentaire.
 */
public class RetrofitClient {

    private static Retrofit instance;

    /**
     * Retourne l'instance singleton de Retrofit.
     * Méthode synchronisée pour éviter des initialisations concurrentes.
     *
     * @return instance de Retrofit configurée avec Gson
     */
    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    //.baseUrl("http://10.0.2.2:8080/") (adresse IP locale pour l'émulateur Android en phase de développement)
                    .baseUrl("https://backendsae-production.up.railway.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
