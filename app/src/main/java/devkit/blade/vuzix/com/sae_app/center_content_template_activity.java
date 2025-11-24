package devkit.blade.vuzix.com.sae_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hud.actionmenu.ActionMenuActivity;
import com.vuzix.hud.actionmenu.DefaultActionMenuItemView;


/**
 * Main Template Activity, This application follows the Center Lock style of the Vuzix Camera App.
 * All Navigation buttons are MenuItems and the Rotation is handle by the ActionMenuActivity.
 * The Center of the screen is your normal Layout.
 * For more information on the ActionMenuActivity read the JavaDocs in Android Studio or download the
 * Java docs at:  https://www.vuzix.com/support/Downloads_Drivers
 */
public class center_content_template_activity extends ActionMenuActivity {

    private MenuItem MenuItem1;
    private MenuItem MenuItem2;
    private MenuItem MenuItem3;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_content_template_style);

        mainText = findViewById(R.id.main_text);
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
        MenuItem3.setEnabled(false);
    }


    //Action Menu Click events
    //This events where register via the XML for the menu definitions.

    public void openMenuItem1(MenuItem item)
    {
        startActivity(new Intent(this, QuizActivity.class));
    }

    public void openMenuItem2(MenuItem item)
    {
        startActivity(new Intent(this, QuizActivity.class));
    }

}
