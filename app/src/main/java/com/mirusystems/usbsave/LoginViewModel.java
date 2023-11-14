package com.mirusystems.usbsave;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    public MutableLiveData<String> passwordLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();
    public MutableLiveData<String> versionLiveData = new MutableLiveData<>();

    private String password = "";

    public LoginViewModel() {
        super();
        init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private void init() {
        try {
            Context context = App.getContext();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionLiveData.postValue(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Boolean> getSuccess() {
        return successLiveData;
    }

    public LiveData<String> getVersion() {
        return versionLiveData;
    }

    public void onLoginButtonClicked() {
        successLiveData.postValue(true);
    }
}