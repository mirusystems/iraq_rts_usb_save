package com.mirusystems.usbsave;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.LogDatabase;
import com.mirusystems.usbsave.Event;


public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private MutableLiveData<Event<Integer>> selectedModeLiveData = new MutableLiveData<>();
    public MutableLiveData<String> versionLiveData = new MutableLiveData<>();


    public MainViewModel() {
        super();
        AppDatabase db = AppDatabase.getInstance(App.getContext());
//        updateDb(db);
        Manager.setDb(db);

        LogDatabase log = LogDatabase.getInstance(App.getContext());
        Manager.setLog(log);

        try {
            Context context = App.getContext();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionLiveData.postValue(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<Event<Integer>> getSelectedMode() {
        return selectedModeLiveData;
    }


    public void onSimulationUsbButtonClicked() {
        selectedModeLiveData.postValue(new Event<>(Manager.MODE_4_SIMULATION_USB));
    }

    public void onClearUsbDataClicked() {
        selectedModeLiveData.postValue(new Event<>(Manager.MODE_CLEAR_USB_DATA));
    }

    public void onCopyUsbDataButtonClicked() {
        selectedModeLiveData.postValue(new Event<>(Manager.MODE_COPY_USB_DATA));
    }

    public void onSettingsButtonClicked() {
        selectedModeLiveData.postValue(new Event<>(Manager.MODE_SETTINGS));
    }

    public void onExitButtonClicked() {
        selectedModeLiveData.postValue(new Event<>(Manager.MODE_EXIT));
    }
    
}