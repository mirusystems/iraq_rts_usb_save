package com.mirusystems.usbsave.label;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

public class LabelUtil {
    private static final String TAG = "LabelUtil";

    public static final int DPI_203 = 203;
    public static final int DPI_305 = 305;
    public static final double INCH_PER_MM = 0.03937;

    /**
     * 길이를 입력하면 몇 픽셀인지 계산
     *
     * @param mm
     * @param dpi
     * @return
     */
    public static int calculatePixels(double mm, int dpi) {
        if (dpi == DPI_203) {
            return (int) Math.floor(mm * 8);
        } else if (dpi == DPI_305) {
            return (int) Math.floor(mm * 12);
        } else {
            return (int) Math.floor(mm * dpi * INCH_PER_MM);
        }
    }

    /**
     * 실제 출력될 텍스트의 전체 높이를 입력하면 몇 픽셀인지 계산
     *
     * @param mm
     * @param dpi
     * @return
     */
    public static int calculateTextSize(double mm, int dpi) {
        if (dpi == DPI_203) {
            return (int) Math.floor(mm * 8);
        } else if (dpi == DPI_305) {
            return (int) Math.floor(mm * 12);
        } else {
            return (int) Math.floor(mm * dpi * INCH_PER_MM);
        }
    }

    /**
     * 대문자와 숫자만 사용하는 텍스트를 그리고, 가운데 정렬하기 좋게 이미지로 생성
     *
     * @param text
     * @param textSize
     * @return
     */
    public static Bitmap drawText(String text, int textSize) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(textSize);
        // 소문자가 없으면 필요없음
//        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        int ascent = (int) Math.floor(textSize + fontMetrics.ascent) - 2; // 2 pixel 여유
//        Log.d(TAG, "drawText: ascent = " + ascent);

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        Log.v(TAG, "drawText: rect = " + rect + ", left:" + rect.left + ", top:" + rect.top + ", right:" + rect.right + ", bottom:" + rect.bottom + ", " + rect.width() + "x" + rect.height());
        int width = rect.width();
        int height = -rect.top; // - ascent; // 대문자 높이. 소문자가 없으면 필요없음

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        float x = -rect.left; // 텍스트을 2만큼 뒤에 그림
        float y = height; // baseline의 위치 (텍스트를 그릴 때, baseline에서 그림)
//        Log.v(TAG, "drawText: y = " + y);
        canvas.drawText(text, x, y, paint); // y = baseline

        return output;
    }
}
