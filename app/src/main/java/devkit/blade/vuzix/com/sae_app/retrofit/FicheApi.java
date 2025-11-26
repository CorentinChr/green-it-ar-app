package devkit.blade.vuzix.com.sae_app.retrofit;

import java.util.List;

import devkit.blade.vuzix.com.sae_app.model.FicheInfoItem;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FicheApi {
    @GET("fiches")
    Call<List<FicheInfoItem>> getFiches();
}
