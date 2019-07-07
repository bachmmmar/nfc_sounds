package com.bachmann.nfcsound;

import android.app.Application;
import android.app.ActivityManager;
import android.content.Context;

import com.bachmann.nfcsound.infra.DataManager;

public class AppContainer extends Application {

    private static final String BASE_ASSETS_PATH = "nfc_name";

    @Override
    public void onCreate () {
        super.onCreate();
        status = new AppStatus((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE));
        data_manager = new DataManager(getApplicationContext(), BASE_ASSETS_PATH);
    }

    public AppStatus getStatus() {
        return status;
    }

    public DataManager getDataManager() {
        return data_manager;
    }

    private AppStatus status;
    private DataManager data_manager;

}
