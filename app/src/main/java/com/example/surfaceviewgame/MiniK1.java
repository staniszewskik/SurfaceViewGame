package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MiniK1 extends BaseScreen {
    private int width;
    private int height;

    private int COLOR_COUNT=4;
    private final int POINT_VALUE = 200;
    private int cardCount=0;

    String[] colorNames = new String[]{"green","blue","yellow","red"};
    private float r;
    private float normalInitVel = 0.002f;
    private float initVel;
    private float normalAccel = 0.0001f;
    private float accel;
    Card card;

    private final int MAX_VEL_MULTI = 5;
    private final float SCORE_INIT_VEL = 20.0f;


    private Circle[] circles = new Circle[COLOR_COUNT];

    private Paint redPaint = new Paint();
    private Paint grnPaint = new Paint();
    private Paint bluPaint = new Paint();
    private Paint ylwPaint = new Paint();
    private Paint[] colors = new Paint[]{grnPaint,bluPaint,ylwPaint,redPaint};

    int selectedG,selectedB,selectedY,selectedR;

    private float xTime;
    private float yScore;

    private boolean noMoreUpdates = false;

    public MiniK1(ScreenManager sm) {
        super(sm);

        card = new Card(sm.getWidth()/3,sm.getHeight()/3,sm.getWidth()/3,sm.getHeight()/3);
        width = sm.getWidth();
        height = sm.getHeight();
        r = 0.2f * height;

        int widthRange = width - (int)(1.3 * r);
        int widthStart = (int)(1.3*r);
        int heightRange = height - (int)(1.3 * r);
        int heightStart = (int)(1.3*r);

        circles[0] = new Circle(widthStart, heightStart, 0);
        circles[1] = new Circle(widthStart, heightRange, 1);
        circles[2] = new Circle(widthRange, heightStart, 2);
        circles[3] = new Circle(widthRange, heightRange, 3);

        initVel = normalInitVel * height;
        accel = normalAccel * height;

        redPaint.setColor(Color.RED);
        grnPaint.setColor(Color.GREEN);
        bluPaint.setColor(Color.BLUE);
        ylwPaint.setColor(Color.YELLOW);
        for(int i=0;i<COLOR_COUNT;i++){
            colors[i].setTextSize(sm.getHeight()/5);
            colors[i].setTextAlign(Paint.Align.CENTER);
        }

    }

    @Override
    public void drawScreen(Canvas canvas) {
        if(sm.miniFinished)
            return;

        drawBackground(canvas);

        for(int i = 0; i < COLOR_COUNT; i++) {

            Paint circlePaint = null;
            switch(circles[i].kind) {
                case 0:
                    circlePaint = grnPaint;
                    break;
                case 1:
                    circlePaint = bluPaint;
                    break;
                case 2:
                    circlePaint = ylwPaint;
                    break;
                case 3:
                    circlePaint = redPaint;
                    break;
            }

            canvas.drawCircle(circles[i].x, circles[i].y, r, circlePaint);
            }
            card.drawCard(canvas);
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
        if(cardCount>=10)
            sm.miniFinished=true;
        if(noMoreUpdates)
            return;
        sm.timeLeft--;
        sm.totalTime++;
        if(sm.timeLeft <= 0)
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

        cardCount++;
        for(int i = COLOR_COUNT - 1; i >= 0; i--) {
            int cX = circles[i].x;
            int cY = circles[i].y;
            float dist = (pX - cX) * (pX - cX) + (pY - cY) * (pY - cY);

            if(dist > r * r )
                continue;
            else {
                if(card.colorId==circles[i].kind){
                    sm.score+=POINT_VALUE;
                }
                else{
                    sm.score -= (int)(2 * MAX_VEL_MULTI * SCORE_INIT_VEL);
                    //sm.timeLeft-=1000;
                    if(sm.score < 0)
                        sm.score = 0;
                }
                card = new Card(sm.getWidth()/3,sm.getHeight()/3,sm.getWidth()/3,sm.getHeight()/3);
                return true;
            }
        }

        // didn't find a circle to select, tapping the screen randomly gives a score penalty
        sm.score -= (int)(2 * MAX_VEL_MULTI * SCORE_INIT_VEL);
        //sm.timeLeft-=1000;
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

    private class Card {
        public int x;
        public int y;
        public int width;
        public int height;
        public Paint cardPaint;
        public Paint fontPaint;
        public String colorName;
        public int colorId;

        public Card(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            int nr=sm.rand.nextInt(24);
            int c1,c2,c3,c4;
            c1=nr/6;
            c2=(nr/2)%3;
            if(c2>=c1)c2++;
            c3=nr%2;
            c4=1-c3;
            if(c2>c1)
            {
                if(c3>=c1)c3++;
                if(c4>=c1)c4++;
                if(c3>=c2)c3++;
                if(c4>=c2)c4++;
            }
            else
            {
                if(c3>=c2)c3++;
                if(c4>=c2)c4++;
                if(c3>=c1)c3++;
                if(c4>=c1)c4++;
            }
            cardPaint = colors[c1];
            fontPaint = colors[c2];
            colorName = colorNames[c3];
            colorId = c4;
        }
        void drawCard(Canvas canvas){
            canvas.drawRect(x,y,x+width,y+height,cardPaint);
            canvas.drawText(colorName,x+width/2,y+fontPaint.getTextSize(),fontPaint);
        }
    }

}
