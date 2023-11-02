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

import com.mirusystems.usbsave.databinding.LoginFragmentBinding;

public class LoginFragment extends BaseFragment {
    private static final String TAG = "LoginFragment";

    private LoginFragmentBinding binding;
    private LoginViewModel mViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), booleanEvent -> {
            Boolean success = booleanEvent;
            if (success != null) {
                Log.v(TAG, "onActivityCreated: getSuccess = " + success);
                if (success) {
                    navController.navigate(R.id.mainFragment);
                } else {
                    showDialog();
                }
            }
        });
    }

    private void showDialog() {
        BaseDialog.showDialog(requireActivity(), "Wrong password", "Pleas retry", "Ok", null, new OnDialogClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                Log.v(TAG, "onPositiveButtonClicked: ");
            }

            @Override
            public void onNegativeButtonClicked() {
                Log.v(TAG, "onNegativeButtonClicked: ");
            }
        });
    }

}