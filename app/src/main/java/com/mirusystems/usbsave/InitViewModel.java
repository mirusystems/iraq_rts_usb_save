package com.mirusystems.usbsave;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InitViewModel extends ViewModel {
    private static final String TAG = "InitViewModel";

    private static final String ACTION_USB_MOUNTED = "com.mirusystems.device.action.USB_MOUNTED";
    private static final String ACTION_USB_UNMOUNTED = "com.mirusystems.device.action.USB_UNMOUNTED";

    private Context context;
    private MutableLiveData<Boolean> isFileSavedLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> existDbOnUsbLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> existPasswordOnUsbLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUsbConnected = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> isSuccessLiveData = new MutableLiveData<>();

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive: intent = " + intent);
            checkUsbConnected();
            checkFileOnUsb();
        }
    };

    public InitViewModel() {
        super();
        context = App.getContext();
        registerReceiver();
        checkUsbConnected();
        checkSavedFile();
        checkFileOnUsb();
    }

    @Override
    protected void onCleared() {
        unregisterReceiver();
        super.onCleared();
    }

    public LiveData<Boolean> isFileSaved() {
        return isFileSavedLiveData;
    }

    public LiveData<Boolean> existDbOnUsb() {
        return existDbOnUsbLiveData;
    }

    public LiveData<Boolean> existPasswordOnUsb() {
        return existPasswordOnUsbLiveData;
    }

    public LiveData<Boolean> isUsbConnected() {
        return isUsbConnected;
    }

    public LiveData<Event<Boolean>> isSuccess() {
        return isSuccessLiveData;
    }

    public void onUpdateDbButtonClicked() {
        saveDbFile();
        checkSavedFile();
        existDbOnUsbLiveData.postValue(false); // 복사 후 버튼을 비활성화
    }

    public void onUpdatePasswordButtonClicked() {
        savePasswordFile();
        checkSavedFile();
        existPasswordOnUsbLiveData.postValue(false); // 복사 후 버튼을 비활성화
    }

    public void onBackupButtonClicked() {
        backup();
        isUsbConnected.postValue(false); // 복사 후 버튼을 비활성화
    }

    public void onNextButtonClicked() {
        isSuccessLiveData.postValue(new Event<>(true));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_MOUNTED);
        filter.addAction(ACTION_USB_UNMOUNTED);
        context.registerReceiver(usbReceiver, filter);
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(usbReceiver);
    }

    private boolean saveDbFile() {
        String path = Manager.getDbPathOnUsb();
        if (path != null) {
            File sdcard = new File("/sdcard");
            // pso.db, pso.db-shm, pso.db-wal 파일들을 삭제해라
            File[] savedDbFiles = sdcard.listFiles(pathname -> {
                String fileName = pathname.getName();
                return fileName.startsWith("pso.db");
            });
            if (savedDbFiles != null) {
                for (File file : savedDbFiles) {
                    file.delete();
                }
            }
            try {
                Files.copy(new File(path), new File(Manager.DB_PATH));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean savePasswordFile() {
        String path = Manager.getPasswordPathOnUsb();
        if (path != null) {
            try {
                Files.copy(new File(path), new File(Manager.PW_PATH));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private boolean backup() {
        File sdcardDir = new File("/sdcard");
        List<String> paths = Manager.getUsbPaths();
        for (String path : paths) {
            String dirName = FORMAT.format(new Date());
            File dir = new File(path, dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File[] files = sdcardDir.listFiles(pathname -> {
                if (pathname.isFile()) {
                    String fileName = pathname.getName();
                    String ext = Files.getFileExtension(fileName);
                    if (fileName.startsWith("pso")) {
                        return true;
                    }
                }
                return false;
            });
            if (files != null) {
                for (File src : files) {
                    try {
                        Files.copy(src, new File(dir, src.getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    private void checkUsbConnected() {
        List<String> paths = Manager.getUsbPaths();
        if (paths != null && paths.size() > 0) {
            isUsbConnected.postValue(true);
        } else {
            isUsbConnected.postValue(false);
        }
    }

    private void checkSavedFile() {
        isFileSavedLiveData.postValue(Manager.isFileSaved());
    }

    private void checkFileOnUsb() {
        existDbOnUsbLiveData.postValue(Manager.findDbOnUsb());
        existPasswordOnUsbLiveData.postValue(Manager.findPasswordOnUsb());
    }
}