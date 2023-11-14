package com.mirusystems.usbsave;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.District;
import com.mirusystems.usbsave.data.UsbDistrict;

import java.util.List;


public class UsbVrcListViewModel extends ViewModel implements OnItemSelectListener<UsbDistrict> {
    private static final String TAG = "VrcListViewModel";
    private int govCode;
    private int edCode;
    private AppDatabase db;
    private MutableLiveData<List<UsbDistrict>> districtListLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<UsbDistrict>> selectedDistrictLiveData = new MutableLiveData<>();

    private MutableLiveData<Event<Boolean>> isWorkingLiveData = new MutableLiveData<>();


    public UsbVrcListViewModel() {
        super();
        db = Manager.getDb();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void setCode(int govCode) {
        this.govCode = govCode;

        List<UsbDistrict> list;
        list = db.usbSaveDao().getVrcList(govCode);

        districtListLiveData.setValue(list);
    }

    public LiveData<List<UsbDistrict>> getDistrictList() {
        return districtListLiveData;
    }

    public LiveData<Event<UsbDistrict>> getSelectedDistrict() {
        return selectedDistrictLiveData;
    }

    @Override
    public void onItemSelected(UsbDistrict district) {
        Log.v(TAG, "onItemSelected: " + district);
        selectedDistrictLiveData.postValue(new Event<>(district));
    }

    public void onStartButtonClicked() {
        Log.v(TAG, "onStartButtonClicked: ");
        isWorkingLiveData.postValue(new Event<>(true));
//        startWorker(); // TODO: 2021-06-21
    }

}