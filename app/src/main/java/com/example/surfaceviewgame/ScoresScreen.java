package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class ScoresScreen extends BaseScreen {
    private BtnRect backBtn = new BtnRect(R.drawable.check_button_image);

    private Bitmap testBackImage;
    private Rect testBackImageRect;

    public ScoresScreen(ScreenManager sm) {
        super(sm);

        int w = sm.getWidth();
        int h = sm.getHeight();
        int offset = (int)(0.05f * h);
        float buttonSide = 0.1f * w;

        backBtn.lft = (int)(w - buttonSide - offset);
        backBtn.rgt = (int)(w - offset);
        backBtn.top = (int)(h - buttonSide - offset);
        backBtn.btm = (int)(h - offset);

        testBackImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.test_scores);
        testBackImageRect = new Rect(0, 0, w, h);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(0, 0, 80));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        canvas.drawBitmap(testBackImage, null, testBackImageRect, null);

        backBtn.draw(canvas);
    }

    @Override
    public void updateScreen() {
        if(sm.viewMenu) {
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if(backBtn.pointInside(x, y))
                sm.viewMenu = true;
        }

        return true;
    }

    private class BtnRect {
        public int lft, rgt, top, btm;

        public Bitmap bmp;

        public BtnRect(int bmpCode) {
            bmp = BitmapFactory.decodeResource(sm.ma.getResources(), bmpCode);
        }

        public boolean pointInside(int x, int y) {
            if(x >= lft && x <= rgt && y >= top && y <= btm)
                return true;
            return false;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(bmp, null, new Rect(lft, top, rgt, btm), null);
        }
    }
}
