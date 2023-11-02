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


public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";

    private MainFragmentBinding binding;
    private MainViewModel mViewModel;

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

        mViewModel.getSelectedMode().observe(getViewLifecycleOwner(), event -> {
            Integer mode = event.getContentIfNotHandled();
            if (mode != null) {
                Manager.setMode(mode);
                switch (mode) {
                    case Manager.MODE_4_SIMULATION_USB: {
                        navController.navigate(R.id.govUsbListFragment);
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