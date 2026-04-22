package devkit.blade.vuzix.com.sae_app.data.retrofit;

import devkit.blade.vuzix.com.sae_app.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API Retrofit pour l'utilisateur de test.
 */
public interface  UserApi {

    // endpoint demandé par l'utilisateur
    @GET("users/123")
    Call<User> getUser();

    /**
     * Endpoint générique pour envoyer un évènement de score au backend.
     * Le backend peut choisir d'appender ce score dans une liste spécifique
     * (ex: "qh_score_history" ou "ar_score_history").
     *
     * Nous envoyons un objet JSON décrivant la liste cible et la valeur du score.
     */
    @POST("users/123/scores")
    Call<Void> postScore(@Body Object body);
}

