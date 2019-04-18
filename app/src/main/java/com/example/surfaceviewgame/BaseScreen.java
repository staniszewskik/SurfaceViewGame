package com.example.surfaceviewgame;

import android.graphics.Canvas;
import android.view.View;

public abstract class BaseScreen implements View.OnTouchListener {
    public ScreenManager sm;

    protected BaseScreen(ScreenManager sm) {
        this.sm = sm;
    }

    public abstract void drawScreen(Canvas canvas);
    public abstract void updateScreen();
}
