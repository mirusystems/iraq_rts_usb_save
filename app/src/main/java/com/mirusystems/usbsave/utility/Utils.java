package com.mirusystems.usbsave.utility;

/**
 * Created by oyuhaanaa on 2017. 4. 23..
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    private static final String[] HEX_CHARS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    Utils() {
    }

    public static boolean compare(byte[] v, byte[] w) {
        return compare(v, 0, v.length, w, 0, w.length);
    }

    public static boolean compare(byte[] v, int vOffset, int vLen, byte[] w, int wOffset, int wLen) {
        if(vLen == wLen && v.length >= vOffset + vLen && w.length >= wOffset + wLen) {
            for(int i = 0; i < vLen; ++i) {
                if(v[i + vOffset] != w[i + wOffset]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static byte[] getDwordBytes(int value) {
        byte[] ret = new byte[]{(byte)(value >> 24 & 255), (byte)(value >> 16 & 255), (byte)(value >> 8 & 255), (byte)(value >> 0 & 255)};
        return ret;
    }

    public static byte[] getOneBytes(int value) {
        byte[] ret = new byte[]{(byte)(value & 255)};
        return ret;
    }

    public static byte[] getShortBytes(int value) {
        byte[] ret = new byte[]{(byte)(value >> 8 & 255), (byte)(value >> 0 & 255)};
        return ret;
    }

    public static byte[] getBytes(byte[] src, int srcPos, int length) {
        if(length == 0) {
            return null;
        } else if(src.length < srcPos + length) {
            return null;
        } else {
            byte[] temp = new byte[length];
            System.arraycopy(src, srcPos, temp, 0, length);
            return temp;
        }
    }

    public static byte[] getBytes(char[] src, int off, int len) {
        if((len & 1) != 0) {
            throw new IllegalArgumentException("The argument \'len\' can not be odd value");
        } else {
            byte[] buffer = new byte[len / 2];

            for(int i = 0; i < len; ++i) {
                char nib = src[off + i];
                int var6;
                if(48 <= nib && nib <= 57) {
                    var6 = nib - 48;
                } else if(65 <= nib && nib <= 70) {
                    var6 = nib - 65 + 10;
                } else {
                    if(97 > nib || nib > 102) {
                        throw new IllegalArgumentException("The argument \'src\' can contains only HEX characters");
                    }

                    var6 = nib - 97 + 10;
                }

                if((i & 1) != 0) {
                    buffer[i / 2] = (byte)(buffer[i / 2] + var6);
                } else {
                    buffer[i / 2] = (byte)(var6 << 4);
                }
            }

            return buffer;
        }
    }

    public static byte[] getBytes(char[] src) {
        return getBytes((char[])src, 0, src.length);
    }

    public static byte[] getBytes(String s) {
        char[] src = s.replaceAll("\\s+", "").toCharArray();
        return getBytes(src);
    }

    public static byte[] getBytes(String s, char delimiter) {
        char[] src = s.toCharArray();
        int srcLen = 0;
        char[] var7 = src;
        int var6 = src.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            char c = var7[var5];
            if(c != delimiter) {
                src[srcLen++] = c;
            }
        }

        return getBytes((char[])src, 0, srcLen);
    }

    public static String dump(byte[] bytes) {
        return dump(bytes, 0, bytes.length);
    }

    public static String dump(byte[] bytes, int paramInt1, int paramInt2) {
        if(bytes == null) {
            return "null";
        } else {
            char[] arrayOfChar = new char[16];
            StringBuffer localStringBuffer = new StringBuffer(256);
            if(paramInt1 + paramInt2 > bytes.length) {
                paramInt2 = bytes.length - paramInt1;
            }

            int i = paramInt1;

            while(i < paramInt1 + paramInt2) {
                localStringBuffer.append(hexifyShort(i));
                localStringBuffer.append(":  ");

                for(int j = 0; j < 16; ++i) {
                    if(i >= paramInt1 + paramInt2) {
                        localStringBuffer.append("   ");
                        arrayOfChar[j] = 32;
                    } else {
                        int k = bytes[i] & 255;
                        localStringBuffer.append(hexifyShort(k)).append(' ');
                        arrayOfChar[j] = k >= 32 && k < 127?(char)k:46;
                    }

                    ++j;
                }

                localStringBuffer.append(' ').append(arrayOfChar).append("\n");
            }

            return localStringBuffer.toString();
        }
    }

    public static String dump(byte[] data, int offset, int length, int widths, int indent) {
        StringBuffer buffer = new StringBuffer(80);
        if(data != null && widths != 0 && length >= 0 && indent >= 0) {
            while(length > 0) {
                int i;
                for(i = 0; i < indent; ++i) {
                    buffer.append(' ');
                }

                buffer.append(hexifyShort(offset));
                buffer.append("  ");
                int ofs = offset;
                int len = widths < length?widths:length;

                for(i = 0; i < len; ++ofs) {
                    buffer.append(HEX_CHARS[data[ofs] >>> 4 & 15]);
                    buffer.append(HEX_CHARS[data[ofs] & 15]);
                    buffer.append(' ');
                    ++i;
                }

                while(i < widths) {
                    buffer.append("   ");
                    ++i;
                }

                buffer.append(' ');
                ofs = offset;

                for(i = 0; i < len; ++ofs) {
                    char ch = (char)(data[ofs] & 255);
                    if(ch < 32 || ch >= 127) {
                        ch = 46;
                    }

                    buffer.append(ch);
                    ++i;
                }

                buffer.append('\n');
                offset += len;
                length -= len;
            }

            return buffer.toString();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static byte[] append(byte[] data, byte[] more) {
        int length = data.length + more.length;
        byte[] temp = new byte[length];
        if(data.length > 0) {
            System.arraycopy(data, 0, temp, 0, data.length);
        }

        if(more.length > 0) {
            System.arraycopy(more, 0, temp, data.length, more.length);
        }

        return temp;
    }

    public static String hexify(byte[] bytes) {
        if(bytes == null) {
            return "null";
        } else {
            StringBuffer localStringBuffer = new StringBuffer(256);
            int i = 0;

            for(int j = 0; j < bytes.length; ++j) {
                if(i > 0) {
                    localStringBuffer.append(' ');
                }

                localStringBuffer.append(HEX_CHARS[bytes[j] >> 4 & 15]);
                localStringBuffer.append(HEX_CHARS[bytes[j] & 15]);
                ++i;
                if(i == 16) {
                    localStringBuffer.append('\n');
                    i = 0;
                }
            }

            return localStringBuffer.toString();
        }
    }

    public static String hexify(byte[] buffer, char delimiter, int length) {
        StringBuffer sb = new StringBuffer((length << 1) + (delimiter == 0?0:length));

        for(int i = 0; i < length; ++i) {
            sb.append(HEX_CHARS[buffer[i] >>> 4 & 15]);
            sb.append(HEX_CHARS[buffer[i] & 15]);
            if(delimiter != 0 && i < length - 1) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }

    public static String hexifyByte(byte paramInt) {
        return HEX_CHARS[(paramInt & 255 & 240) >>> 4] + HEX_CHARS[paramInt & 15];
    }

    public static String hexifyShort(int paramInt) {
        return HEX_CHARS[(paramInt & '\uffff' & '\uf000') >>> 12] + HEX_CHARS[(paramInt & 4095 & 3840) >>> 8] + HEX_CHARS[(paramInt & 255 & 240) >>> 4] + HEX_CHARS[paramInt & 15];
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException(String.format("File %s is a directory", new Object[]{file}));
            } else if(!file.canRead()) {
                throw new IOException(String.format("File %s does not have read permissions", new Object[]{file}));
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException(String.format("File %s does not exist", new Object[]{file}));
        }
    }

    public static byte[] InputStreamtoByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return buffer;
    }

    public static byte[] readFile(String path) throws IOException {
        if(path == null) {
            throw new NullPointerException("path");
        } else {
            FileInputStream fis = null;

            byte[] var3;
            try {
                fis = openInputStream(new File(path));
                var3 = InputStreamtoByteArray(fis);
            } finally {
                if(fis != null) {
                    fis.close();
                }

            }

            return var3;
        }
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (int len; (len = is.read(buffer)) != -1; ) {
            os.write(buffer, 0, len);
        }
        os.flush();
        return os.toByteArray();
    }

    public static byte[] readFileResource(Context context, String resourcePath) throws IOException {
        InputStream is = context.getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("cannot find resource: " + resourcePath);
        }
        return getBytesFromInputStream(is);
    }


}

