package com.hymn.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.net.Uri;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private WebView mWebView;
    private static final String HOST = "hymn.app.local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);

        WebSettings s = mWebView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req) {
                Uri uri = req.getUrl();
                if (!HOST.equals(uri.getHost())) return null;

                String path = uri.getPath();
                if (path == null || path.equals("/")) path = "/index.html";
                if (path.startsWith("/")) path = path.substring(1);

                try {
                    InputStream is = getAssets().open(path);
                    String mime = getMime(path);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Access-Control-Allow-Origin", "*");
                    return new WebResourceResponse(mime, "utf-8", 200, "OK", headers, is);
                } catch (Exception e) {
                    return null;
                }
            }

            private String getMime(String path) {
                if (path.endsWith(".html")) return "text/html";
                if (path.endsWith(".js"))   return "application/javascript";
                if (path.endsWith(".json")) return "application/json";
                if (path.endsWith(".txt"))  return "text/plain";
                if (path.endsWith(".png"))  return "image/png";
                if (path.endsWith(".css"))  return "text/css";
                return "application/octet-stream";
            }
        });

        mWebView.loadUrl("https://" + HOST + "/index.html");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
