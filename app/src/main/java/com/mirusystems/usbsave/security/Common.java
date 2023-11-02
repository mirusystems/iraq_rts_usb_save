/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : common(not in use)
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2018.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.security;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {
	
	public static boolean isBCardInit=false;
	public static byte[] signCert;
	public static byte[] signKey;
	public static byte[] secretCert;
	public static byte[] secretKey;
	public static byte[] commID;
	
	public static byte[] string2Bytearray(String str, int len){
		byte[] strt = null;
		byte[] result=null;
		try {
			strt = str.getBytes("MS949");
			result = new byte[len];
			for(int i=0; i<len;i++){
				if(i<strt.length)
					result[i]=strt[i];
				else
					result[i]=0;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	public static String bytearray2String(byte[] bytearr){
		String result=null;
		try {
			result = new String(bytearr, "MS949");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static byte[] subByteArray(byte[] arr, int len){
		byte[] result=new byte[len];
			for(int i=0; i<len;i++)
				result[i]=arr[i];
		return result;
	}
	
	public static int byteArrayToInt(byte[] b, int len) 
	{
	    int value = 0;
	    for (int i = 0; i < len; i++) {
	        int shift = (len - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value;
	}
	
	public static byte[] getFileModified(File f){
		String modDate = "";
		if(f.exists()){
			Date date = new Date(f.lastModified());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

			modDate = sdf.format(date);
		}else{
		}
	
		return modDate.getBytes();
	}
	
	public String substring(String data, String header, String footer ){
		return (new StringBuffer( data )).substring( header.length(), data.length()-footer.length() );
	}
}
