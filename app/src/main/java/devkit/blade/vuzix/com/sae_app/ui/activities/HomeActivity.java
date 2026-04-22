package devkit.blade.vuzix.com.sae_app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import devkit.blade.vuzix.com.sae_app.R;
import devkit.blade.vuzix.com.sae_app.qrcode.MainActivity;
import devkit.blade.vuzix.com.sae_app.model.User;
import devkit.blade.vuzix.com.sae_app.data.retrofit.RetrofitClient;
import devkit.blade.vuzix.com.sae_app.data.retrofit.UserApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Main Template Activity, This application follows the Center Lock style of the Vuzix Camera App.
 * All Navigation buttons are MenuItems and the Rotation is handle by the ActionMenuActivity.
 * The Center of the screen is your normal Layout.
 * For more information on the ActionMenuActivity read the JavaDocs in Android Studio or download the
 * Java docs at:  https://www.vuzix.com/support/Downloads_Drivers
 */
public class HomeActivity extends ActionMenuActivity {

    private MenuItem MenuItem1;
    private MenuItem MenuItem2;
    private MenuItem MenuItem3;
    private MenuItem MenuItem4;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mainText = findViewById(R.id.main_text);
        // Lance la récupération des informations utilisateur pour afficher le nom
        fetchUser();
    }

    /**
     *  Main override to create the ACTION MENU. Notice that this is onCreate-ACTION-MENU. Not to be
     *  confuse with onCreate-Option-Menu which will create the basic Android menu that will not
     *  display properly in the small device screen.
     * @param menu Menu to inflate too.
     * @return Return if menu was setup correctly.
     */
    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem1 = menu.findItem(R.id.action_menu_item1);
        MenuItem2 = menu.findItem(R.id.action_menu_item2);
        MenuItem3 = menu.findItem(R.id.action_menu_item3);
        MenuItem4 = menu.findItem(R.id.action_menu_item4);
        updateMenuItems();

        return true;
    }

    /**
     * Override of the ActionMenuActivity. TRUE will tell the system to always show the Action Menu in
     * the position that you ask for. If this is false, the action menu will be hidden and will be
     * presented upon the Menu Option Gesture (1 Finger hold for 1 second.)
     * https://www.vuzix.com/Developer/KnowledgeBase/Detail/65
     */
    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    /**
     * Override of the ActionMenuActivity. This will tell the BladeOS which item to start at and
     * which one if the default action to start at on activity restarts.
     * @return
     */
    @Override
    protected int getDefaultAction() {
        return 1;
    }

    private void updateMenuItems() {
        if (MenuItem1 == null) {
            return;
        }

        MenuItem1.setEnabled(true);
        MenuItem2.setEnabled(true);
        MenuItem3.setEnabled(true);
        MenuItem4.setEnabled(true);
    }


    //Action Menu Click events
    //This events where register via the XML for the menu definitions.

    public void openMenuItem1(MenuItem item)
    {
        startActivity(new Intent(this, QuizActivity.class));
    }

    public void openMenuItem2(MenuItem item)
    {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openMenuItem3(MenuItem item)
    {
        startActivity(new Intent(this, QuizHabitudeActivity.class));
    }

    public void openMenuItem4(MenuItem item)
    {
        startActivity(new Intent(this, FicheInfoActivity.class));
    }

    // Ouvre l'écran Profil (nouvelle activité)
    public void openProfile(MenuItem item) {
        startActivity(new Intent(this, ProfilActivity.class));
    }

    // Requête Retrofit pour récupérer l'utilisateur test et stocker son niveau
    private void fetchUser() {
        UserApi api = RetrofitClient.getInstance().create(UserApi.class);
        api.getUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    // on ne bloque pas l'app si l'API échoue ; affiche juste un message
                    return;
                }

                User user = response.body();

                // Sauvegarde des infos essentielles en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_name", user.getName());
                editor.putString("user_level_text", user.getLevel());
                editor.putInt("user_level", user.getLevelValue());
                editor.putInt("user_last_score", user.score);
                // scoreHistory : stocke en simple chaîne csv
                if (user.scoreHistory != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < user.scoreHistory.size(); i++) {
                        if (i > 0) sb.append(",");
                        sb.append(user.scoreHistory.get(i));
                    }
                    editor.putString("user_score_history", sb.toString());
                }
                editor.apply();

                // Met à jour l'interface principale : Bonjour + nom, puis le texte-guide d'origine
                if (user.getName() != null && !user.getName().isEmpty()) {
                    String guide = getString(R.string.main_menu_text);
                    mainText.setText("Bonjour, " + user.getName() + "\n" + guide);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Ne pas planter l'app si l'API est indisponible
                Toast.makeText(HomeActivity.this, "Impossible de récupérer l'utilisateur", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
