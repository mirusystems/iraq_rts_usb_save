/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : class for trace card debugging
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2017.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.utility;

import android.util.Log;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
public class Trace {
    public static final int LEVEL_OFF = 0;
    public static final int LEVEL_ERROR = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_DEBUG = 4;
    public static final int LEVEL_VERVOSE = 5;
    private static volatile int log_level = 5;
    private static String TAG = Trace.class.getPackage().getName();
    private final String src;
    private final String tag;

    private static String SRC(String string, int length) {
        return String.format("%1$-" + length + "s", new Object[]{string}).replace(' ', '.') + ":: ";
    }

    private Trace(String tag, String src) {
        this.tag = tag;
        this.src = src;
    }

    public void e(String msg) {
        if(log_level >= 1) {
            Log.e(this.tag, this.src + msg);
        }

    }

    public void e(Throwable t, String msg) {
        if(log_level >= 1) {
            Log.e(this.tag, this.src + msg, t);
        }

    }

    public void e(Throwable t) {
        if(log_level >= 1) {
            Log.e(this.tag, this.src + t.toString());
        }

    }

    public void e(Exception e) {
        if(log_level >= 1) {
            Log.e(this.tag, this.src + e.toString());
        }

    }

    public void w(String msg) {
        if(log_level >= 2) {
            Log.w(this.tag, this.src + msg);
        }

    }

    public void i(String msg) {
        if(log_level >= 3) {
            Log.i(this.tag, this.src + msg);
        }

    }

    public void d(String msg) {
        if(log_level >= 4) {
            Log.d(this.tag, this.src + msg);
        }

    }

    public void v(String msg) {
        if(log_level >= 5) {
            Log.v(this.tag, this.src + msg);
        }
    }

    public static void set(int level) {
        log_level = level;
    }

    public static Trace getInstance() {
        return new Trace(TAG, SRC((new Exception()).getStackTrace()[1].getClassName(), 50));
    }

    public static Trace getInstance(String tag) {
        return new Trace(tag, SRC((new Exception()).getStackTrace()[1].getClassName(), 50));
    }
}
