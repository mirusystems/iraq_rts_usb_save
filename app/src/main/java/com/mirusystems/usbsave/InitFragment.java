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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.mirusystems.usbsave.databinding.InitFragmentBinding;

public class InitFragment extends BaseFragment {
    private static final String TAG = "InitFragment";

    private InitFragmentBinding binding;
    private InitViewModel mViewModel;

    public static InitFragment newInstance() {
        return new InitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.init_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InitViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.isFileSaved().observe(owner, found -> binding.nextButton.setEnabled(found));
        mViewModel.existDbOnUsb().observe(owner, exist -> binding.updateDbButton.setEnabled(exist));
        mViewModel.existPasswordOnUsb().observe(owner, exist -> binding.updatePasswordButton.setEnabled(exist));
        mViewModel.isUsbConnected().observe(owner, connected -> binding.backupDbButton.setEnabled(connected));
        mViewModel.isSuccess().observe(owner, booleanEvent -> {
            Boolean success = booleanEvent.getContentIfNotHandled();
            if (success != null) {
                if (success) {
                    navController.navigate(R.id.loginFragment);
                } else {
                    // 호출될 일이 없음
                }
            }
        });

        ((MainActivity) requireActivity()).setDisplayHomeAsUpEnabled(false);
    }



}