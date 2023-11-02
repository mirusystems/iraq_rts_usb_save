/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : save bitmap
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2018.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveBitmap {
    public synchronized static boolean save(byte [] pixels, int width, int height, String filePath, boolean ReverseSave, boolean bmpSave) throws IOException {
        byte[] cbitarray = BitmapUtil.rawToBitmapByteArray(pixels,  width,  height);
        if(bmpSave)
        {
	        FileOutputStream fos = new FileOutputStream(filePath);  //BMP저장
	        fos.write(cbitarray);
	        fos.getFD().sync();
	        fos.close();
        }
        else
        {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			Bitmap bm = BitmapFactory.decodeByteArray(cbitarray, 0, cbitarray.length, options);   //JPEG 저장
			FileOutputStream out = new FileOutputStream(filePath);
			bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			out.getFD().sync();
			out.close();
        }
		File f = new File(filePath);
		if(f.exists()) {
			f.setReadable(true, false);
			f.setWritable(true, false);
			f.setExecutable(true, false);
		}
        return true;
    }
}
