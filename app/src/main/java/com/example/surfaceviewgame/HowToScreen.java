package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HowToScreen extends BaseScreen {
    private BtnRect checkBtn = new BtnRect(R.drawable.check_button_image);

    private BtnRect prevBtn = new BtnRect(0);
    private BtnRect nextBtn = new BtnRect(0);
    private BtnRect backBtn = new BtnRect(0);
    private Bitmap multiButtonImage;
    private Rect multiButtonImageRect;

    private Bitmap howToImage;
    private Rect howToImageRect;

    public HowToScreen(ScreenManager sm) {
        super(sm);

        int w = sm.getWidth();
        int h = sm.getHeight();
        int offset = (int)(0.05f * h);
        float buttonSide = 0.1f * w;

        checkBtn.lft = (int)(w - buttonSide - offset);
        checkBtn.rgt = (int)(w - offset);
        checkBtn.top = (int)(h - buttonSide - offset);
        checkBtn.btm = (int)(h - offset);

        prevBtn.lft = checkBtn.lft;
        prevBtn.rgt = checkBtn.lft + (int)(buttonSide / 2);
        prevBtn.top = checkBtn.top;
        prevBtn.btm = checkBtn.top + (int)(buttonSide / 2);

        nextBtn.lft = prevBtn.rgt + 1;
        nextBtn.rgt = checkBtn.rgt;
        nextBtn.top = checkBtn.top;
        nextBtn.btm = prevBtn.btm;

        backBtn.lft = checkBtn.lft;
        backBtn.rgt = checkBtn.rgt;
        backBtn.top = prevBtn.btm + 1;
        backBtn.btm = checkBtn.btm;

        multiButtonImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.change_how_to_buttons_image);
        multiButtonImageRect = new Rect(checkBtn.lft, checkBtn.top, checkBtn.rgt, checkBtn.btm);

        switchHowToImage();
        howToImageRect = new Rect(0, 0, w, h);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(0, 0, 80));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        canvas.drawBitmap(howToImage, null, howToImageRect, null);

        if(sm.simpleHowTo)
            checkBtn.draw(canvas);
        else
            canvas.drawBitmap(multiButtonImage, null, multiButtonImageRect, null);
    }

    @Override
    public void updateScreen() {
        if(sm.simpleHowToFinished || sm.viewMenu) {
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

            if(sm.simpleHowTo) {
                if(checkBtn.pointInside(x, y))
                    sm.simpleHowToFinished = true;
            }
            else {
                if(prevBtn.pointInside(x, y)) {
                    sm.viewingHowToFor = (sm.viewingHowToFor + sm.miniCount - 1 - 1) % sm.miniCount + 1;
                    switchHowToImage();
                }
                else if(nextBtn.pointInside(x, y)) {
                    sm.viewingHowToFor = (sm.viewingHowToFor + 1 - 1) % sm.miniCount + 1;
                    switchHowToImage();
                }
                else if(backBtn.pointInside(x, y))
                    sm.viewMenu = true;
            }
        }

        return true;
    }

    private void switchHowToImage() {
        switch (sm.viewingHowToFor) {
            case 1:
                howToImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.test_mini1_how_to);
                break;
            case 2:
                howToImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.test_mini2_how_to);
                break;
            default:
                howToImage = null;
                break;
        }
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
