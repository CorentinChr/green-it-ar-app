package devkit.blade.vuzix.com.sae_app.data.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.FicheInfoItem;
import retrofit2.Call;
import retrofit2.http.GET;
/**
 * Interface Retrofit définissant les endpoints liés aux "fiches".
 * Retrofit génère l'implémentation à l'exécution à partir des annotations.
 */
public interface FicheApi {

    /**
     * Requête HTTP GET vers l'endpoint "fiches".
     * L'URL complète est construite en combinant la base URL configurée dans Retrofit
     * avec ce chemin relatif.
     *
     * @return un `Call` qui, lorsqu'il est exécuté, renvoie une `List<FicheInfoItem>`.
     *         Le `Call` permet une exécution synchrone (.execute()) ou asynchrone (.enqueue()).
     */
    @GET("fiches")
    Call<List<FicheInfoItem>> getFiches();
}
