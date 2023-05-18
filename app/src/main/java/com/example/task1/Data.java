package com.example.task1;

import android.app.Application;
import android.graphics.Color;

public class Data extends Application {

    // 定数の定義
    public static final int EASY=0, NORMAL=1, HARD=2, EXPERT=3;
    public static final int OFF=0, ON=1;

    private int gameDifficulty = EASY;
    private int cameraMode = OFF;
    private int ballColor = Color.RED;

    public void setBallColor(int ballColor) {
        this.ballColor = ballColor;
    }

    public int getBallColor() {
        return ballColor;
    }

    public int getGameDifficulty() {
        return gameDifficulty;
    }

    public void setGameDifficulty(int gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public int getCameraMode() {
        return cameraMode;
    }

    public void setCameraMode(int cameraMode) {
        this.cameraMode = cameraMode;
    }
}
