package com.mirusystems.usbsave;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;

import com.mirusystems.usbsave.R;

import java.util.Random;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    static Context context;
    static MainActivity activity;
    static FragmentManager fragmentManager;
    static NavController navController;

    private int value = 0;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activity == null) {
            activity = (MainActivity) requireActivity();
        }
        if (context == null) {
            context = activity.getApplicationContext();
        }
        if (fragmentManager == null) {
            fragmentManager = activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager();
        }


        Random random = new Random();
        value = random.nextInt();

        Log.d(TAG, "onCreate: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(v -> hideSoftKeyboard());
        Log.d(TAG, "onViewCreated: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (navController == null) {
            navController = activity.navController;
        }
        Log.d(TAG, "onActivityCreated: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        Log.d(TAG, "onResume: backStackCount = " + backStackCount + ", " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + this.random() + ", " + getClassName(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + this.random() + ", " + getClassName(this));
    }

    public void hideSoftKeyboard() {
        Log.v(TAG, "hideSoftKeyboard: ");
        Activity activity = requireActivity();
        View v = activity.getCurrentFocus();
        if (v == null) {
            v = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void showToast(String text) {
        Log.v(TAG, "showToast: text = " + text);
        Toast toast = Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        toast.show();
    }

    public int random() {
        return value;
    }

    private static String getClassName(int resId) {
        switch (resId) {
            case R.id.splashFragment:
                return SplashFragment.class.getName();
            case R.id.initFragment:
                return InitFragment.class.getName();
            case R.id.loginFragment:
                return LoginFragment.class.getName();
            case R.id.mainFragment:
                return MainFragment.class.getName();
            default:
                return "UNKNOWN_FRAGMENT";
        }
    }

    private static String getClassName(Object o) {
        if (o instanceof SplashFragment) {
            return "SplashFragment";
        } else if (o instanceof InitFragment) {
            return "InitFragment";
        } else if (o instanceof LoginFragment) {
            return "LoginFragment";
        } else if (o instanceof MainFragment) {
            return "MainFragment";
        } else if (o instanceof GovUsbListFragment) {
            return "GovUsbListFragment";
        }else if (o instanceof UsbSaveFragment) {
            return "UsbSaveFragment";
        }  else {
            return "UNKNOWN_FRAGMENT";
        }
    }
}