/*
 * Filename	: ByteUtil.java
 * Function	:
 * Comment 	:
 * History	: 2015/09/03, ruinnel, Create
 *
 * Version	: 1.0
 * Author   : Copyright (c) 2015 by JC Square Inc. All Rights Reserved.
 */

package com.mirusystems.usbsave.utility;


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Pattern;

public class ByteUtil {
	private static final String TAG = ByteUtil.class.getSimpleName();
	private static final String[] HEX_CHARS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

	/**
	 * Returns an hexadecimal string representation of the given byte array, where each byte is represented by two
	 * hexadecimal characters and padded with a zero if its value is comprised between 0 and 15 (inclusive).
	 * As an example, this method will return "6d75636f0a" when called with the byte array {109, 117, 99, 111, 10}.
	 *
	 * @param bytes the array of bytes for which to get an hexadecimal string representation
	 * @return an hexadecimal string representation of the given byte array
	 */
	
	/**
	 * byte를 String로 변환합니다.
	 * @param b
	 * @return
	 */
	public static String toHexString(byte b) {
		String hexByte;
		hexByte = Integer.toHexString(b & 0xFF).toUpperCase();
		if (hexByte.length() == 1) {
			hexByte = "0" + hexByte;
		}
		return hexByte;
	}

	public static String toHexString(byte[] bytes, byte delimeter) {
		if (bytes == null || bytes.length == 0) return null;
		StringBuffer sb = new StringBuffer();

		int bytesLen = bytes.length;
		String hexByte;
		String delibyte;
		for(int i=0; i<bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if(hexByte.length()==1)
				sb.append('0');
			sb.append(hexByte);
			if(delimeter != 0) {
				delibyte = Integer.toHexString(delimeter & 0xFF).toUpperCase();
				sb.append(delibyte);
			}
		}
		return sb.toString();
	}

	/**
	 * byte[]를 string로 변경합니다. 변환시 1바이트 단위로 space가 삽입됩니다.
	 * @param bytes 변환 할 데이터
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes == null || bytes.length == 0) return "00";
		StringBuffer sb = new StringBuffer();

		int bytesLen = bytes.length;
		String hexByte;
		for(int i=0; i<bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if(hexByte.length()==1)
				sb.append('0');
			sb.append(hexByte);
			sb.append(" ");
		}

		return sb.toString();
	}
	
	/**
	 * byte[]를 string로 변경합니다. 변환시 1바이트 단위로 space가 삽입됩니다.
	 * @param bytes 변환할 데이터
	 * @param length 변환 할 길이
	 * @return
	 */
	public static String toHexString(byte[] bytes, int length) {
		if (bytes == null || bytes.length < length) return "00"; 
		StringBuffer sb = new StringBuffer();

		int bytesLen = length;
		String hexByte;
		for(int i=0; i<bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if(hexByte.length()==1)
				sb.append('0');
			sb.append(hexByte);
			sb.append(" ");
		}

		return sb.toString();
	}

	public static String stringToHex0x(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format(Locale.US, "0x%X ", (int) s.charAt(i));
		}

		return result;
	}

	public static String ByteToHex0x(byte s[], int length) {
		String result = "";

		for (int i = 0; i < length; i++) {
			result += String.format(Locale.US, "%02X ", (byte)s[i]);
		}
		return result;
	}

	/**
	 * byte[]를 int로 변경합니다.(4개 바이트를 넘을 경우 제대로 동작하지 않습니다.)
	 * @param ba
	 * @return
	 */
	public static int toInt(byte[] ba) {
		int result = 0;
		for (int i = 0; i < ba.length; i++) {
			result = (result << 8) | (ba[i]  & 0x000000FF);
		}
		return result;
	}

	public static int byteToInt(byte s[], int pos) {
		int value = 0;

		value = (((int)s[pos]<< 24) & 0xFF000000) | (((int)s[pos+1] << 16) & 0x00FF0000) | (((int)s[pos+2] << 8) & 0x0000FF00) | ((int)s[pos+3] & 0x000000FF);

		return(value);
	}

	public static int byteToShort(byte s[], int pos) {
		int value = 0;

		value = ((short)s[pos] << 8) | (short)s[pos+1];

		return(value);
	}

	/**
	 * byte array를 length 만큼 확장 합니다.(0x00) 으로 채움
	 * @param val
	 * @param length
	 * @return
	 */
	public static byte[] expend(byte[] val, int length) {
		byte[] result = new byte[length];
		System.arraycopy(val, 0, result, 0, val.length);
		if (val.length < length) {
			int diff = length - val.length;
			byte[] dummy = new byte[diff];
			for (int i = 0; i < diff; i++) dummy[i] = 0x00;
			System.arraycopy(dummy, 0, result, val.length, diff);
		}
		return result;
	}
	
	/**
	 * byte의 부호비트를 무시하고 int로 변환합니다.
	 * @param b
	 * @return
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
	
	/**
	 * crc 처리용 상수
	 */
	public static final byte r1[] = {
		(byte) 0x00, (byte) 0x5e, (byte) 0xbc, (byte) 0xe2, (byte) 0x61, (byte) 0x3f, (byte) 0xdd, (byte) 0x83, 
		(byte) 0xc2, (byte) 0x9c, (byte) 0x7e, (byte) 0x20, (byte) 0xa3, (byte) 0xfd, (byte) 0x1f, (byte) 0x41 };
	
	/**
	 * crc 처리용 상수
	 */
	public static final byte r2[] = {
		(byte) 0x00, (byte) 0x9d, (byte) 0x23, (byte) 0xbe, (byte) 0x46, (byte) 0xdb, (byte) 0x65, (byte) 0xf8,
		(byte) 0x8c, (byte) 0x11, (byte) 0xaf, (byte) 0x32, (byte) 0xca, (byte) 0x57, (byte) 0xe9, (byte) 0x74 };
	
	/**
	 * crc8을 생성합니다.
	 * @param cCrc
	 * @param cData
	 * @return
	 */
	public static byte CRC8Byte(byte cCrc, byte cData) {
		byte i = (byte) ((cData ^ cCrc) & 0xff);
		cCrc = (byte) (r1[i&0xf] ^ r2[(i>>4) & 0x0f]);
		return cCrc;
	}
	
	/**
	 * crc8를 생성합니다.
	 * @param crc_in
	 * @param buff
	 * @param count
	 * @return
	 */
	public static byte getCRC8(byte crc_in, byte[] buff, int count) {
		byte rtn;
		byte inx;
		
		rtn = crc_in;
		for (int i = 0; i < count; i++) {
			inx = (byte) ((rtn ^ buff[i]) & 0xff);
			rtn = (byte) (r1[inx & 0x0f] ^ r2[(inx >>4) & 0x0f]);
		}
		return rtn;
	}

	/**
	 * 16진수 문자열을 byte array로 변환 합니다.(단, 문자열 길이는 짝수만)
	 * @param val
	 * @return
	 */
	public static byte[] hexStringToByteArray(String val) {
		if (val.length() % 2 != 0) return null;
		
		byte[] result = new byte[val.length() / 2];
		for (int i = 0; i < val.length(); i = i + 2) {
			String s = val.substring(i, i + 2);
			//Log.d(TAG, "hexStringToByteArray - " + s);
			result[i == 0 ? 0 : i / 2] = (byte)(Integer.parseInt(s, 16) & 0x000000FF);
		}
		return result;
	}

	public static byte[] IntToByteArray(int val) {
		return new byte[] {
				(byte)val,
				(byte)(val >>> 8),
				(byte)(val >>> 16),
				(byte)(val >>> 24)};
	}

	/**
	 * int를 byte array로 변경합니다.
	 * @param val
	 * @return 변환된 byte[] (4 byte)
	 */
	public static byte[] toByteArray(int val) {
		 return new byte[] {
				 (byte)(val >>> 24),
				 (byte)(val >>> 16),
				 (byte)(val >>> 8),
				 (byte)val};
	}
	
	/**
	 * int를 byte array로 변환합니다.
	 * 변환된 바이트 중 지정된 길이 만큼을 반환합니다.
	 * 변환 후의 바이트 수를 고려해서 사용해 주세요.
	 * @param val
	 * @param len
	 * @return 변환된 byte array
	 */
	public static byte[] toByteArray(int val, int len) {
		if (len > 4 || len < 0) {
			return null;
		}
		
//		if (val > Math.pow(256, len)) {
//			return null;
//		}
		byte[] ba = new byte[] {
				 (byte)(val >>> 24),
				 (byte)(val >>> 16),
				 (byte)(val >>> 8),
				 (byte)(val%256)};
		
		byte[] result = new byte[len];
		
		System.arraycopy(ba, ba.length - len, result, 0, len);
		
		return result;
	}
	
	/**
	 * XOR Checksum을 생성합니다.
	 * JRM-700용이며, 0으로 시작하는 인덱스 값을 기준으로 1번째부터 n - 2까지의 패킷에 대한 check sum 을 반환합니다.
	 * @param packet
	 * @return XOR checksum
	 */
	public static byte getCheckSum(byte[] packet) {
		byte bcc = 0x00;
		for (int i = 1; i < packet.length - 2; i++) {
			bcc = (byte) (bcc ^ packet[i]);
		}
		return bcc;
	}
	
	/**
	 * null(0x00) 으로 끝나는 문자열(byte array)를 String 타입으롤 변환합니다.
	 * @param strArray
	 * @return
	 */
	public static String byteArrayToString(byte[] strArray) {
		int idx = 0;
		if(strArray == null)
		    return null;
		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i] == 0x00) { // null
				break;
			} else {
				idx++;
			}
		}
		
		return new String(strArray, 0, idx);
	}

	public static String byteArrayToString2(byte[] strArray) {
		int idx = 0;
		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i] == 0x00 || strArray[i] == 0x0D || strArray[i] == 0x0A) { // null
				break;
			} else {
				idx++;
			}
		}

		return new String(strArray, 0, idx);
	}

	//byte[] 크기만큼 무조건 String으로 변환
	public static String rawByteArrayToString(byte[] strArray) {
		int idx = 0;
		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i] < 0x10)
				strArray[i] += 0x30;
			idx++;
		}
		return new String(strArray, 0, idx);
	}

	/**
	 * Byte Array를 지정된 size 크기로 잘라서 2차원 Byte Array로 반환합니다.
	 * @param fullpacket
	 * @param size
	 * @return
	 */
	public static byte[][] splitBytes(byte[] fullpacket, int size) {
		int len = (fullpacket.length % size) == 0 ? (fullpacket.length / size) : (fullpacket.length / size) + 1;
		byte[][] packets = new byte[len][];
		
		for (int i = 0; i < packets.length; i++) {
			int length = size;
			if (i == (packets.length -1)) {
				length = fullpacket.length - (i * size);
			}
			byte[] temp = new byte[length];
			System.arraycopy(fullpacket, i * size, temp, 0, length);
			packets[i] = temp;
		}
		
		return packets;
	}
	
	/**
	 * byte array의 일부분을 카피해 새로운 byte array를 만듭니다.
	 * @param src  - 원본 byte array
	 * @param offset  - 복사 시작 위치
	 * @param length  - 복사할 길이
	 * @return  복사된 byte array
	 */
	public static byte[] copyOf(byte[] src, int offset, int length) {
		byte[] result = new byte[length];
		System.arraycopy(src, offset, result, 0, length);
		
		return result;
	}

	public static String convNumericStrToArabicStr(String num)
	{
		if(num == null || num == "" || num.length() < 1)
			return(null);

		if(Pattern.matches("^[0-9]+$", num) == false) {
			Log.d("ByteUtil", "No numeric num String !");
			return (num);
		}

		int len = num.length();
		String arabStrg = "";
		Locale arabic = new Locale("ar");

		for(int i = 0 ; i < len ; ++i) {
			arabStrg += String.format(arabic, "%d", (num.charAt(i) - 0x30));
		}

		return(arabStrg);
	}

	public static String convArabicStrToNumericStr(String arabicStr)
	{
		if(arabicStr == null || arabicStr == "" || arabicStr.length() < 1)
			return(null);

		if(Pattern.matches("^[٠-٩]+$", arabicStr) == false) {
			Log.d("ByteUtil", "No arabic num String !");
			return (arabicStr);
		}

		String[] arabic = {"٠","١","٢","٣","٤","٥","٦","٧","٨","٩"};
		String cvtStrg = arabicStr;
		for(int i = 0 ; i < 10 ; ++i)
			cvtStrg = cvtStrg.replaceAll(arabic[i], String.format(Locale.US, "%d", i));

		return(cvtStrg);
	}

	public static byte[] append(byte[] data, byte[] more)
	{
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

	public static int parseInt(String pText) {
		try {
			return Integer.parseInt(pText);

		} catch (Exception e) {
			return 0;
		}
	}

	public static long parseLong(String pText) {
		try {
			return Long.parseLong(pText);

		} catch (Exception e) {
			return 0;
		}
	}

	public static boolean compare(byte[] v, byte[] w) {
		return compare(v, 0, v.length, w, 0, w.length);
	}

	public static boolean compare(byte[] v, int vOffset, int vLen, byte[] w, int wOffset, int wLen) {
		if(v == null || w == null || v.length < 1 || w.length < 1)
			return(false);
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

	public static byte[] getShortBytes(int value) {
		byte[] ret = new byte[]{(byte)(value >> 8 & 255), (byte)(value >> 0 & 255)};
		return ret;
	}

	public static byte[] getOneBytes(int value) {
		byte[] ret = new byte[]{(byte)(value & 255)};
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
						byte k = (byte)(bytes[i] & 0xFF);
						localStringBuffer.append(hexifyByte(k)).append(' ');
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

	public static String hexify(byte[] buffer, int length) {
		String hexstrg = "";

		for (int i = 0; i < length ; ++i)
			hexstrg += String.format(Locale.US, "%02X", (byte)buffer[i]);
		return hexstrg;
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

	public static void AppendStrToFile(String append_strg, String append_filepath) {
		try {
			File f = new File(append_filepath);
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f, true);
			fos.write(append_strg.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int convertTag(byte ch)
	{
		byte pre_nibble = (byte)((ch >> 4) & 0x0F);

		if(pre_nibble > 9)
			return(ch & 0xFF);
		else
			return((ch >> 4)*16+(ch & 0x0F));
	}

}
