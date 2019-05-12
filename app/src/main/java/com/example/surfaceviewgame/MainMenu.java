package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class MainMenu extends BaseScreen {
    //private Paint grayPaint;

    private BtnRect playBtn = new BtnRect(R.drawable.play_button_image);
    private BtnRect scoresBtn = new BtnRect(R.drawable.scores_button_image);
    private BtnRect howToBtn = new BtnRect(R.drawable.how_to_button_image);

    private boolean finishedLayout;

    private Bitmap mml;
    private Rect mmlRect;

    public MainMenu(ScreenManager sm) {
        super(sm);

        //grayPaint = new Paint();
        //grayPaint.setColor(Color.rgb(128, 128, 128));

        finishedLayout = false;
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(0, 0, 80));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        playBtn.draw(canvas);
        scoresBtn.draw(canvas);
        howToBtn.draw(canvas);

        //Paint textPaint = new Paint();
        //textPaint.setColor(Color.rgb(60, 60, 60));
        //textPaint.setTextSize(sm.getHeight() * 0.1f);

        //canvas.drawText("PLAY", playBtn.lft, playBtn.btm, textPaint);
        //canvas.drawText("SCORES", scoresBtn.lft, scoresBtn.btm, textPaint);

        canvas.drawBitmap(mml, null, mmlRect, null);
    }

    @Override
    public void updateScreen() {
        if(sm.startGame || sm.viewScores || sm.viewHowTo) {
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
        }

        // first screen loaded on launch, possibly before screen manager is fully set up
        if(!finishedLayout && sm.getWidth() > 0 && sm.getHeight() > 0) {
            finishedLayout = true;

            float buttonAspectRatio = ((float)playBtn.bmp.getHeight()) / playBtn.bmp.getWidth();
            playBtn.lft = (int)(0.65f * sm.getWidth());
            playBtn.rgt = (int)(0.85f * sm.getWidth());
            float buttonHeight = buttonAspectRatio * (playBtn.rgt - playBtn.lft);
            float buttonVertOffset = (sm.getHeight() - 3f * buttonHeight) / 4f;
            playBtn.top = (int)buttonVertOffset;
            playBtn.btm = (int)(playBtn.top + buttonHeight);

            scoresBtn.lft = playBtn.lft;
            scoresBtn.rgt = playBtn.rgt;
            scoresBtn.top = (int)(playBtn.btm + buttonVertOffset);
            scoresBtn.btm = (int)(scoresBtn.top + buttonHeight);

            howToBtn.lft = playBtn.lft;
            howToBtn.rgt = playBtn.rgt;
            howToBtn.top = (int)(scoresBtn.btm + buttonVertOffset);
            howToBtn.btm = (int)(howToBtn.top + buttonHeight);

            mml = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.main_menu_logo);
            int h = sm.getHeight();
            int offset = (int)(0.05f * h);
            mmlRect = new Rect(offset, offset, h - offset, h - offset);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if(playBtn.pointInside(x, y))
                sm.startGame = true;
            else if(scoresBtn.pointInside(x, y))
                sm.viewScores = true;
            else if(howToBtn.pointInside(x, y))
                sm.viewHowTo = true;
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
