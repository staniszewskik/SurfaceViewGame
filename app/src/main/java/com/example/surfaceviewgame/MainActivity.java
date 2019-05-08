package com.example.surfaceviewgame;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    public BaseScreen curScreen;
    private ScreenManager screenManager;
    private ConstraintLayout mainLayout;

    private final int MINI_COUNT = 2;
    private int bagIndex = MINI_COUNT;
    private int[] bag = new int[MINI_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });

        screenManager = new ScreenManager(this);
        curScreen = new MainMenu(screenManager);
        screenManager.init();
        screenManager.setScreen();
        mainLayout = (ConstraintLayout)findViewById(R.id.main_layout);
        mainLayout.addView(screenManager);

        for(int i = 0; i < MINI_COUNT; i++)
            bag[i] = i + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });

        screenManager.resumeScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();

        screenManager.pauseScreen();
    }

    public void switchScreen() {
        if(screenManager.miniFinished) {
            screenManager.miniFinished = false;
            screenManager.minis++;
            changeToScreen(getRandomMini());
        }
        else if(screenManager.startGame) {
            screenManager.startGame = false;
            screenManager.initGame();
            changeToScreen(getRandomMini());
        }
        else if(screenManager.endGame) {
            screenManager.endGame = false;
            screenManager.miniFinished = false;
            changeToScreen(new EndScreen(screenManager));
        }
        else if(screenManager.viewScores) {
            screenManager.viewScores = false;
            //
        }
        else if(screenManager.viewMenu) {
            screenManager.viewMenu = false;
            changeToScreen(new MainMenu(screenManager));
        }
    }

    private void changeToScreen(BaseScreen screen) {
        screenManager.pauseScreen();
        curScreen = screen;
        screenManager.updateCount = 0;
        screenManager.setScreen();
        screenManager.resumeScreen();
    }

    private BaseScreen getRandomMini() {
        if(bagIndex == MINI_COUNT) {
            for(int i = 0; i < MINI_COUNT; i++) {
                int shuffleIndex = screenManager.rand.nextInt(MINI_COUNT);
                int temp = bag[i];
                bag[i] = bag[shuffleIndex];
                bag[shuffleIndex] = temp;
            }
            bagIndex = 0;
        }

        int nextMini = bag[bagIndex++];
        switch (nextMini) {
            case 1:
                return new Mini1(screenManager);
            case 2:
                return new Mini2(screenManager);
            default:
                return null;
        }
    }
}
