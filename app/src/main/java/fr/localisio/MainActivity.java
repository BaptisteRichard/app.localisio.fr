package fr.localisio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {

    /* Création de la variable membre mWebView */
    private WebView mWebView;

    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Geolocation permissions coming from this app's Manifest will only be valid for devices with
            // API_VERSION < 23. On API 23 and above, we must check for permissions, and possibly
            // ask for them.
            String perm = Manifest.permission.ACCESS_FINE_LOCATION;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    ContextCompat.checkSelfPermission(MainActivity.this, perm) == PackageManager.PERMISSION_GRANTED) {
                // we're on SDK < 23 OR user has already granted permission
                callback.invoke(origin, true, false);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, perm)) {
                    // ask the user for permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{perm},11  );
                }
            }
        }
    }

    public class MyAppWebViewClient extends WebViewClient {

        @Override
        /*
         * La fonction shouldOverrideUrlLoading permet de restreindre l'url configurée avec loadurl
         * à une chaîne de caractère précise, soit le nom de domaine dans le script suivant
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Uri.parse(url).getHost().endsWith("localisio.fr")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Déclare mWebView à activity_main (le layout)
        //noinspection RedundantCast
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.clearCache(true);
        // Configure la webview pour l'utilisation du javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Permet l'ouverture des fenêtres
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(false);
        mWebView.setWebChromeClient(new GeoWebChromeClient());

        // Autorise le stockage DOM (Document Object Model)
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Charge l'url
        mWebView.loadUrl("https://localisio.fr/?app=1.0.1");

        /*
         * Les instructions ci-dessous permettent de forcer l'application
         * à ouvrir les Url directement dans l'application et non dans
         * un navigateur externe. MyAppWebViewClient() est la fonction
         * contenue dans le fichier MyAppWebViewClient.java .
         */

        mWebView.setWebViewClient(new MyAppWebViewClient() {

            // Fonction qui permet l'affichage de la page lorsque tout est chargé (événement onPageFinished)
            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.activity_main_webview).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case android.R.id.title:
                mWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

}