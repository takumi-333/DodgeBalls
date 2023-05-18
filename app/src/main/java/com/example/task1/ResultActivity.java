package com.example.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    private Data data;
    private TextView difficultyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        data = (Data) this.getApplication();
        difficultyText =  findViewById(R.id.text_result_difficulty);
        TextView scoreText = findViewById(R.id.text_score);
        TextView highScoreText = findViewById(R.id.text_high_score);

        // 難易度表示
        setDifficultyText(data.getGameDifficulty());

        // SCORE表示
        int score = getIntent().getIntExtra("SCORE", 0);
        scoreText.setText("SCORE: " + score + "M");

        // HIGH SCORE表示
        SharedPreferences sharedPreferences = getSharedPreferences(
                "GAME_DATA", MODE_PRIVATE);
        int highScore = 0;
        if (data.getGameDifficulty() == data.EASY) {
            highScore = sharedPreferences.getInt("HIGH_SCORE_EASY", 0);
        } else if (data.getGameDifficulty() == data.NORMAL) {
            highScore = sharedPreferences.getInt("HIGH_SCORE_NORMAL", 0);
        } else if (data.getGameDifficulty() == data.HARD) {
            highScore = sharedPreferences.getInt("HIGH_SCORE_HARD", 0);
        } else if (data.getGameDifficulty() == data.EXPERT) {
            highScore = sharedPreferences.getInt("HIGH_SCORE_EXPERT", 0);
        }

        if (score > highScore) {
            highScore = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (data.getGameDifficulty() == data.EASY) {
                editor.putInt("HIGH_SCORE_EASY", highScore);
            } else if (data.getGameDifficulty() == data.NORMAL) {
                editor.putInt("HIGH_SCORE_NORMAL", highScore);
            } else if (data.getGameDifficulty() == data.HARD) {
                editor.putInt("HIGH_SCORE_HARD", highScore);
            } else if (data.getGameDifficulty() == data.EXPERT) {
                editor.putInt("HIGH_SCORE_EXPERT", highScore);
            }
            editor.apply();
        }
        highScoreText.setText("HIGH SCORE: " + highScore + "M");

        // スタート画面へ戻る
        Button button = findViewById(R.id.button_result_to_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
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
        } else {
            difficultyText.setText("難易度: エラー");
        }
    }

    @Override
    public void onBackPressed() {}
}