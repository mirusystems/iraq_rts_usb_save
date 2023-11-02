/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : save from raw bitmap data to bmp or jpg image
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
import java.nio.ByteBuffer;

public class SaveBmp {
    private static final int BMP_WIDTH_OF_TIMES = 4;
    private static final int BYTE_PER_PIXEL = 1;

    public synchronized static boolean save(byte[] pixels, int width, int height, String filePath, boolean ReverseSave, boolean bmpSave) throws IOException {

        long start = System.currentTimeMillis();
        if (filePath == null) {
            return false;
        }

        byte[] dummyBytesPerRow = null;
        boolean hasDummy = false;
        int rowWidthInBytes = BYTE_PER_PIXEL * width; //source image width * number of bytes to encode one pixel.
        if (rowWidthInBytes % BMP_WIDTH_OF_TIMES > 0) {
            hasDummy = true;
            dummyBytesPerRow = new byte[(BMP_WIDTH_OF_TIMES - (rowWidthInBytes % BMP_WIDTH_OF_TIMES))];
            for (int i = 0; i < dummyBytesPerRow.length; i++) {
                dummyBytesPerRow[i] = (byte) 0xFF;
            }
        }

        int imageSize = (rowWidthInBytes + (hasDummy ? dummyBytesPerRow.length : 0)) * height;
        //file headers size
        int imageDataOffset = 0x36 + 1024;

        //final size of the file
        int fileSize = imageSize + imageDataOffset;

        //Android Bitmap Image Data
//        orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        //       showMemoryStatusLog();
        //ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        /**
         * BITMAP FILE HEADER Write Start
         **/
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x4D);

        //size
        buffer.put(writeInt(fileSize));

        //reserved
        buffer.put(writeShort((short) 0));
        buffer.put(writeShort((short) 0));

        //image data start offset
        buffer.put(writeInt(imageDataOffset));

        /** BITMAP FILE HEADER Write End */

        //*******************************************

        /** BITMAP INFO HEADER Write Start */
        //size
        buffer.put(writeInt(0x28));

        //width, height
        //if we add 3 dummy bytes per row : it means we add a pixel (and the image width is modified.
        buffer.put(writeInt(width + (hasDummy ? (dummyBytesPerRow.length == 3 ? 1 : 0) : 0)));
        buffer.put(writeInt(height));

        //planes
        buffer.put(writeShort((short) 1));

        //bit count
        buffer.put(writeShort((short) (BYTE_PER_PIXEL * 8)));

        //bit compression
        buffer.put(writeInt(0));

        //image data size
        buffer.put(writeInt(imageSize));

        //horizontal resolution in pixels per meter
        buffer.put(writeInt(0));

        //vertical resolution in pixels per meter (unreliable)
        buffer.put(writeInt(0));

        buffer.put(writeInt(0));
        buffer.put(writeInt(0));

        /** BITMAP INFO HEADER Write End */
        //RGB Quard 256 write...
        for (int i = 0; i < 256; ++i) {
            buffer.put((byte) i);
            buffer.put((byte) i);
            buffer.put((byte) i);
            buffer.put((byte) 0);
        }

        if (!ReverseSave)
            buffer.put(pixels);
        else {
            int row = height;
            int col = width;
            int startPosition = (row - 1) * col;
            int endPosition = row * col;
            while (row > 0) {
                for (int i = startPosition; i < endPosition; i++) {
                    buffer.put((byte) (pixels[i] & 0xFF));
                }
                if (hasDummy) {
                    buffer.put(dummyBytesPerRow);
                }
                row--;
                endPosition = startPosition;
                startPosition = startPosition - col;
            }
        }
        if (bmpSave) {
            FileOutputStream fos = new FileOutputStream(filePath);  //BMP저장
            fos.write(buffer.array());
            fos.getFD().sync();
            fos.close();
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            Bitmap bm = BitmapFactory.decodeByteArray(buffer.array(), 0, buffer.limit(), options);   //JPEG 저장
            FileOutputStream out = new FileOutputStream(filePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.getFD().sync();
            out.close();
        }
        File f = new File(filePath);
        if (f.exists()) {
            f.setReadable(true, false);
            f.setWritable(true, false);
            f.setExecutable(true, false);
        }
        return true;
    }

    /**
     * Write integer to little-endian
     *
     * @param value
     * @return
     * @throws IOException
     */
    private static byte[] writeInt(int value) throws IOException {
        byte[] b = new byte[4];

        b[0] = (byte) (value & 0x000000FF);
        b[1] = (byte) ((value & 0x0000FF00) >> 8);
        b[2] = (byte) ((value & 0x00FF0000) >> 16);
        b[3] = (byte) ((value & 0xFF000000) >> 24);

        return b;
    }

    /**
     * Write short to little-endian byte array
     *
     * @param value
     * @return
     * @throws IOException
     */
    private static byte[] writeShort(short value) throws IOException {
        byte[] b = new byte[2];

        b[0] = (byte) (value & 0x00FF);
        b[1] = (byte) ((value & 0xFF00) >> 8);

        return b;
    }
}
