package com.mirusystems.usbsave;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.UsbDistrict;
import com.mirusystems.usbsave.data.UsbListEntity;

import java.util.List;

public class GovUsbListViewModel extends ViewModel implements OnItemSelectListener<UsbDistrict> {
    private static final String TAG = "GovListViewModel";

    private AppDatabase db;
    private MutableLiveData<List<UsbDistrict>> districtListLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<UsbDistrict>> selectedDistrictLiveData = new MutableLiveData<>();

    public GovUsbListViewModel() {
        super();

        db = Manager.getDb();
        List<UsbListEntity> usbListEntity = db.usbSaveDao().getAll();
        usbListEntity.size();
        List<UsbDistrict> list;
        list = db.usbSaveDao().getGovListForSimulation();
        for (UsbDistrict district : list) {
            district.name += "\n" + Manager.getGovEnglishName(district.code);
        }
      districtListLiveData.postValue(list);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<UsbDistrict>> getDistrictList() {
        return districtListLiveData;
    }

    public LiveData<Event<UsbDistrict>> getSelectedDistrict() {
        return selectedDistrictLiveData;
    }

    @Override
    public void onItemSelected(UsbDistrict district) {
        Log.v(TAG, "onItemSelected: district = " + district);
        selectedDistrictLiveData.postValue(new Event<>(district));
    }

    public void updateList() {
        db = Manager.getDb();
        List<UsbDistrict> list;
        list = db.usbSaveDao().getGovListForSimulation();
        for (UsbDistrict district : list) {
            district.name += "\n" + Manager.getGovEnglishName(district.code);
        }
        districtListLiveData.postValue(list);
    }
}