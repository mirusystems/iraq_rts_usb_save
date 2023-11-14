package com.mirusystems.usbsave;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.LogDatabase;
import com.mirusystems.usbsave.databinding.MainFragmentBinding;
import com.mirusystems.utility.MiruUtility;

import java.io.File;
import java.util.Locale;


public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";

    private MainFragmentBinding binding;
    private MainViewModel mViewModel;
    public static String[] USB_PATH = new String[4];
    public static String[] USB_DISK = new String[4];

    public int mUSBSearchCnt = 0;
    private static final int PERMISSION_REQUEST_CODE = 123;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        checkAndRequestPermissions();
        for (int i = 0; i < USB_DISK.length; ++i) {
            USB_DISK[i] = String.format(Locale.US, "/mnt/usbdisk%d", i + 1);
        }
        SearchUSBDisk();
        mViewModel.getSelectedMode().observe(getViewLifecycleOwner(), event -> {
            Integer mode = event.getContentIfNotHandled();
            if (mode != null) {
                Manager.setMode(mode);
                switch (mode) {
                    case Manager.MODE_4_SIMULATION_USB: {
                        navController.navigate(R.id.govUsbListFragment);
                        break;
                    }
                    case Manager.MODE_COPY_USB_DATA:{
                        for (int i = 0; i < mUSBSearchCnt; ++i) {
                            File f = new File(USB_PATH[i] + "/RTS");
                            if (!f.exists())
                                f.mkdirs();
                            MiruUtility.MiruService("cp -r /mnt/sdcard/RTS/*" +  USB_PATH[i] + "/RTS/");
                        }
                        MiruUtility.MiruService("sync");
                        break;
                    }
                    case Manager.MODE_CLEAR_USB_DATA:{
                        if(App.isDevice_Xml_Data_Delete_Mode()){
                            MiruUtility.MiruService("rm -rf " + "/mnt/sdcard/RTS/*");
                            MiruUtility.MiruService("rm -rf " + "/mnt/sdcard/replacexml/*");
                        }
                        for (int i = 0; i < mUSBSearchCnt; ++i) {
                            File f = new File(USB_PATH[i] + "/RTS");
                            if (!f.exists())
                                f.mkdirs();
                            MiruUtility.MiruService("rm -rf " +  USB_PATH[i] + "/RTS/*");
                        }
                        MiruUtility.MiruService("sync");
                        break;
                    }
                    case Manager.MODE_SETTINGS: {
                        navController.navigate(R.id.settingsFragment);
                        break;
                    }
                    case Manager.MODE_EXIT: {
                        AppDatabase db = Manager.getDb();
                        db.close();
                        LogDatabase log = Manager.getLog();
                        log.close();
                        requireActivity().finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                    }
                }
            }
        });
    }

    public synchronized void SearchUSBDisk() {
        mUSBSearchCnt = 0;
        for (int i = 0; i < USB_DISK.length; ++i) {
//            Log.i("USB", "finding usb path : [" + USB_DISK[i] + ConfigManager.COMMON_DIR + "][" + mUSBSearchCnt + "]");
            if (new File(USB_DISK[i]).exists()) {
                if (mUSBSearchCnt < USB_PATH.length) {
                    USB_PATH[mUSBSearchCnt] = USB_DISK[i];
//                    Log.i("USB", "exists usb path : [" + USB_PATH[mUSBSearchCnt] + "][" + mUSBSearchCnt + "]");
                    mUSBSearchCnt++;
                }
                if (mUSBSearchCnt >= USB_PATH.length)
                    break;
            }
        }
//        Log.i("USB","found usb count : "+mUSBSearchCnt);
    }


    private void checkAndRequestPermissions() {
        // 권한이 이미 허용되었는지 확인
        if (checkPermissions()) {
            // 이미 허용되었으면 여기에서 원하는 작업 수행
        } else {
            // 권한을 요청
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int manageExternalStoragePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                manageExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
    }

    // 사용자가 권한 요청에 응답한 후 호출되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                // 권한이 허용되었을 때 수행할 작업
            } else {
                // 사용자가 권한 요청을 거부한 경우 처리할 작업
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}