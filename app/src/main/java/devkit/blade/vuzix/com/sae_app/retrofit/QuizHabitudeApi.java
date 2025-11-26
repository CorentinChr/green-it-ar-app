package devkit.blade.vuzix.com.sae_app.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.QuizHabitudesItem;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuizHabitudeApi {
    @GET("quizhabitude")
    Call<List<QuizHabitudesItem>> getQuizHabitude();
}