package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Mini2 extends BaseScreen {
    private int w;
    private int h;

    private final int KIND_COUNT = 50;
    private final int POINT_VALUE = 50;
    private List<Point> points;
    private float r;

    private int selections = 0;
    private final int SEL_COUNT = 5;
    private SelRect[] sels = new SelRect[SEL_COUNT];
    private SelRect curSel = new SelRect();
    private boolean twoPointers;
    private float edgeThickness;

    private Paint redPaint = new Paint();
    private Paint grnPaint = new Paint();
    private Paint bluPaint = new Paint();
    private Paint ylwPaint = new Paint();

    private float xTime;
    private float yScore;

    public Mini2(ScreenManager sm) {
        super(sm);

        w = sm.getWidth();
        h = sm.getHeight();
        r = (float)w / 100f;
        edgeThickness = r / 4;
        twoPointers = false;

        points = new ArrayList<Point>();
        for(int i = 0; i < KIND_COUNT; i++)
            points.add(new Point(sm.rand.nextInt(w), sm.rand.nextInt(h), 0));
        for(int i = 0; i < KIND_COUNT; i++)
            points.add(new Point(sm.rand.nextInt(w), sm.rand.nextInt(h), 1));
        for(int i = 0; i < 5; i++)
            points.add(new Point(sm.rand.nextInt(w), sm.rand.nextInt(h), 2));

        redPaint.setColor(Color.RED);
        grnPaint.setColor(Color.GREEN);
        bluPaint.setColor(Color.BLUE);
        ylwPaint.setColor(Color.YELLOW);

        grnPaint.setStrokeWidth(edgeThickness);
        grnPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        drawBackground(canvas);

        for (Point p : points) {
            switch(p.kind) {
                case 0:
                    canvas.drawCircle(p.x, p.y, r, bluPaint);
                    break;
                case 1:
                    canvas.drawCircle(p.x, p.y, r, redPaint);
                    break;
                case 2:
                    canvas.drawCircle(p.x, p.y, r, ylwPaint);
                    break;
            }
        }

        if(twoPointers)
            curSel.draw(canvas);

        for(int i = 0; i < selections; i++)
            sels[i].draw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        Paint lgtBlue = new Paint();
        lgtBlue.setColor(Color.rgb(0, 0, 120));
        Paint midBlue = new Paint();
        midBlue.setColor(Color.rgb(0, 0, 80));
        Paint drkBlue = new Paint();
        drkBlue.setColor(Color.rgb(0, 0, 40));

        canvas.drawRect(0, yScore, xTime, sm.getHeight(), lgtBlue);
        canvas.drawRect(0, 0, xTime, yScore, midBlue);
        canvas.drawRect(xTime, yScore, sm.getWidth(), sm.getHeight(), midBlue);
        canvas.drawRect(xTime, 0, sm.getWidth(), yScore, drkBlue);
    }

    @Override
    public void updateScreen() {
        sm.timeLeft--;
        sm.totalTime++;
        if(sm.timeLeft == 0)
            sm.endGame = true;
        if(sm.endGame || sm.miniFinished) {
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
        }

        xTime = ((float)sm.timeLeft / (60f * sm.UPS)) * sm.getWidth();
        yScore = (1f - (float)(sm.score % 10000) / 10000f) * sm.getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getPointerCount() >= 2) {
            twoPointers = true;

            curSel.lft = (int)event.getX(0);
            curSel.rgt = (int)event.getX(1);
            if(curSel.lft > curSel.rgt) {
                int temp = curSel.lft;
                curSel.lft = curSel.rgt;
                curSel.rgt = temp;
            }

            curSel.top = (int)event.getY(0);
            curSel.btm = (int)event.getY(1);
            if(curSel.top > curSel.btm) {
                int temp = curSel.top;
                curSel.top = curSel.btm;
                curSel.btm = temp;
            }
        } else if(twoPointers) {
            twoPointers = false;
            finishSel();

            if(selections == 5) {
                sm.miniFinished = true;
            }
        }

        return true;
    }

    private void finishSel() {
        for(int i = 0; i < points.size(); i++) {
            Point curPoint = points.get(i);
            if(curPoint.x <= curSel.rgt && curPoint.x >= curSel.lft
                && curPoint.y <= curSel.btm && curPoint.y >= curSel.top
                && !curPoint.collected) {
                switch(curPoint.kind) {
                    case 0:
                        sm.score += POINT_VALUE;
                        break;
                    case 1:
                        sm.score -= POINT_VALUE;
                        if(sm.score < 0)
                            sm.score = 0;
                        break;
                    case 2:
                        sm.timeLeft += sm.UPS;
                        if(sm.timeLeft > 60 * sm.UPS)
                            sm.timeLeft = 60 * sm.UPS;
                        break;
                }
                curPoint.collected = true;
            }
        }

        sels[selections] = new SelRect(curSel.lft, curSel.rgt, curSel.top, curSel.btm);
        selections++;
    }

    private class Point {
        public int x;
        public int y;
        public int kind;
        public boolean collected;

        public Point(int x, int y, int kind) {
            this.x = x;
            this.y = y;
            this.kind = kind;
            this.collected = false;
        }
    }

    private class SelRect {
        public int lft, rgt, top, btm;

        public SelRect() {}

        public SelRect(int lft, int rgt, int top, int btm) {
            this.lft = lft;
            this.rgt = rgt;
            this.top = top;
            this.btm = btm;
        }

        public void draw(Canvas canvas) {
            canvas.drawLine(lft, top, rgt, top, grnPaint);
            canvas.drawLine(rgt, top, rgt, btm, grnPaint);
            canvas.drawLine(lft, top, lft, btm, grnPaint);
            canvas.drawLine(lft, btm, rgt, btm, grnPaint);
        }
    }
}
