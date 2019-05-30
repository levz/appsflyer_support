package com.appsflyer.androidsampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;

  /** Test this deep link with the link : https://androidsampleapp.onelink.me/Pvqj */
  /** run: $ adb shell am start -a android.intent.action.VIEW -d https://androidsampleapp.onelink.me/Pvqj */



public class DeepLink extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        /* Add this call to the tracker on each deep linked activity */
        AppsFlyerLib.getInstance().sendDeepLinkData(this);


        // open MainActivity in case onAppAttribution is not called
        Intent intent = getIntent();
        if (intent != null) {
            Intent newIntent = new Intent(
                    intent.getAction(),
                    intent.getData(),
                    this,
                    MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }
        finish();
    }


    public static class AppsFlyerListener implements AppsFlyerConversionListener{

        private Context mApplicationContext;
        private Handler mHandler;

        public AppsFlyerListener(Context context){
            this.mApplicationContext = context;
            mHandler = new Handler();
        }

        /* Returns the attribution data. Note - the same conversion data is returned every time per install */
        @Override
        public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
            Log.d(AFApplication.LOG_TAG, "DeepLink onInstallConversionDataLoaded()  conversionData=" + conversionData);
            for (String attrName : conversionData.keySet()) {
                Log.d(AFApplication.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
            }
        }

        @Override
        public void onInstallConversionFailure(String errorMessage) {
            Log.d(AFApplication.LOG_TAG, "error onInstallConversionFailure : " + errorMessage);
        }


        /* Called only when a Deep Link is opened */
        @Override
        public void onAppOpenAttribution(final Map<String, String> conversionData) {
            Log.d(AFApplication.LOG_TAG, "DeepLink onAppOpenAttribution()  conversionData=" + conversionData);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    startMainActivity(conversionData.get("af_dp"));
                }
            });
        }

        @Override
        public void onAttributionFailure(String errorMessage) {
            Log.d(AFApplication.LOG_TAG, "error onAttributionFailure : " + errorMessage);
        }

        private void startMainActivity(String af_dp){
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(af_dp != null ? af_dp : "tvzavr://test/deeplink"),
                    mApplicationContext,
                    MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplicationContext.startActivity(intent);
        }
    }

}
