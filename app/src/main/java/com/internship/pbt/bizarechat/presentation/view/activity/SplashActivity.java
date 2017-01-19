package com.internship.pbt.bizarechat.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.internship.pbt.bizarechat.BuildConfig;
import com.internship.pbt.bizarechat.presentation.view.BasicActivity;


public class SplashActivity extends BasicActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(BuildConfig.SPLASH_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mNavigator.navigateToLoginActivity(this);
        finish();
    }
}
