package walker.pack;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CMSActivity extends AppCompatActivity {
    ProgressDialog progDailog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cms);

        WebView cms_web_view = (WebView) findViewById(R.id.cms_web_view);
        cms_web_view.getSettings().setJavaScriptEnabled(true);
        cms_web_view.getSettings().setLoadWithOverviewMode(true);
        cms_web_view.getSettings().setUseWideViewPort(true);
        progDailog = new ProgressDialog(CMSActivity.this);
        cms_web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });
        cms_web_view.loadUrl("http://nmmuwalker.csdev.nmmu.ac.za/");
        Toast.makeText(this, cms_web_view.getUrl(), Toast.LENGTH_SHORT).show();
    }
}
