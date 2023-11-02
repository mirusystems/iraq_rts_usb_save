package com.mirusystems.usbsave;

import android.util.Log;

import com.google.common.io.Files;
import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.LogDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Manager {
    private static final String TAG = "Manager";

    public static final int MODE_SIMULATION_PSO_CARD = 1;
    public static final int MODE_SIMULATION_VVD = 2;
    public static final int MODE_ELECTION_PSO_VVD = 3;
    public static final int MODE_PRODUCT_NUMBER = 4;
    public static final int MODE_RTS_SAT_IMEI = 5;
    public static final int MODE_4_SIMULATION_USB = 8;
    public static final int MODE_SETTINGS = 6;
    public static final int MODE_EXIT = 7;

    public static final String KEY_LEVEL = "level";
    public static final String KEY_MODE = "mode";
    public static final String KEY_PIN = "pin";
    public static final String KEY_GOV_CODE = "gov_code";
    public static final String KEY_GOV_NAME = "gov_name";
    public static final String KEY_ED_CODE = "ed_code";
    public static final String KEY_ED_NAME = "ed_name";
    public static final String KEY_VRC_CODE = "vrc_code";
    public static final String KEY_VRC_NAME = "vrc_name";
    public static final String KEY_PC_CODE = "pc_code";
    public static final String KEY_PC_NAME = "pc_name";
    public static final String KEY_PS_CODE = "ps_code";
    public static final String KEY_ = "";
    public static final String KEY_DEVICE_NAME = "device_name";

    public static final Map<Integer, String> GOV_MAP = new ConcurrentHashMap<Integer, String>() {{
        put(1, "Baghdad Rusafa");
        put(4, "Dahuk");
        put(5, "Erbil");
        put(6, "Sulaymaniyah");
        put(12, "Ninewa");
        put(14, "Kirkuk");
        put(21, "Diyala");
        put(22, "Anbar");
        put(23, "Baghdad Karkh");
        put(24, "Babylon");
        put(25, "Kerbala");
        put(26, "Wassit");
        put(27, "Salah Al-Din");
        put(28, "Najaf");
        put(31, "Qadissiya");
        put(32, "Muthanna");
        put(33, "Thi-Qar");
        put(34, "Missan");
        put(35, "Basrah");
        put(91, "Edu");
    }};

    public static String getGovEnglishName(int govCode) {
        return GOV_MAP.get(govCode);
    }

    public static final String DB_PATH = "/sdcard/sim4_usb.db";
    public static final String Usb_DB_PATH = "/sdcard/sim4_usb.db";
    public static final String LOG_PATH = "/sdcard/psolog.db";
    public static final String DB_NAME = "pso";
    public static final String DB_EXT = "db";
    public static final String PW_NAME = "pso.pw";
    public static final String PW_PATH = "/sdcard/pso.pw";

    private static AppDatabase db;
    public static AppDatabase getDb() {
        return db;
    }
    public static void setDb(AppDatabase db) {
        Manager.db = db;
    }

    private static LogDatabase log;

    public static LogDatabase getLog() {
        return log;
    }

    public static void setLog(LogDatabase log) {
        Manager.log = log;
    }

    /**
     * 앱을 동작시키기 위해 필요한 DB 파일과 비밀번호가 저장된 파일이 있는지 여부를 확인
     *
     * @return
     */
    public static boolean isFileSaved() {
        File dbFile = new File(DB_PATH);
        File pwFile = new File(PW_PATH);
        return dbFile.exists() && pwFile.exists();
    }

    private static final String[] USB_PATHS = {
            "/mnt/usbdisk1",
            "/mnt/usbdisk2",
            "/mnt/usbdisk3",
            "/mnt/usbdisk4"
    };

    public static List<String> getUsbPaths() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
            }
        }
        return paths;
    }

    /**
     * USB에 DB/PW 파일이 있으면 업데이트할 수 있으므로 초기화 화면으로 이동해야 한다
     *
     * @return
     */
    public static boolean findFilesOnUsb() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
                File[] files = dir.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        String ext = Files.getFileExtension(fileName);
                        if (ext.equals(DB_EXT) || fileName.equals(PW_NAME)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean findDbOnUsb() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
                File[] files = dir.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        String ext = Files.getFileExtension(fileName);
                        if (ext.equals(DB_EXT)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    Log.v(TAG, "findDbOnUsb: true");
                    return true;
                }
            }
        }

        return false;
    }

    public static String getDbPathOnUsb() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
                File[] files = dir.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        String ext = Files.getFileExtension(fileName);
                        if (ext.equals(DB_EXT)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    return files[0].getAbsolutePath();
                }
            }
        }

        return null;
    }

    public static boolean findPasswordOnUsb() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
                File[] files = dir.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        if (fileName.equals(PW_NAME)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    Log.v(TAG, "findPasswordOnUsb: true");
                    return true;
                }
            }
        }

        return false;
    }

    public static String getPasswordPathOnUsb() {
        List<String> paths = new ArrayList<>();
        for (String path : USB_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                paths.add(path);
                File[] files = dir.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        if (fileName.equals(PW_NAME)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    return files[0].getAbsolutePath();
                }
            }
        }

        return null;
    }

    private static int mode;

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        Manager.mode = mode;
    }

    public static void deleteLogIndexFile() {
        File logIndexFile = new File("/sdcard/pso.logid");
        if (logIndexFile.exists()) {
            logIndexFile.delete();
        }
    }

    public static void saveLogIndexFile(int index) {
        File logIndexFile = new File("/sdcard/pso.logid");
        try {
            Files.write(String.valueOf(index).getBytes(), logIndexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getModeDescription(int mode) {
        switch (mode) {
            case MODE_SIMULATION_PSO_CARD:
                return "MODE_SIMULATION_PSO_CARD";
            case MODE_SIMULATION_VVD:
                return "MODE_SIMULATION_VVD";
            case MODE_ELECTION_PSO_VVD:
                return "MODE_ELECTION_PSO_VVD";
            case MODE_PRODUCT_NUMBER:
                return "MODE_PRODUCT_NUMBER";
            case MODE_RTS_SAT_IMEI:
                return "MODE_RTS_SAT_IMEI";
            case MODE_4_SIMULATION_USB:
                return "MODE_4_SIMULATION_USB";
            case MODE_SETTINGS:
                return "MODE_SETTINGS";
            case MODE_EXIT:
                return "MODE_EXIT";
            default:
                return "UNKNOWN(" + mode + ")";
        }
    }
}
