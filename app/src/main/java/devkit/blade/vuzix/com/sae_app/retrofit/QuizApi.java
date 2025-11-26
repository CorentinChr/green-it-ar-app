package devkit.blade.vuzix.com.sae_app.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizItem;
import retrofit2.Call;
import retrofit2.http.GET;
/**
 * Interface Retrofit décrivant les endpoints liés aux quiz.
 * Retrofit génère l'implémentation à l'exécution à partir des annotations HTTP.
 */
public interface QuizApi {

    /**
     * Requête HTTP GET vers l'endpoint "quiz".
     * L'URL finale est composée de la base URL configurée dans Retrofit + ce chemin relatif.
     *
     * @return un `Call` qui, une fois exécuté, renvoie une `List<QuizItem>`.
     *         Le `Call` peut être exécuté de manière synchrone (.execute()) ou asynchrone (.enqueue()).
     */
    @GET("quiz")
    Call<List<QuizItem>> getQuiz();
}