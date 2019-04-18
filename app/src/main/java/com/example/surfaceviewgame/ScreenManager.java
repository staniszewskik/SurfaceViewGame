package com.example.surfaceviewgame;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class ScreenManager extends SurfaceView implements Runnable {
    private Thread thread = null;
    private boolean runThread;
    private SurfaceHolder surfaceHolder = null;

    public boolean gameOngoing;
    public boolean startGame;

    public long timeLeft;
    public int score;

    public boolean miniFinished;

    public int totalScore;
    public long totalTime;
    public int totalMinis;

    public long lastUpdateTime;
    public final long UPS = 60L;
    public final long minUpdateInterval = 1000000000L / UPS;
    public long updateCount;

    public MainActivity ma;
    public Random rand;

    public ScreenManager(Context context) {
        super(context);
    }

    public ScreenManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context) {
        gameOngoing = false;
        startGame = false;
        timeLeft = 60000000000L; // nanoseconds
        score = 0;
        miniFinished = false;

        totalScore = 0;
        totalTime = 0;
        totalMinis = 0;

        if(surfaceHolder == null) {
            surfaceHolder = getHolder();
        }

        ma = (MainActivity)context;
        rand = new Random();
        lastUpdateTime = System.nanoTime();
        updateCount = 0;
    }

    public void setScreen() {
        setOnTouchListener(ma.curScreen);
    }

    @Override
    public void run() {
        while (runThread) {
            // updates roughly UPS times per second, redraws after updated
            boolean updated = false;
            long curTime = System.nanoTime();
            if(curTime - lastUpdateTime > minUpdateInterval) {
                ma.curScreen.updateScreen();
                updateCount++;
                lastUpdateTime += minUpdateInterval;
                //Log.i("updateCount", Long.toString(updateCount));
                updated = true;
            }
            if(curTime - lastUpdateTime > minUpdateInterval) {
                ma.curScreen.updateScreen(); // double update if falling behind
                updateCount++;
                lastUpdateTime = curTime;
                Log.i("double update", "double update");
            }

            if(!updated) {
                try {
                    Thread.sleep((minUpdateInterval - (curTime - lastUpdateTime)) / 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    thread.interrupt();
                }
            } else if(surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    ma.curScreen.drawScreen(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void pauseScreen() {
        if(thread != null) {
            runThread = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeScreen() {
        runThread = true;
        thread = new Thread(this);
        thread.start();
    }
}
