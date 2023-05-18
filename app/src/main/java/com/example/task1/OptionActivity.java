package com.example.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener {
    private Button blueButton, redButton, greenButton;
    private Button easyButton, normalButton, hardButton, expertButton;
    private Button cameraOnButton, cameraOffButton;
    private Button backButton;
    private TextView difficultyText, ballColorText, cameraModeText, highScoreText;

    private Data data;
    private int highScores[] =  {0, 0, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        data = (Data) this.getApplication();

        blueButton = findViewById(R.id.button_blue);
        redButton = findViewById(R.id.button_red);
        greenButton = findViewById(R.id.button_green);
        easyButton = findViewById(R.id.button_easy);
        normalButton = findViewById(R.id.button_normal);
        hardButton = findViewById(R.id.button_hard);
        expertButton = findViewById(R.id.button_expert);
        cameraOnButton = findViewById(R.id.button_camera_on);
        cameraOffButton = findViewById(R.id.button_camera_off);
        backButton = findViewById(R.id.button_back_to_start);

        blueButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
        easyButton.setOnClickListener(this);
        normalButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);
        expertButton.setOnClickListener(this);
        cameraOnButton.setOnClickListener(this);
        cameraOffButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        ballColorText = findViewById(R.id.text_ball_color);
        difficultyText = findViewById(R.id.text_difficulty);
        cameraModeText = findViewById(R.id.text_camera_mode);
        setBallColorText(data.getBallColor());
        setDifficultyText(data.getGameDifficulty());
        setCameraModeText(data.getCameraMode());

        highScoreText = findViewById(R.id.text_option_high_score);
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_DATA",
                MODE_PRIVATE);

        highScores[0] = sharedPreferences.getInt("HIGH_SCORE_EASY", 0);
        highScores[1] = sharedPreferences.getInt("HIGH_SCORE_NORMAL", 0);
        highScores[2] = sharedPreferences.getInt("HIGH_SCORE_HARD", 0);
        highScores[3] = sharedPreferences.getInt("HIGH_SCORE_EXPERT", 0);
        int  highScore=0;
        if (data.getGameDifficulty() == data.EASY) {
            highScore = highScores[0];
        } else  if (data.getGameDifficulty() == data.NORMAL) {
            highScore = highScores[1];
        } else if (data.getGameDifficulty() == data.HARD) {
            highScore = highScores[2];
        } else if (data.getGameDifficulty() == data.EXPERT) {
            highScore = highScores[3];
        }
        highScoreText.setText("HIGH SCORE: " + highScore + "M");

        // 全ての難易度でスコア1000以上でEXPERTの解放
        if (highScores[0] < 1000 || highScores[1] < 1000 || highScores[2] < 1000) {
            expertButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int highScore=0;
        if (view.getId() == R.id.button_blue) {
            data.setBallColor(Color.BLUE);
            setBallColorText(data.getBallColor());
        } else if (view.getId() == R.id.button_red) {
            data.setBallColor(Color.RED);
            setBallColorText(data.getBallColor());
        } else if (view.getId() == R.id.button_green) {
            data.setBallColor(Color.GREEN);
            setBallColorText(data.getBallColor());
        } else if (view.getId() == R.id.button_easy) {
            data.setGameDifficulty(data.EASY);
            setDifficultyText(data.getGameDifficulty());
            highScore = highScores[0];
            highScoreText.setText("HIGH SCORE: "+ highScore + "M");
        } else if (view.getId() == R.id.button_normal) {
            data.setGameDifficulty(data.NORMAL);
            setDifficultyText(data.getGameDifficulty());
            highScore = highScores[1];
            highScoreText.setText("HIGH SCORE: "+ highScore + "M");
        } else if (view.getId() == R.id.button_hard) {
            data.setGameDifficulty(data.HARD);
            setDifficultyText(data.getGameDifficulty());
            highScore = highScores[2];
            highScoreText.setText("HIGH SCORE: "+ highScore + "M");
        } else if (view.getId() == R.id.button_expert) {
            data.setGameDifficulty(data.EXPERT);
            setDifficultyText(data.getGameDifficulty());
            highScore = highScores[3];
            highScoreText.setText("HIGH SCORE: "+ highScore + "M");
        } else if (view.getId() == R.id.button_camera_on) {
            data.setCameraMode(data.ON);
            setCameraModeText(data.getCameraMode());
        } else if (view.getId() == R.id.button_camera_off) {
            data.setCameraMode(data.OFF);
            setCameraModeText(data.getCameraMode());
        } else if (view.getId() == R.id.button_back_to_start) {
            finish();
        }
    }

    private void setBallColorText(int color) {
        if (color == Color.RED)  {
            ballColorText.setText("ボールカラー: 赤");
        } else if (color == Color.BLUE)  {
            ballColorText.setText("ボールカラー: 青");
        } else if (color == Color.GREEN)  {
            ballColorText.setText("ボールカラー: 緑");
        } else {
            ballColorText.setText("ボールカラー: エラー");
        }
    }

    private void  setDifficultyText(int difficulty) {
        if (difficulty == data.EASY) {
            difficultyText.setText("難易度: EASY");
        } else if (difficulty == data.NORMAL) {
            difficultyText.setText("難易度: NORMAL");
        } else if (difficulty == data.HARD) {
            difficultyText.setText("難易度: HARD");
        } else if (difficulty == data.EXPERT) {
            difficultyText.setText("難易度: EXPERT");
        }else {
            difficultyText.setText("難易度: エラー");
        }
    }

    private void setCameraModeText(int cameraMode) {
        if (cameraMode == data.OFF) {
            cameraModeText.setText("カメラモード: OFF");
        } else if (cameraMode == data.ON) {
            cameraModeText.setText("カメラモード: ON");
        } else {
            cameraModeText.setText("カメラモード: エラー");
        }
    }

    @Override
    public void onBackPressed() {
    }
}