package com.mirusystems.usbsave;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SplashViewModel extends ViewModel {
    private static final String TAG = "SplashViewModel";

    private MutableLiveData<Boolean> shouldInitLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isExitButtonClicked = new MutableLiveData<>();

    public SplashViewModel() {
        super();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /**
     * 데이터가 있는지 검사하고 없으면 초기화해야 한다
     * @return
     */
    public LiveData<Boolean> shouldInit() {
        return shouldInitLiveData;
    }

    public LiveData<Boolean> isExitButtonClicked() {
        return isExitButtonClicked;
    }

    public void checkInitStatus() {
        boolean shouldInit = false;
        if (!Manager.isFileSaved()) {
            shouldInit = true;
        } else {
            if (Manager.findFilesOnUsb()) {
                shouldInit = true;
            }
        }

        shouldInitLiveData.postValue(shouldInit);
    }

    public void onExitButtonClicked() {
        isExitButtonClicked.postValue(true);
    }

}