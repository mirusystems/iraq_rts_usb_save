package com.mirusystems.usbsave;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;


import com.mirusystems.devices.printer.sato.PrinterManager;

import com.mirusystems.usbsave.label.LabelUtil;
import com.mirusystems.usbsave.security.Crypto;
import com.mirusystems.utility.MiruUtility;

import java.util.Locale;

public class App extends Application {
    private static final String TAG = "App";

    private static Context context = null;
    private static App mInstance;
    public static final String local_PATH = "/mnt/sdcard/RTS";
    public static final String replacexml = "/mnt/sdcard/replacexml";

    public static final String QR_SRCFILE_NAME = "decsv_value.csv";
    public static final String LOG_SRCFILE_NAME = "depoi_value.poi";
    public static byte[] ECRYPT_KEY;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setAppLocale("en");
        PrinterManager.getInstance(getContext());
        MiruUtility.MiruService("mkdir -p /mnt/sdcard/RTS");
        MiruUtility.MiruService("mkdir -p /mnt/sdcard/xml");
        MiruUtility.MiruService("mkdir -p /mnt/sdcard/replacexml");
//        drawLabel();

        String title = getTitle();
        ECRYPT_KEY = Crypto.getPlainContents("sp8HBr1u7jITGJR+bv8imet935PlgcyZ8haBA32wP8M=").getBytes();
        Log.v(TAG, "onCreate: title = " + title);


    }

    @Override
    public void onTerminate() {
        PrinterManager.clear();
        super.onTerminate();
    }

    public static App getInstance()
    {
        return mInstance;
    }

    public App() {
        mInstance = this;
    }

    public static Context getContext() {
        return context;
    }



    public static String getTitle() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("title", "");
    }

    public static boolean isOverrideCheckingEnabled() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("override_checking", true);
    }

//    public static boolean isElectionMode() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getBoolean("election_mode", true);
//    }

    public static boolean isEducationMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("education_mode", true);
    }

    public static boolean isPsoLabelPrint() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("pso_label_print", true);
    }

    public static boolean isEdEnabled() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("ed_enabled", false);
    }

    public static int getPrinterDpi() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String value = preferences.getString("printer_dpi", String.valueOf(LabelUtil.DPI_203));
//
//        return Integer.parseInt(value);
        return LabelUtil.DPI_203;
    }

    public static boolean isOutlineEnabled() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("outline_enabled", false);
    }

    private void setAppLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


}
