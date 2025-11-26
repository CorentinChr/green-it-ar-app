// java
package devkit.blade.vuzix.com.sae_app.qrcode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;

import devkit.blade.vuzix.com.sae_app.R;

public class WebViewActivity extends Activity {

    private WebView webView;
    private static final String TAG = "WebViewActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        try {
            webView = findViewById(R.id.webView);
            if (webView == null) throw new NullPointerException("WebView view introuvable (R.id.webView)");

            // Configuration WebView
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setMediaPlaybackRequiresUserGesture(false);

            webView.setWebViewClient(new WebViewClient());

            // Récupération de l'URL passée par le MainActivity
            String url = getIntent().getStringExtra("url");

            //Condition de sécurité si l'URL est nulle ou vide (mettre une vidéo anonçant la mauvaise lecture du QRCode)
            if (url == null || url.isEmpty()) {
                url = "https://www.youtube.com/embed/dQw4w9WgXcQ?autoplay=1&controls=0";
            }

            webView.loadUrl(url);

            // Plein écran
            hideSystemUI();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation du WebView", e);
            Toast.makeText(this, "Impossible d'ouvrir la page web", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            hideSystemUI();
        } catch (Exception e) {
            Log.w(TAG, "Erreur lors du hideSystemUI()", e);
        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            try {
                webView.loadUrl("about:blank");
                webView.stopLoading();
                webView.setWebViewClient(null);
                webView.destroy();
            } catch (Exception e) {
                Log.w(TAG, "Erreur lors de la destruction du WebView", e);
            }
        }
        super.onDestroy();
    }
}
