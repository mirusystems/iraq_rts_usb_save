package com.mirusystems.usbsave.utility;


import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void zip(String sourceDirectory, String zipFile) {
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File sourceFolder = new File(sourceDirectory);
            addFilesToZip(sourceFolder, sourceFolder.getName(), zos);
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFilesToZip(File sourceFile, String parent, ZipOutputStream zos) throws IOException {
        if (sourceFile.isDirectory()) {
            for (File file : sourceFile.listFiles()) {
                addFilesToZip(file, parent + File.separator + sourceFile.getName(), zos);
            }
        } else {
            FileInputStream fis = new FileInputStream(sourceFile);
            ZipEntry zipEntry = new ZipEntry(parent + File.separator + sourceFile.getName());
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    public static void unzip(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];

        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                if (ze.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
