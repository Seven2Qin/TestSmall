package com.example.test;

import android.app.Application;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import net.wequick.small.Small;
import net.wequick.small.webkit.WebView;
import net.wequick.small.webkit.WebViewClient;

/**
 * The host application for Small
 */
public class HostApp extends Application {

    private static final String TAG = "HostApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "----onCreate");

        // If you have some native web modules, uncomment following
        // to declare a base URI for cross-platform page jumping.
        //
        // Small.setBaseUri("http://your_domain/path");
        //

        // !Important, ensure the Small can smooth functioning even
        // after the application was killed in background.
        Small.setBaseUri("http://m.qianmi.com");
        Small.setWebViewClient(new AdminWebViewClient());
        Small.preSetUp(this);
    }

    private static final class AdminWebViewClient extends WebViewClient {

        private ProgressDialog mDlg;

        @Override
        public void onPageStarted(Context context, WebView view, String url, Bitmap favicon) {
            mDlg = new ProgressDialog(context);
            mDlg.setMessage("正在加载中");
            mDlg.show();
            super.onPageStarted(context, view, url, favicon);
        }

        @Override
        public void onPageFinished(Context context, WebView view, String url) {
            super.onPageFinished(context, view, url);
            mDlg.dismiss();
        }

        @Override
        public void onReceivedError(Context context, WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(context, view, errorCode, description, failingUrl);
            mDlg.dismiss();
            Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
        }
    }
}
