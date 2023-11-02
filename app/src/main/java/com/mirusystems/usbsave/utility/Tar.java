/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : tar compress/decompress
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2018.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Tar {
    public static void unzipfile(String zipPathName, String targetPath, String subDir, String targetName) {
        try{
//            Runtime.getRuntime().exec("busybox-armv7l tar xf " + zipPathName + " -C " + targetPath + " "+ subDir + "/" + targetName);
//            Runtime.getRuntime().exec("chmod 666 " + targetPath + "/" + subDir + "/" + targetName);
            waitShellCommand("busybox-armv7l tar xf " + zipPathName + " -C " + targetPath + " "+ subDir + "/" + targetName);
            waitShellCommand("chmod 666 " + targetPath + "/" + subDir + "/" + targetName);
        }catch(Exception e){
        }
    }

    private static void waitShellCommand(String cmdstrg)
    {
        try {
            Process sp = Runtime.getRuntime().exec(cmdstrg);
            sp.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(sp.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                Log.d("TAR", "waitShellCommand: "+line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
