package devkit.blade.vuzix.com.sae_app.data.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizHabitudesItem;
import retrofit2.Call;
import retrofit2.http.GET;
/**
 * Interface Retrofit décrivant l'endpoint lié aux "quiz d'habitudes".
 * Retrofit génère l'implémentation à l'exécution à partir des annotations HTTP.
 */
public interface QuizHabitudeApi {

    /**
     * Requête HTTP GET vers l'endpoint "quizhabitude".
     *
     * L'URL finale = base URL configurée dans Retrofit + "quizhabitude".
     * Retourne un `Call` qui, une fois exécuté, fournit une `List<QuizHabitudesItem>`.
     */
    @GET("quizhabitude")
    Call<List<QuizHabitudesItem>> getQuizHabitude();
}