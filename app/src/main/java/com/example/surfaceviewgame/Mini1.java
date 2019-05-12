package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Mini1 extends BaseScreen {
    private int w;
    private int h;

    private final int CIRCLE_COUNT = 20;
    private Circle[] circles = new Circle[CIRCLE_COUNT];
    private float r;
    private float selR;

    private int selectedGB = 0;
    private int collectedGB = 0;
    private int chainGB = 0;
    private int[] selsGB = new int[3];
    private float velGB;
    private int selectedYR = 0;
    private int collectedYR = 0;
    private int chainYR = 0;
    private int[] selsYR = new int[3];
    private float velYR;
    private float lineThickness;

    private float normalInitVel = 0.002f;
    private float initVel;
    private float normalAccel = 0.0001f;
    private float accel;

    private final float SCORE_INIT_VEL = 20.0f;
    private float scoreVelGB;
    private float scoreVelYR;
    private final float SCORE_ACCEL = 0.01f;

    private final int MAX_VEL_MULTI = 5;

    private Paint redPaint = new Paint();
    private Paint grnPaint = new Paint();
    private Paint bluPaint = new Paint();
    private Paint ylwPaint = new Paint();

    private Paint selGrn = new Paint();
    private Paint selBlu = new Paint();
    private Paint selYlw = new Paint();
    private Paint selRed = new Paint();

    private float xTime;
    private float yScore;

    // switching screens might take long enough that another update creates another Runnable,
    // which leads to skipping minis and/or howtos and wrong scores
    private boolean noMoreUpdates = false;

    public Mini1(ScreenManager sm) {
        super(sm);

        w = sm.getWidth();
        h = sm.getHeight();
        r = 0.1f * h;
        selR = 0.8f * r;
        lineThickness = r / 6;

        int widthRange = w - (int)(2 * r);
        int widthStart = (w - widthRange) / 2;
        int heightRange = h - (int)(2 * r);
        int heightStart = (h - heightRange) / 2;

        for(int i = 0; i < CIRCLE_COUNT; i++)
            circles[i] = new Circle(widthStart + sm.rand.nextInt(widthRange), heightStart + sm.rand.nextInt(heightRange), i % 4);

        initVel = normalInitVel * h;
        accel = normalAccel * h;

        redPaint.setColor(Color.RED);
        grnPaint.setColor(Color.GREEN);
        bluPaint.setColor(Color.BLUE);
        ylwPaint.setColor(Color.YELLOW);

        selGrn.setColor(Color.rgb(0, 215, 0));
        selGrn.setStrokeWidth(lineThickness);
        selGrn.setStrokeCap(Paint.Cap.ROUND);
        selBlu.setColor(Color.rgb(0, 0, 215));
        selBlu.setStrokeWidth(lineThickness);
        selBlu.setStrokeCap(Paint.Cap.ROUND);
        selYlw.setColor(Color.rgb(215, 215, 0));
        selYlw.setStrokeWidth(lineThickness);
        selYlw.setStrokeCap(Paint.Cap.ROUND);
        selRed.setColor(Color.rgb(215, 0, 0));
        selRed.setStrokeWidth(lineThickness);
        selRed.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void drawScreen(Canvas canvas) {
        if(sm.miniFinished)
            return;

        drawBackground(canvas);

        for(int i = 0; i < CIRCLE_COUNT; i++) {
            if(circles[i].collected)
                continue;

            Paint circlePaint = null;
            Paint selPaint = null;
            switch(circles[i].kind) {
                case 0:
                    circlePaint = grnPaint;
                    selPaint = selGrn;
                    break;
                case 1:
                    circlePaint = bluPaint;
                    selPaint = selBlu;
                    break;
                case 2:
                    circlePaint = ylwPaint;
                    selPaint = selYlw;
                    break;
                case 3:
                    circlePaint = redPaint;
                    selPaint = selRed;
                    break;
            }

            canvas.drawCircle(circles[i].x, circles[i].y, r, circlePaint);
            if(circles[i].selected) {
                // draw selection circle
                canvas.drawCircle(circles[i].selMovedX, circles[i].selMovedY, selR, selPaint);
                if(circles[i].nextSel != -1) {
                    // draw selection line
                    canvas.drawLine(circles[i].x, circles[i].y,
                            circles[circles[i].nextSel].x, circles[circles[i].nextSel].y,
                            selPaint);
                }
            }
        }
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
        if(noMoreUpdates)
            return;
        sm.timeLeft--;
        sm.totalTime++;
        if(sm.timeLeft == 0)
            sm.endGame = true;
        if(sm.endGame || sm.miniFinished) {
            noMoreUpdates = true;
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
            return;
        }

        xTime = ((float)sm.timeLeft / (60f * sm.UPS)) * sm.getWidth();
        yScore = (1f - (float)(sm.score % 10000) / 10000f) * sm.getHeight();

        if(selectedGB >= 2) {
            float selCirclesDist = calcCirclesDist(circles[selsGB[0]], circles[selsGB[1]]);

            circles[selsGB[0]].selMovedDist += velGB;
            circles[selsGB[0]].selMovedX = circles[selsGB[0]].x +
                    (int)((circles[selsGB[1]].x - circles[selsGB[0]].x) * circles[selsGB[0]].selMovedDist / selCirclesDist);
            circles[selsGB[0]].selMovedY = circles[selsGB[0]].y +
                    (int)((circles[selsGB[1]].y - circles[selsGB[0]].y) * circles[selsGB[0]].selMovedDist / selCirclesDist);
            velGB += accel;
            if(velGB > MAX_VEL_MULTI * initVel)
                velGB = MAX_VEL_MULTI * initVel;

            sm.score += (int)scoreVelGB;
            scoreVelGB += SCORE_ACCEL;
            if(scoreVelGB > MAX_VEL_MULTI * SCORE_INIT_VEL)
                scoreVelGB = MAX_VEL_MULTI * SCORE_INIT_VEL;

            if(circles[selsGB[0]].selMovedDist >= selCirclesDist) {
                collectedGB++;
                chainGB++;
                circles[selsGB[0]].collected = true;
                circles[selsGB[0]].selected = false;
                selsGB[0] = selsGB[1];
                selsGB[1] = selsGB[2];
                selectedGB--;

                if(selectedGB == 1) {
                    if(chainGB >= 5) {
                        sm.timeLeft += 2 * (chainGB - 4) * sm.UPS;
                        if(sm.timeLeft > 60 * sm.UPS)
                            sm.timeLeft = 60 * sm.UPS;
                    }
                    chainGB = 0;
                }
            }
        }

        if(selectedYR >= 2) {
            float selCirclesDist = calcCirclesDist(circles[selsYR[0]], circles[selsYR[1]]);

            circles[selsYR[0]].selMovedDist += velYR;
            circles[selsYR[0]].selMovedX = circles[selsYR[0]].x +
                    (int)((circles[selsYR[1]].x - circles[selsYR[0]].x) * circles[selsYR[0]].selMovedDist / selCirclesDist);
            circles[selsYR[0]].selMovedY = circles[selsYR[0]].y +
                    (int)((circles[selsYR[1]].y - circles[selsYR[0]].y) * circles[selsYR[0]].selMovedDist / selCirclesDist);
            velYR += accel;
            if(velYR > MAX_VEL_MULTI * initVel)
                velYR = MAX_VEL_MULTI * initVel;

            sm.score += (int)scoreVelYR;
            scoreVelYR += SCORE_ACCEL;
            if(scoreVelYR > MAX_VEL_MULTI * SCORE_INIT_VEL)
                scoreVelYR = MAX_VEL_MULTI * SCORE_INIT_VEL;

            if(circles[selsYR[0]].selMovedDist >= selCirclesDist) {
                collectedYR++;
                chainYR++;
                circles[selsYR[0]].collected = true;
                circles[selsYR[0]].selected = false;
                selsYR[0] = selsYR[1];
                selsYR[1] = selsYR[2];
                selectedYR--;

                if(selectedYR == 1) {
                    if(chainYR >= 5) {
                        sm.timeLeft += 2 * (chainYR - 4) * sm.UPS;
                        if(sm.timeLeft > 60 * sm.UPS)
                            sm.timeLeft = 60 * sm.UPS;
                    }
                    chainYR = 0;
                }
            }
        }

        if(collectedGB == 9 && collectedYR == 9)
            sm.miniFinished = true;
    }

    private float calcCirclesDist(Circle a, Circle b) {
        return (float)Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionIndex = -1;
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            actionIndex = 0;
        else if(event.getAction() == MotionEvent.ACTION_POINTER_DOWN)
            actionIndex = event.getActionIndex();
        else
            return true;

        int pX = (int)event.getX(actionIndex);
        int pY = (int)event.getY(actionIndex);

        for(int i = CIRCLE_COUNT - 1; i >= 0; i--) {
            int cX = circles[i].x;
            int cY = circles[i].y;
            float dist = (pX - cX) * (pX - cX) + (pY - cY) * (pY - cY);

            if(dist > r * r || circles[i].collected || circles[i].selected)
                continue;

            if((circles[i].kind == 0 || circles[i].kind == 1) && selectedGB < 3) {
                if(selectedGB > 0 && circles[selsGB[selectedGB - 1]].kind == circles[i].kind)
                    continue;

                if(selectedGB == 1) {
                    velGB = initVel;
                    scoreVelGB = SCORE_INIT_VEL;
                }

                selsGB[selectedGB] = i;
                circles[i].selected = true;
                if(selectedGB > 0)
                    circles[selsGB[selectedGB - 1]].nextSel = i;
                selectedGB++;

                return true;
            }
            else if((circles[i].kind == 2 || circles[i].kind == 3) && selectedYR < 3) {
                if(selectedYR > 0 && circles[selsYR[selectedYR - 1]].kind == circles[i].kind)
                    continue;

                if(selectedYR == 1) {
                    velYR = initVel;
                    scoreVelYR = SCORE_INIT_VEL;
                }

                selsYR[selectedYR] = i;
                circles[i].selected = true;
                if(selectedYR > 0)
                    circles[selsYR[selectedYR - 1]].nextSel = i;
                selectedYR++;

                return true;
            }
        }

        // didn't find a circle to select, tapping the screen randomly gives a score penalty
        sm.score -= (int)(2 * MAX_VEL_MULTI * SCORE_INIT_VEL);
        if(sm.score < 0)
            sm.score = 0;

        return true;
    }

    private class Circle {
        public int x;
        public int y;
        public int kind;
        public boolean collected;
        public boolean selected;
        public int nextSel;
        public float selMovedDist;
        public int selMovedX;
        public int selMovedY;

        public Circle(int x, int y, int kind) {
            this.x = x;
            this.y = y;
            this.kind = kind;
            this.collected = false;
            this.selected = false;
            this.nextSel = -1;
            this.selMovedDist = 0;
            this.selMovedX = x;
            this.selMovedY = y;
        }
    }
}
