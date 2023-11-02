/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : 7z compress/decompress
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2018.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.utility;

import android.content.Context;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SevenZip {

    private static final String LOG_TAG = SevenZip.class.getSimpleName();

    // 예제 위치
    // http://commons.apache.org/proper/commons-compress/examples.html

    public static void zip(Context context, String entryFileName, String outputFileName) {
        File entryFile = new File(context.getFilesDir(), entryFileName);
        File outputFile = new File(context.getFilesDir(), outputFileName);

        SevenZOutputFile sevenZOutput = null;
        try {
            sevenZOutput = new SevenZOutputFile(outputFile);
            SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(entryFile, entryFile.getName());
            sevenZOutput = new SevenZOutputFile(outputFile);
            sevenZOutput.putArchiveEntry(entry);

            // write 1 (파일 사이즈에 따라 OOM 생길 수 있을 듯 함)
//            FileInputStream fis = new FileInputStream(entryFile);
//            byte[] data = IOUtils.toByteArray(fis);
//            sevenZOutput.write(data);

            // write 2 (OOM 회피용 고정 버퍼 쓰기 반복)
            FileInputStream in = new FileInputStream(entryFile);
            byte buffer[] = new byte[8192];
            int len;
            int transferredBytes = 0;
            while ((len = in.read(buffer)) > 0) {
                sevenZOutput.write(buffer, 0, len);
                transferredBytes += len;
                if (transferredBytes > 0) {
                    long fileSize = entryFile.length();
//                    Log.d(LOG_TAG, "File size: " + fileSize + ", Transferred: " + transferredBytes + " bytes.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sevenZOutput != null) {
                    sevenZOutput.closeArchiveEntry();
                    sevenZOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzip(Context context, String entryFileName) {
        File entryFile = new File(context.getFilesDir(), entryFileName);
        SevenZFile sevenZFile = null;

        try {
            sevenZFile = new SevenZFile(entryFile);

            // 압축된 파일 여럿일 경우, 순회
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                File outputFile = new File(context.getFilesDir(), entry.getName());
                FileOutputStream fos = new FileOutputStream(outputFile);

                // write 1 (파일 사이즈에 따라 OOM 생길 수 있을 듯 함)
//                byte[] content = new byte[(int) entry.getSize()];
//                sevenZFile.read(content, 0, content.length);
//                fos.write(content);

                // write2 (OOM 회피용 고정 버퍼 쓰기 반복)
                byte buffer[] = new byte[8192];
                int len;
                int transferredBytes = 0;
                while ((len = sevenZFile.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    transferredBytes += len;
                    if (transferredBytes > 0) {
                        long fileSize = entry.getSize();
//                        Log.d(LOG_TAG, "File size: " + fileSize + ", Transferred: " + transferredBytes + " bytes.");
                    }
                }
//                fos.flush();
//                fos.getFD().sync();
                fos.close();
                entry = sevenZFile.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFile(String zipPath, String entryFileName, String outputFileName) {
        File entryFile = new File(zipPath, entryFileName);
        File outputFile = new File(zipPath, outputFileName);

        SevenZOutputFile sevenZOutput = null;
        try {
            sevenZOutput = new SevenZOutputFile(outputFile);
            SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(entryFile, entryFile.getName());
            sevenZOutput = new SevenZOutputFile(outputFile);
            sevenZOutput.putArchiveEntry(entry);

            // write 1 (파일 사이즈에 따라 OOM 생길 수 있을 듯 함)
//            FileInputStream fis = new FileInputStream(entryFile);
//            byte[] data = IOUtils.toByteArray(fis);
//            sevenZOutput.write(data);

            // write 2 (OOM 회피용 고정 버퍼 쓰기 반복)
            FileInputStream in = new FileInputStream(entryFile);
            byte buffer[] = new byte[8192];
            int len;
            int transferredBytes = 0;
            while ((len = in.read(buffer)) > 0) {
                sevenZOutput.write(buffer, 0, len);
                transferredBytes += len;
                if (transferredBytes > 0) {
                    long fileSize = entryFile.length();
//                    Log.d(LOG_TAG, "File size: " + fileSize + ", Transferred: " + transferredBytes + " bytes.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sevenZOutput != null) {
                    sevenZOutput.closeArchiveEntry();
                    sevenZOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzipfile(String zipPathName, String targetPath, String subDir, String targetName) {
        File entryFile = new File(zipPathName);
        SevenZFile sevenZFile = null;

//        Log.d("7ZIP", "7unzipfile step1");
        try {
//            Log.d("7ZIP", "7unzipfile step2");
            sevenZFile = new SevenZFile(entryFile);
//            Log.d("7ZIP", "7unzipfile step3");

            // 압축된 파일 여럿일 경우, 순회
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
 //           Log.d("7ZIP", "7unzipfile step4");
            while (entry != null) {
//                Log.d("7ZIP", "cName="+entry.getName());
                if(!entry.isDirectory())
                {
                    String compName = (subDir != null)?subDir+"/"+targetName:targetName;
                    if(entry.getName().equals(compName)) {
                        File outputFile = new File(targetPath, targetName);
                        FileOutputStream fos = new FileOutputStream(outputFile);

                        // write2 (OOM 회피용 고정 버퍼 쓰기 반복)
                        byte buffer[] = new byte[8192];
                        int len;
                        int transferredBytes = 0;
                        while ((len = sevenZFile.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                            transferredBytes += len;
                            if (transferredBytes > 0) {
                                long fileSize = entry.getSize();
//                                Log.d(LOG_TAG, "File size: " + fileSize + ", Transferred: " + transferredBytes + " bytes.");
                            }
                        }
//                        fos.flush();
//                        fos.getFD().sync();
                        fos.close();
                        break;
                    }
                }
                entry = sevenZFile.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
