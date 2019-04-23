package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class MainMenu extends BaseScreen {
    private Paint grayPaint;

    private BtnRect playBtn = new BtnRect();
    private BtnRect scoresBtn = new BtnRect();

    private boolean finishedLayout;

    public MainMenu(ScreenManager sm) {
        super(sm);

        grayPaint = new Paint();
        grayPaint.setColor(Color.rgb(128, 128, 128));

        finishedLayout = false;
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(255, 187, 102));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        playBtn.draw(canvas);
        scoresBtn.draw(canvas);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(60, 60, 60));
        textPaint.setTextSize(sm.getHeight() * 0.1f);

        canvas.drawText("PLAY", playBtn.lft, playBtn.btm, textPaint);
        canvas.drawText("SCORES", scoresBtn.lft, scoresBtn.btm, textPaint);
    }

    @Override
    public void updateScreen() {
        if(sm.startGame || sm.viewScores) {
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
            playBtn.lft = (int)(0.6f * sm.getWidth());
            playBtn.rgt = (int)(0.8f * sm.getWidth());
            scoresBtn.lft = playBtn.lft;
            scoresBtn.rgt = playBtn.rgt;
            playBtn.top = (int)(0.2f * sm.getHeight());
            playBtn.btm = (int)(0.4f * sm.getHeight());
            scoresBtn.top = (int)(0.6f * sm.getHeight());
            scoresBtn.btm = (int)(0.8f * sm.getHeight());
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
        }

        return true;
    }

    private class BtnRect {
        public int lft, rgt, top, btm;

        public BtnRect() {}

        public boolean pointInside(int x, int y) {
            if(x >= lft && x <= rgt && y >= top && y <= btm)
                return true;
            return false;
        }

        public void draw(Canvas canvas) {
            canvas.drawRect(lft, top, rgt, btm, grayPaint);
        }
    }
}
