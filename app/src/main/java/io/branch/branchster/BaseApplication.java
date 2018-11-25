package io.branch.branchster;

import android.app.Application;

import io.branch.referral.Branch;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // TASK #1 Initialize the Branch object
        Branch.getAutoInstance(this);
    }
}
