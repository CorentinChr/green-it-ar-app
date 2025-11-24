package devkit.blade.vuzix.com.sae_app.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizItem;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuizApi {
    @GET("quiz")
    Call<List<QuizItem>> getQuiz();
}