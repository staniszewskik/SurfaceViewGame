package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class TestScreen extends BaseScreen {
    private int xtest = 0;
    private int ytest = 0;
    private int x = 0;
    private int y = 0;
    private int red = 200;

    public TestScreen(ScreenManager sm) {
        super(sm);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint test = new Paint();
        test.setColor(Color.argb(255, 20, 120, 60));
        test.setTextSize(sm.getHeight() * 0.1f);
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), test);

        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.argb(255, 30, 60, 240));
        canvas.drawRect(sm.getWidth() * 0.75f, sm.getHeight() * 0.75f, sm.getWidth(), sm.getHeight(), bluePaint);

        Paint touchPaint = new Paint();
        touchPaint.setColor(Color.argb(255, red, 40, 40));
        canvas.drawRect(0, 0, xtest, ytest, touchPaint);

        canvas.drawText("Test screen", sm.getWidth() * 0.25f, sm.getHeight() * 0.5f, test);
    }

    @Override
    public void updateScreen() {
        if(sm.miniFinished) {
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
        }

        // for some reason those Math functions are really slow

        xtest = x + (int)(sm.updateCount % 33) - 16;//16 * (int) Math.sin(Math.toRadians(sm.updateCount));
        if(xtest < 0)
            xtest = 0;
        ytest = y + (int)(sm.updateCount % 33) - 16;//16 * (int) Math.cos(Math.toRadians(sm.updateCount));
        if(ytest < 0)
            ytest = 0;

        red = 200 + (int)(sm.updateCount % 61) - 30;//60 * (int) Math.sin(Math.toRadians(sm.updateCount));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_UP) {
            x = (int)event.getX();
            y = (int)event.getY();

            if(x > sm.getWidth() * 0.75 && y > sm.getHeight() * 0.75 && event.getAction() == MotionEvent.ACTION_DOWN)
                sm.miniFinished = true;
        }

        return true;
    }
}
