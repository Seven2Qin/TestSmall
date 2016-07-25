package com.example.test;

import android.app.Activity;
import android.os.Bundle;

import net.wequick.small.Small;

/**
 * An example launcher activity that setUp the Small bundles and launch the main plugin.
 */
public class LaunchActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Small.setUp(this, new Small.OnCompleteListener() {
            @Override
            public void onComplete() {
                Small.openUri("member", LaunchActivity.this);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}
