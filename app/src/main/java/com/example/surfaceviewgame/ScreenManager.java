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

    public boolean startGame;
    public boolean endGame;
    public boolean viewScores;
    public boolean viewMenu;
    public boolean miniFinished;

    public int timeLeft;
    public int score;
    public int totalTime;
    public int minis;

    public long lastUpdateTime;
    public final int UPS = 60;
    public final long minUpdateInterval = 1000000000L / UPS;
    public long updateCount;

    public MainActivity ma;
    public Random rand;

    public ScreenManager(Context context) {
        super(context);
        ma = (MainActivity)context;
    }

    public ScreenManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        ma = (MainActivity)context;
    }

    public ScreenManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ma = (MainActivity)context;
    }

    public void init() {
        startGame = false;
        endGame = false;
        viewScores = false;
        viewMenu = false;
        miniFinished = false;

        initGame();

        if(surfaceHolder == null) {
            surfaceHolder = getHolder();
        }

        rand = new Random();
        lastUpdateTime = System.nanoTime();
        updateCount = 0;
    }

    public void initGame() {
        timeLeft = 60 * UPS; // updates
        score = 0;
        totalTime = 0;
        minis = 0;
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
            }
            else if(surfaceHolder.getSurface().isValid()) {
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
