package com.mirusystems.usbsave.label;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.google.common.io.Files;

import com.mirusystems.usbsave.App;
import com.mirusystems.usbsave.data.PollingStation;
import com.mirusystems.utils.image.BmpUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class PsoLabel {
    private static final String TAG = "PsoLabel";

    private String gov;
    private String ed;
    private String vrc;
    private String pc;
    private String ps;
    private String qr;
    private String title;

    private boolean isSpare;

    int mWidth;
    int mHeight;
    int mHorizontalMargin;
    int mVerticalMargin;
    int mQrWidth;
    int mQrHeight;
    int mTextSize;

    public PsoLabel() {
        gov = "32";
        ed = "01";
        vrc = "1721";
        pc = "172101";
        ps = "17210101";

        if (App.isEdEnabled()) {
            qr = String.format(Locale.ENGLISH, "%s%s-%s-%s-%s", gov, ed, ps.substring(0, 4), ps.substring(4, 6), ps.substring(6, 8));
        } else {
            qr = String.format(Locale.ENGLISH, "%s-%s-%s-%s", gov, ps.substring(0, 4), ps.substring(4, 6), ps.substring(6, 8));
        }
        title = App.getTitle();
        setPixels();
    }

    public PsoLabel(PollingStation p) {
        String psCode = String.valueOf(p.getPsCode());
        if (App.isEdEnabled()) {
            qr = String.format(Locale.ENGLISH, "%02d%02d-%s-%s-%s", p.getGovCode(), p.getEdCode(), psCode.substring(0, 4), psCode.substring(4, 6), psCode.substring(6, 8));
        } else {
            qr = String.format(Locale.ENGLISH, "%02d-%s-%s-%s", p.getGovCode(), psCode.substring(0, 4), psCode.substring(4, 6), psCode.substring(6, 8));
        }
        title = App.getTitle();
        setPixels();
    }

    public void VrcLabel(PollingStation p) {
        String psCode = String.valueOf(p.getPsCode());
        qr = String.format(Locale.ENGLISH, "%s", psCode.substring(0, 4));
        title = App.getTitle();
        setPixels();
    }

    private void setPixels() {
        int dpi = App.getPrinterDpi();
        mWidth = LabelUtil.calculatePixels(45, dpi);
        mHeight = LabelUtil.calculatePixels(15, dpi);
        mHorizontalMargin = LabelUtil.calculatePixels(3, dpi);// 1 -> 3
        mVerticalMargin = LabelUtil.calculatePixels(1, dpi);
        mQrHeight = mQrWidth = LabelUtil.calculatePixels(9, dpi); //11 -> 9
        mTextSize = LabelUtil.calculateTextSize(10, dpi);
        Log.v(TAG, "setPixels: mTextSize = " + mTextSize);
    }

    private int getTextSize(String text, int maxTextSize, int width) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        Rect rect;
        int step = 0;
        while (maxTextSize > step) {
            paint.setTextSize(maxTextSize - step);
            rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            if (rect.width() > width) {
                step++;
            } else {
                break;
            }
        }

        return maxTextSize - step;
    }

    public Bitmap draw() {
        Bitmap output = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        if (App.isOutlineEnabled()) {
            int strokeWidth = 4;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            float left = strokeWidth;
            float top = strokeWidth;
            float right = mWidth - strokeWidth;
            float bottom = mHeight - strokeWidth;
            canvas.drawRect(left, top, right, bottom, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        // QR 코드
        Bitmap qrCodeBitmap = QrUtil.getQrCodeImage(qr, mQrWidth, mQrHeight);
        Log.v(TAG, "draw: QR = " + qrCodeBitmap.getWidth() + "x" + qrCodeBitmap.getHeight());
        int qrVerticalMargin = (mHeight - mQrWidth) / 2;
        Rect qrCodeRect = new Rect(mHorizontalMargin, qrVerticalMargin, mHorizontalMargin + mQrWidth, qrVerticalMargin + mQrHeight);
        canvas.drawBitmap(qrCodeBitmap, null, qrCodeRect, null);

        paint.setTextSize(mTextSize);
        int rowHeight;
        float text_margin_top;
        float x;
        float y;
        int width = mWidth - mQrWidth - (mHorizontalMargin * 3);
        int textSize;
        if (title != null && title.length() > 0) {
            rowHeight = (mHeight - mVerticalMargin * 2) / 2;
            textSize = getTextSize(title, mTextSize, width);
            text_margin_top = (rowHeight - (textSize * 3 / 4)) / 2 + (textSize * 3 / 4);
            x = mHorizontalMargin + mQrWidth + mHorizontalMargin;
            y = mVerticalMargin + rowHeight * 0 + text_margin_top;
            paint.setTextSize(textSize);
            canvas.drawText(title, x, y, paint);

            textSize = getTextSize(qr, mTextSize, width);
            text_margin_top = (rowHeight - (textSize * 3 / 4)) / 2 + (textSize * 3 / 4);
            x = mHorizontalMargin + mQrWidth + mHorizontalMargin;
            y = mVerticalMargin + rowHeight * 1 + text_margin_top;
            paint.setTextSize(textSize);
            canvas.drawText(qr, x, y, paint);
        } else {
//            rowHeight = mHeight - mVerticalMargin * 2;
//            textSize = getTextSize(qr, mTextSize, width);
//            textSize = mTextSize;
//            text_margin_top = (rowHeight - (textSize * 3 / 4)) / 2 + (textSize * 3 / 4);
//            x = mHorizontalMargin + mQrWidth + mHorizontalMargin;
//            y = mVerticalMargin + rowHeight * 0 + text_margin_top;
//            paint.setTextSize(textSize);
//            canvas.drawText(qr, x, y, paint);
            Rect rect = new Rect();
            paint.getTextBounds(qr, 0, qr.length(), rect);

            Bitmap textBitmap = LabelUtil.drawText(qr, mTextSize);
            int textHeight = textBitmap.getHeight();
            int left = mHorizontalMargin + mQrWidth + mHorizontalMargin;
            int top = (mHeight - textHeight) / 2;
            int right = mWidth - mHorizontalMargin;
            int bottom = top + textHeight;

            Rect textRect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(textBitmap, null, textRect, null);

            byte[] bmp = BmpUtil.toBmp(textBitmap, 1);
            new Thread(() -> {
                try {
                    Files.write(bmp, new File("/sdcard/text.bmp"));
                    Log.v(TAG, "printLabel: save image");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Log.v(TAG, "draw: bitmap = " + output.getWidth() + "x" + output.getHeight() + ", " + output.getDensity());
        return output;
//        if (degrees != 0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(degrees);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(output, 0, 0, output.getWidth(), output.getHeight(), matrix, true);
//            return rotatedBitmap;
//        } else {
//            return output;
//        }
    }
}
