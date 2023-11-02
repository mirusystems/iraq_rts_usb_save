package com.mirusystems.usbsave.label;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class ProductLabel {
    private static final String TAG = "PsoLabel";

    private static final int WIDTH = 608; // 76 mm * 8 dots/mm = 608
    private static final int HEIGHT = 144; // 18 mm * 8 = 144

    private static final int MARGIN_HORIZONTAL = 24;
    private static final int MARGIN_VERTICAL = 24;
    private static final int ROW_HEIGHT = HEIGHT - MARGIN_VERTICAL * 2;
    private static final int TEXT_SIZE = 96;

    private String productSerialNumber;

    public ProductLabel(String productSerialNumber) {
        this.productSerialNumber = productSerialNumber; // .toUpperCase(Locale.ROOT);
    }

    public Bitmap drawText(String text, int textSize) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        int width = rect.width() + 8;
        int height = rect.height() + 8;

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        float x = 0;
        float y = textSize / 1.4f + 6;
        canvas.drawText(text, x, y, paint);

        return output;
    }

    public Bitmap draw() {
        return draw(180);
    }

    public Bitmap draw(float degrees) {
        Bitmap output = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(TEXT_SIZE);

        // 긴모양의 폰트가 없어서 일반 폰트로 그리고 이미지를 해당 영역에 넣음
        Bitmap textBitmap = drawText(productSerialNumber, TEXT_SIZE);
        Rect textRect = new Rect(MARGIN_HORIZONTAL, MARGIN_VERTICAL, WIDTH - MARGIN_HORIZONTAL, HEIGHT - MARGIN_VERTICAL);
        canvas.drawBitmap(textBitmap, null, textRect, null);

//        byte[] png = BitmapUtil.toPng(textBitmap);
//        try {
//            Files.write(png, new File("/sdcard/text.png"));
//            Log.v(TAG, "draw: save text");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            Bitmap rotatedBitmap = Bitmap.createBitmap(output, 0, 0, output.getWidth(), output.getHeight(), matrix, true);
            return rotatedBitmap;
        } else {
            return output;
        }
    }
}
