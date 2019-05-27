package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;
import java.util.UUID;

public class EndScreen extends BaseScreen {
    private Paint grnPaint = new Paint();
    private Paint grnBigPaint = new Paint();
    private Paint ylwPaint = new Paint();
    private Paint ylwBigPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint redBigPaint = new Paint();

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
    private Rect textBounds = new Rect();

    private Bitmap endScoreImage;
    private Rect endScoreImageRect;
    private Bitmap endTotalImage;
    private Rect endTotalImageRect;
    private Bitmap endMinisImage;
    private Rect endMinisImageRect;

    private boolean savingOrSaved = false;

    public EndScreen(ScreenManager sm) {
        super(sm);

        grnPaint.setColor(Color.GREEN);
        grnPaint.setTextSize(sm.getHeight() * 0.1f);
        grnBigPaint.setColor(Color.GREEN);
        grnBigPaint.setTextSize(sm.getHeight() * 0.14f);
        ylwPaint.setColor(Color.YELLOW);
        ylwPaint.setTextSize(sm.getHeight() * 0.1f);
        ylwBigPaint.setColor(Color.YELLOW);
        ylwBigPaint.setTextSize(sm.getHeight() * 0.14f);
        redPaint.setColor(Color.RED);
        redPaint.setTextSize(sm.getHeight() * 0.1f);
        redBigPaint.setColor(Color.RED);
        redBigPaint.setTextSize(sm.getHeight() * 0.14f);

        tag1UpBtn = new BtnRect(R.drawable.change_letter_grn_up);
        float buttonAspectRatio = ((float)tag1UpBtn.bmp.getHeight()) / tag1UpBtn.bmp.getWidth();
        tag1UpBtn.lft = (int)(0.1f * sm.getWidth());
        tag1UpBtn.rgt = (int)(0.2f * sm.getWidth());
        float buttonHeight = buttonAspectRatio * (tag1UpBtn.rgt - tag1UpBtn.lft);
        tag1UpBtn.btm = (int)(0.6f * sm.getHeight());
        tag1UpBtn.top = (int)(tag1UpBtn.btm - buttonHeight);
        tag2UpBtn = new BtnRect(tag1UpBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_ylw_up);
        tag3UpBtn = new BtnRect(tag2UpBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_red_up);
        tag1DownBtn = new BtnRect(tag1UpBtn, 0, (int)(0.12f * sm.getHeight() + buttonHeight), R.drawable.change_letter_grn_down);
        tag2DownBtn = new BtnRect(tag1DownBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_ylw_down);
        tag3DownBtn = new BtnRect(tag2DownBtn, (int)(0.11 * sm.getWidth()), 0, R.drawable.change_letter_red_down);

        int w = sm.getWidth();
        int h = sm.getHeight();
        int offset = (int)(0.05f * h);
        float buttonSide = 0.1f * w;

        okayBtn.lft = (int)(w - buttonSide - offset);
        okayBtn.rgt = (int)(w - offset);
        okayBtn.top = (int)(h - buttonSide - offset);
        okayBtn.btm = (int)(h - offset);

        endScoreImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.end_score_image);
        float labelAspectRatio = ((float)endScoreImage.getHeight()) / endScoreImage.getWidth();
        endScoreImageRect = new Rect((int)(0.1f * w), (int)(0.2f * h), (int)(0.3f * w), (int)(0.2f * h + 0.2f * w * labelAspectRatio));

        endTotalImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.end_total_image);
        endTotalImageRect = new Rect((int)(endScoreImageRect.left + 0.5f * w), endScoreImageRect.top, (int)(endScoreImageRect.right + 0.5f * w), endScoreImageRect.bottom);

        endMinisImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.end_minis_image);
        endMinisImageRect = new Rect(endTotalImageRect.left, (int)(endTotalImageRect.top + 0.3f * h), endTotalImageRect.right, (int)(endTotalImageRect.bottom + 0.3f * h));
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

        float tagOffsetX = (tag1UpBtn.rgt - tag1UpBtn.lft) / 2f;
        float tagY = (tag1UpBtn.btm + tag1DownBtn.top) / 2f;
        grnPaint.getTextBounds(Character.toString(tag1), 0, 1, textBounds);
        canvas.drawText(Character.toString(tag1), tag1UpBtn.lft + tagOffsetX - textBounds.exactCenterX(), tagY - textBounds.exactCenterY(), grnPaint);
        grnPaint.getTextBounds(Character.toString(tag2), 0, 1, textBounds);
        canvas.drawText(Character.toString(tag2), tag2UpBtn.lft + tagOffsetX - textBounds.exactCenterX(), tagY - textBounds.exactCenterY(), ylwPaint);
        grnPaint.getTextBounds(Character.toString(tag3), 0, 1, textBounds);
        canvas.drawText(Character.toString(tag3), tag3UpBtn.lft + tagOffsetX - textBounds.exactCenterX(), tagY - textBounds.exactCenterY(), redPaint);

        int totalSeconds = sm.totalTime / sm.UPS;
        grnBigPaint.getTextBounds("0123456789", 0, 9, textBounds);
        float numberOffsetY = (endScoreImageRect.bottom - endScoreImageRect.top) / 2f;

        canvas.drawBitmap(endScoreImage, null, endScoreImageRect, null);
        canvas.drawText(Integer.toString(sm.score), endScoreImageRect.right, endScoreImageRect.top + numberOffsetY - textBounds.exactCenterY(), grnBigPaint);
        canvas.drawBitmap(endTotalImage, null, endTotalImageRect, null);
        canvas.drawText(Integer.toString(totalSeconds), endTotalImageRect.right, endTotalImageRect.top + numberOffsetY - textBounds.exactCenterY(), ylwBigPaint);
        canvas.drawBitmap(endMinisImage, null, endMinisImageRect, null);
        canvas.drawText(Integer.toString(sm.minis), endMinisImageRect.right, endMinisImageRect.top + numberOffsetY - textBounds.exactCenterY(), redBigPaint);
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

                DatabaseEntry entry = new DatabaseEntry();
                entry.setUuid(sm.ma.playerUUID.toString());
                entry.setTag(new String(new char[]{tag1, tag2, tag3}));
                entry.setScore((long)sm.score);
                entry.setDate(sm.ma.dateFormat.format(new Date()));
                entry.setMinis((long)sm.minis);
                entry.setTotal((long)(sm.totalTime / sm.UPS));

                sm.ma.rootRef.child("scores").push().setValue(entry);
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
