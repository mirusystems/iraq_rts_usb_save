/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : bitmap util
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.ByteBuffer;

public class BitmapUtil {
	private static final String TAG = "BitmapUtil";

	private BitmapUtil() { }

	/***
	 * Change the image to black and white and resize it.
	 * @param bmpOriginal  bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int width, int height)
	{

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if(bmpOriginal!=null) {
			Canvas c = new Canvas(bmpGrayscale);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
			c.drawBitmap(bmpOriginal, 0, 0, paint);
		}
		return bmpGrayscale;
	}
	public static Bitmap toGrayscale(Bitmap bmpOriginal)
	{
		return toGrayscale(bmpOriginal, bmpOriginal.getWidth(), bmpOriginal.getHeight());
	}

	/***
	 * Image를 1bit으로 변경
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toOneBit(Bitmap bmpOriginal)
	{
		bmpOriginal = toGrayscale(bmpOriginal);
		int height = bmpOriginal.getHeight();
		int width = bmpOriginal.getWidth();
		int[] pixels = new int[width * height];
		bmpOriginal.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int y = 0; y < height; y++) {
			// Iterate over width
			for (int x = 0; x < width; x++) {
				int pixel = bmpOriginal.getPixel(x, y);
				int lowestBit = pixel & 0xff;
				if(lowestBit<200)
					bmpOriginal.setPixel(x,y, Color.BLACK);
				else
					bmpOriginal.setPixel(x,y, Color.WHITE);
			}
		}
		return bmpOriginal;
	}

	static public byte[] BitmapToByteArr(Bitmap image) {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 50, byteArray);
		byte[] imageData = byteArray.toByteArray();
		return imageData;
	}

	public static byte[] IntToByteArray(int val) {
		return new byte[] {
				(byte)val,
				(byte)(val >>> 8),
				(byte)(val >>> 16),
				(byte)(val >>> 24)};
	}


	public static boolean saveBitmapToFile(Bitmap bm, String filePath){
//		Log.d(TAG, "saveBitmap bmp:"+(bm!=null)+ " path:"+filePath);
		if(bm!=null) {
			File f = new File(filePath);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(f);
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.close();
				f.setReadable(true, false);
				f.setWritable(true, false);
				f.setExecutable(true, false);
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean compressImg(String src, String dst, int quality){
		long start = System.currentTimeMillis();

		Bitmap bmp = BitmapFactory.decodeFile(src);
		if(bmp!=null) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(dst);
				bmp.compress(CompressFormat.JPEG, quality, out);
				out.getFD().sync();
				out.close();
				File f = new File(dst);
				if(f.exists()) {
					f.setReadable(true, false);
					f.setWritable(true, false);
					f.setExecutable(true, false);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SyncFailedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			long end = System.currentTimeMillis();
			Log.e(TAG+" TIME", "compress Img time:" + (end - start));
			return true;
		}
		return false;
	}

	public static Bitmap createScaledBitmap(Bitmap realImage, int maxImageSize) {
		if(realImage!=null) {
			float ratio = Math.min(
					(float) maxImageSize / realImage.getWidth(),
					(float) maxImageSize / realImage.getHeight());
			int width = Math.round((float) ratio * realImage.getWidth());
			int height = Math.round((float) ratio * realImage.getHeight());

			Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, false);
			return newBitmap;
		}
		Log.e(TAG, "createScaledBitmap:bimtap failed bitmap is null");
		return null;
	}

	public static boolean createScaledBitmap(String src, String dst, int maxImageSize) {
		long start = System.currentTimeMillis();
		Bitmap realImage = BitmapFactory.decodeFile(src);
		if(realImage!=null) {
			float ratio = Math.min(
					(float) maxImageSize / realImage.getWidth(),
					(float) maxImageSize / realImage.getHeight());
			int width = Math.round((float) ratio * realImage.getWidth());
			int height = Math.round((float) ratio * realImage.getHeight());

			Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, false);
			saveBitmapToFile(newBitmap, dst);

			long end = System.currentTimeMillis();
			Log.e(TAG + " TIME", "createScaledBmp time:" + (end - start));
			return true;
		}
		Log.e(TAG, "createScaledBitmap:string failed bitmap is null");
		return false;
	}

	public static boolean compressImg(String src, String dst, int reqWidth, int reqHeight, int quality){
		long start = System.currentTimeMillis();
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(src, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		Bitmap bmp = BitmapFactory.decodeFile(src);
		if(bmp!=null) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(dst);
				bmp.compress(CompressFormat.JPEG, quality, out);
				out.getFD().sync();
				out.close();
				File f = new File(dst);
				if(f.exists()) {
					f.setReadable(true, false);
					f.setWritable(true, false);
					f.setExecutable(true, false);
				}
				long end = System.currentTimeMillis();
				Log.e(TAG+" TIME", "compress Img time:" + (end - start));
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SyncFailedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.e(TAG, "compressImg failed bitmap is null");
		return false;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if(height>reqHeight || width>reqWidth){
			final int halfHeight = height/2;
			final int halfWidth = width/2;

			while ((halfHeight/inSampleSize)>=reqHeight && (halfWidth/inSampleSize)>=reqWidth){
				inSampleSize*=2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap loadBitmapFromFile(String filePath, int reqWidth, int reqHeight){
		Log.d("IMAGE", "imageShow:"+filePath);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;///

		return BitmapFactory.decodeFile(filePath, options);
	}


	public static Bitmap loadBitmapFromFile(String filePath){
		Log.d(TAG, "loadBitmapFromFile:"+filePath);
		byte[] arr = null;
		Bitmap bmp = null;
		try {
			File f = new File(filePath);
			FileInputStream fi = new FileInputStream(f);
			arr = new byte[fi.available()];
			fi.read(arr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(arr!=null)
			bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
		return bmp;
	}

	/***
	 * Bitmap을 1비트 파일로 저장하는 함수. 비트맵의 세로 길이가 8의 배수일때 정상작동하지만 아닐때는 문제 샌김
	 * @param mBitmap 비트맵 객체
	 * @param mPath 비트맵을 1비트로 저장할 위치
	 * @return 1비트로 변환된 비트맵 배열
	 * @throws IOException
	 */
	public static byte[] bmpSave(Bitmap mBitmap, String mPath) throws IOException {
		Log.d(TAG, "bmpSave:"+mPath);
		long checkTime = System.currentTimeMillis();
		if (mBitmap == null) return null;
//		if (mPath == null) return  null;

		// 이미지 사이즈
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();
		// 픽셀 갯수
		int[] mPixel = new int[width *height];
		// 이미지 크기 (1비트로 구성된 이미지이므로 8을 나누어 줘야 함)

		boolean hasDummy = (width%8>0);
		int rowWidthInBytes = width/8+(hasDummy?1:0);


		int imgSize = rowWidthInBytes*height;
		// 비트맵 데이터를 찾을 수 있는 시작 주소
		int imgOffset = 0x3E;
		// 비트맵 이미지의 최종 사이즈
		int fileSize = imgSize + imgOffset;
		// 받아온 Bitmap으로부터 픽셀 정보를 int 배열로 받아옴
		// 픽셀 한 개당 32비트의 데이터로 가져오게 됨 (FF FF FF FF)
		// 순서대로 (Alpha Red Green Blue)임
		mBitmap.getPixels(mPixel, 0, width, 0, 0, width, height);
		// 파일 정보를 담아둘 바이트 버퍼를 생성한다
		ByteBuffer mBuffer = ByteBuffer.allocate(fileSize);
		ByteBuffer mBuff = ByteBuffer.allocate(imgSize);

		// 헤더 시작
		mBuffer.put((byte) 0x42);
		mBuffer.put((byte) 0x4D);
		mBuffer.put(putInt(fileSize));
		mBuffer.put(putShort((short) 0));
		mBuffer.put(putShort((short) 0));
		mBuffer.put(putInt(imgOffset));
		mBuffer.put(putInt(0x28));
		mBuffer.put(putInt(width));
		mBuffer.put(putInt(height));
		mBuffer.put(putShort((short) 1));
		mBuffer.put(putShort((short) 1));
		mBuffer.put(putInt(0));
		mBuffer.put(putInt(imgSize));
		mBuffer.put(putInt(0));
		mBuffer.put(putInt(0));
		mBuffer.put(putInt(0));
		mBuffer.put(putInt(0));
		// 여기서 사용할 색상 선택
		// 1비트로 표현되므로 두 가지 색상만 선택 가능
		mBuffer.put(putInt(0)); // 검정색
		mBuffer.put(putInt(0x00FFFFFF)); // 흰색

		// 여기부터 비트맵 데이터
		int startPosition = 0;
		int endPosition = 0;
//        Log.d("1bit", "height:"+height+" width:"+width+" rowWidthinbyte:"+rowWidthInBytes);
		while (height > 0) {
			startPosition = (height - 1) * width;
//            Log.e("1bit", "start:"+startPosition+" height:"+height);
			endPosition = height * width;
			int[] iTemp = new int[8];
			for (int i = startPosition; i < endPosition;) {
				if ((i + 7) <= endPosition) {
//                    Log.e("1bit", "i+7<=end i:"+i+" end:"+endPosition);
					for (int j = 7; j >= 0; j--) {
//                        Log.d("1bit", "j:"+j+" i:"+i);
						iTemp[j] = mPixel[i++];
					}
				} else {
					int mLength = endPosition - i;
//                    Log.e("1bit", "i+7>end i:"+i+" end:"+endPosition);
					for (int j = 7; j >= (8 - mLength); j--) {
//                        Log.d("1bit", "j last:"+j+" i:"+i);
						iTemp[j] = mPixel[i++];
					}
					for (int j = (7 - mLength); j >= 0; j--) {
//                        Log.d("1bit", "j dummy:"+j);
						iTemp[j] = 0x00FFFFFF;
					}
				}
				byte b = putIntToBit(iTemp);
//                Log.d("1bit", "b:"+toHexString(b));
				mBuffer.put(b);
//				mBuffer.put((byte)0xff); //for white printing...
				mBuff.put(b);
			}
			height--;
		}
		if(mPath != null) {
			FileOutputStream fos = new FileOutputStream(mPath);
			fos.write(mBuffer.array());
			fos.close();
			File f = new File(mPath);
			if(f.exists()) {
				f.setReadable(true, false);
				f.setWritable(true, false);
				f.setExecutable(true, false);
			}
		}
		Log.e("1bit", "Onebit BMP make elapsed time:"+(System.currentTimeMillis()-checkTime));
		return mBuffer.array();
	}

	public static String toHexString(byte b) {
		String hexByte;
		hexByte = Integer.toBinaryString(b & 0xFF).toUpperCase();
		if (hexByte.length() == 1) {
			hexByte = "0" + hexByte;
		}
		return hexByte;
	}
	private static byte[] putInt(int value) throws IOException {
		byte[] b = new byte[4];

		b[0] = (byte) (value & 0x000000FF);
		b[1] = (byte) ((value & 0x0000FF00) >> 8);
		b[2] = (byte) ((value & 0x00FF0000) >> 16);
		b[3] = (byte) ((value & 0xFF000000) >> 24);

		return b;
	}
	private static byte[] putShort(short value) throws IOException {
		byte[] b = new byte[2];

		b[0] = (byte) (value & 0x00FF);
		b[1] = (byte) ((value & 0xFF00) >> 8);

		return b;
	}
	private static byte putIntToBit(int[] value) throws IOException {
		byte bReturn = (byte) 0xFF;
		byte[] b = new byte[3];

		for (int i = 0; i < value.length; i++) {
			// 알파값은 버리고 RGB값만 사용함
			b[0] = (byte) (value[i] & 0x000000FF);
			b[1] = (byte) ((value[i] & 0x0000FF00) >> 8);
			b[2] = (byte) ((value[i] & 0x00FF0000) >> 16);
			if ((b[0] != (byte) 0xFF) || (b[1] != (byte) 0xFF) || (b[2] != (byte) 0xFF)) {
				// 셋중 하나라도 FF가 아니면 검정색처리
				bReturn -= Math.pow(2, i);
			}
		}
		return (byte) bReturn;
	}

	public static Bitmap rotate(int angle, Bitmap source){
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
	}

	public static byte[] rawToBitmapByteArray(byte [] pixels, int width, int height)  throws IOException {

		byte[] dummyBytesPerRow = null;
		boolean hasDummy = false;
		int rowWidthInBytes = width; //source image width * number of bytes to encode one pixel.
		if(rowWidthInBytes%4>0){
			hasDummy=true;
			dummyBytesPerRow = new byte[(4-(rowWidthInBytes%4))];
			for(int i = 0; i < dummyBytesPerRow.length; i++){
				dummyBytesPerRow[i] = (byte)0xFF;
			}
		}
		int imageSize = (rowWidthInBytes+(hasDummy?dummyBytesPerRow.length:0)) * height;
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
		buffer.put((byte)0x42);
		buffer.put((byte)0x4D);

		//size
		buffer.put(putInt(fileSize));

		//reserved
		buffer.put(putShort((short)0));
		buffer.put(putShort((short)0));

		//image data start offset
		buffer.put(putInt(imageDataOffset));

		/** BITMAP FILE HEADER Write End */

		//*******************************************

		/** BITMAP INFO HEADER Write Start */
		//size
		buffer.put(putInt(0x28));

		//width, height
		//if we add 3 dummy bytes per row : it means we add a pixel (and the image width is modified.
		buffer.put(putInt(width+(hasDummy?(dummyBytesPerRow.length==3?1:0):0)));
		buffer.put(putInt(height));

		//planes
		buffer.put(putShort((short)1));

		//bit count
		buffer.put(putShort((short)8));

		//bit compression
		buffer.put(putInt(0));

		//image data size
		buffer.put(putInt(imageSize));

		//horizontal resolution in pixels per meter
		buffer.put(putInt(0));

		//vertical resolution in pixels per meter (unreliable)
		buffer.put(putInt(0));

		buffer.put(putInt(0));
		buffer.put(putInt(0));

		/** BITMAP INFO HEADER Write End */
		//RGB Quard 256 write...
		for(int i = 0 ; i < 256 ; ++i)
		{
			buffer.put((byte)i);
			buffer.put((byte)i);
			buffer.put((byte)i);
			buffer.put((byte)0);
		}

		if(hasDummy == false)
			buffer.put(pixels);
		else
		{
			int row = height;
			int col = width;
			for(int i = 0 ; i < height ; ++i)
			{
				buffer.put(pixels, i*width, width);
				buffer.put(dummyBytesPerRow);
			}
		}
		return(buffer.array());
	}

	public static Bitmap rawImageToBitmap(byte[] raw_data, int width, int height, int maxHeight)  throws IOException {
//		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
//		bmp.copyPixelsFromBuffer(ByteBuffer.wrap(raw_data));
		byte[] bitbytes = rawToBitmapByteArray(raw_data, width, height);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		Bitmap bmp;
		if(height > maxHeight)
		{
			options.inSampleSize = 4;
			 bmp = BitmapFactory.decodeByteArray(bitbytes, 0, bitbytes.length, options);
		}
		else
		{
			options.inSampleSize = 2;
			bmp = BitmapFactory.decodeByteArray(bitbytes, 0, bitbytes.length, options);
		}

		return bmp;
	}


	public static byte[] bitmapToByteArray( Bitmap bitmap ) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] intArray = new int[width * height];
		byte[] rawbytes = new byte[width * height];
		int index = 0;

		bitmap.getPixels(intArray, 0, width, 0, 0, width, height);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++) {
				rawbytes[index] = (byte)(intArray[index] & 0xff);
				index++;
			}
		}
		return(rawbytes);
	}

	public static byte[] bitmapToBmpByteArray(Bitmap mBitmap) throws IOException {
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();
		byte[] raw_data = bitmapToByteArray(mBitmap);
		byte[] bitbytes = rawToBitmapByteArray(raw_data, width, height);
		return(bitbytes);
	}

	public static byte[] bitmapToJpgByteArray( Bitmap bitmap ) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
		bitmap.compress( CompressFormat.JPEG, 100, stream) ;
		byte[] byteArray = stream.toByteArray() ;
		return byteArray ;
	}

	public static byte[] bitmapToPngByteArray( Bitmap bitmap ) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
		bitmap.compress( CompressFormat.PNG, 100, stream) ;
		byte[] byteArray = stream.toByteArray() ;
		return byteArray ;
	}
}