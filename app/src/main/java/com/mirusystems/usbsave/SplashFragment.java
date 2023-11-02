package com.mirusystems.usbsave;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;


import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.LogDatabase;
import com.mirusystems.usbsave.databinding.SplashFragmentBinding;

public class SplashFragment extends BaseFragment {
    private static final String TAG = "SplashFragment";

    private SplashFragmentBinding binding;
    private SplashViewModel mViewModel;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.splash_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        mViewModel.shouldInit().observe(getViewLifecycleOwner(), booleanEvent -> {
            Boolean shouldInit = booleanEvent;
            if (shouldInit != null) {
                Log.v(TAG, "onActivityCreated: shouldInit = " + shouldInit);
                if (shouldInit) {
                    navController.navigate(R.id.initFragment);
                } else {
                    navController.navigate(R.id.loginFragment);
                }
            }
        });
        mViewModel.isExitButtonClicked().observe(getViewLifecycleOwner(), booleanEvent -> {
            Boolean isExitButtonClicked = booleanEvent;
            if (isExitButtonClicked != null) {
                if (isExitButtonClicked) {
                    AppDatabase db = Manager.getDb();
                    db.close();
                    LogDatabase log = Manager.getLog();
                    log.close();
                    requireActivity().finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume: E");
        mViewModel.checkInitStatus();
    }
}