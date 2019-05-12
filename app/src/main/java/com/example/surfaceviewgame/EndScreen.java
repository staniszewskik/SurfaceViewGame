package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class EndScreen extends BaseScreen {
    private Paint grayPaint;

    private BtnRect tag1UpBtn;
    private BtnRect tag1DownBtn;
    private BtnRect tag2UpBtn;
    private BtnRect tag2DownBtn;
    private BtnRect tag3UpBtn;
    private BtnRect tag3DownBtn;
    private BtnRect okayBtn = new BtnRect(R.drawable.check_button_image);

    private char tag1 = 'A';
    private char tag2 = 'A';
    private char tag3 = 'A';

    private boolean savingOrSaved = false;

    public EndScreen(ScreenManager sm) {
        super(sm);

        grayPaint = new Paint();
        grayPaint.setColor(Color.rgb(128, 128, 128));

        tag1UpBtn = new BtnRect(R.drawable.change_letter_up);
        float buttonAspectRatio = ((float)tag1UpBtn.bmp.getHeight()) / tag1UpBtn.bmp.getWidth();
        tag1UpBtn.lft = (int)(0.1f * sm.getWidth());
        tag1UpBtn.rgt = (int)(0.2f * sm.getWidth());
        float buttonHeight = buttonAspectRatio * (tag1UpBtn.rgt - tag1UpBtn.lft);
        tag1UpBtn.btm = (int)(0.6f * sm.getHeight());
        tag1UpBtn.top = (int)(tag1UpBtn.btm - buttonHeight);
        tag2UpBtn = new BtnRect(tag1UpBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_up);
        tag3UpBtn = new BtnRect(tag2UpBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_up);
        tag1DownBtn = new BtnRect(tag1UpBtn, 0, (int)(0.12f * sm.getHeight() + buttonHeight), R.drawable.change_letter_down);
        tag2DownBtn = new BtnRect(tag1DownBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_down);
        tag3DownBtn = new BtnRect(tag2DownBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_down);

        int w = sm.getWidth();
        int h = sm.getHeight();
        int offset = (int)(0.05f * h);
        float buttonSide = 0.1f * w;

        okayBtn.lft = (int)(w - buttonSide - offset);
        okayBtn.rgt = (int)(w - offset);
        okayBtn.top = (int)(h - buttonSide - offset);
        okayBtn.btm = (int)(h - offset);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(0, 0, 80));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        tag1UpBtn.draw(canvas);
        tag1DownBtn.draw(canvas);
        tag2UpBtn.draw(canvas);
        tag2DownBtn.draw(canvas);
        tag3UpBtn.draw(canvas);
        tag3DownBtn.draw(canvas);
        okayBtn.draw(canvas);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(120, 120, 120));
        textPaint.setTextSize(sm.getHeight() * 0.1f);

        float tagOffsetX = 0.3f * (tag1UpBtn.rgt - tag1UpBtn.lft);
        float tagY = 0.095f * sm.getHeight() + tag1UpBtn.btm;
        canvas.drawText(Character.toString(tag1), tag1UpBtn.lft + tagOffsetX, tagY, textPaint);
        canvas.drawText(Character.toString(tag2), tag2UpBtn.lft + tagOffsetX, tagY, textPaint);
        canvas.drawText(Character.toString(tag3), tag3UpBtn.lft + tagOffsetX, tagY, textPaint);

        int totalSeconds = sm.totalTime / sm.UPS;

        canvas.drawText("SCORE: " + sm.score, 0.1f * sm.getWidth(), 0.2f * sm.getHeight(), textPaint);
        canvas.drawText("TIME: " + totalSeconds, 0.6f * sm.getWidth(), 0.2f * sm.getHeight(), textPaint);
        canvas.drawText("MINIS: " + sm.minis, 0.6f * sm.getWidth(), 0.4f * sm.getHeight(), textPaint);
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

            if(tag1UpBtn.pointInside(x, y))
                tag1 = (char)(((tag1 - 'A' + 1) % 26) + 'A');
            else if(tag1DownBtn.pointInside(x, y))
                tag1 = (char)(((tag1 - 'A' + 25) % 26) + 'A');
            else if(tag2UpBtn.pointInside(x, y))
                tag2 = (char)(((tag2 - 'A' + 1) % 26) + 'A');
            else if(tag2DownBtn.pointInside(x, y))
                tag2 = (char)(((tag2 - 'A' + 25) % 26) + 'A');
            else if(tag3UpBtn.pointInside(x, y))
                tag3 = (char)(((tag3 - 'A' + 1) % 26) + 'A');
            else if(tag3DownBtn.pointInside(x, y))
                tag3 = (char)(((tag3 - 'A' + 25) % 26) + 'A');
            else if(!savingOrSaved && okayBtn.pointInside(x, y)){
                sm.viewMenu = true;
                savingOrSaved = true;

                // save to database
            }
        }

        return true;
    }

    private class BtnRect {
        public int lft, rgt, top, btm;

        public Bitmap bmp;

        public BtnRect(int bmpCode) {
            bmp = BitmapFactory.decodeResource(sm.ma.getResources(), bmpCode);
        }

        public BtnRect(BtnRect baseBtn, int xOffset, int yOffset, int bmpCode) {
            lft = baseBtn.lft + xOffset;
            rgt = baseBtn.rgt + xOffset;
            top = baseBtn.top + yOffset;
            btm = baseBtn.btm + yOffset;

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
