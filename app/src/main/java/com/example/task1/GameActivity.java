package com.example.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    //ゲーム設定管理class
    private Data data;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private TextView startText;
    private TextView scoreText;
    private Ball playerBall;
    private ArrayList<Ball>  enemyBalls = new ArrayList<>();
    private boolean start_flg = false;
    private Random rnd = new Random();

    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private Handler handler = new Handler();
    private SensorManager sensorManager;
    int screenWidth, screenHeight;

    //　重力加速度
    private float gx, gy;
    private float speedY = 0;
    private float minSpeedY;
    private float maxSpeedY;

    private int timerRate = 1000; //障害物の出現頻度
    private int totDistance = 0; //総移動距離
    private int score = 0;// 総移動距離 / 100
    private int levelUpScore = 100; //levelUpScore感覚で障害物増加


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(Color.WHITE);
        setContentView(frameLayout);

        sensorManager = (SensorManager) getSystemService (SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);


        data = (Data) this.getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //スクリーンサイズの取得
        WindowManager wm = (WindowManager)  getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;

        //障害物の落下速度の最小値、最大値、初期値設定
        if (data.getGameDifficulty() == data.EASY) {
            speedY = screenHeight * 0.008f;
            minSpeedY = screenHeight * 0.008f;
            maxSpeedY = screenHeight * 0.01f;
            timerRate = 1300;
            levelUpScore = 500;
        } else if (data.getGameDifficulty() == data.NORMAL) {
            speedY = screenHeight * 0.01f;
            minSpeedY = screenHeight * 0.01f;
            maxSpeedY = screenHeight * 0.013f;
            timerRate = 1000;
            levelUpScore = 400;
        } else if (data.getGameDifficulty()  == data.HARD) {
            speedY = screenHeight * 0.01f;
            minSpeedY = screenHeight * 0.01f;
            maxSpeedY = screenHeight * 0.015f;
            timerRate = 800;
            levelUpScore = 300;
        } else if (data.getGameDifficulty()  == data.EXPERT) {
            speedY = screenHeight * 0.013f;
            minSpeedY = screenHeight * 0.013f;
            maxSpeedY = screenHeight * 0.018f;
            timerRate = 500;
            levelUpScore = 200;
        }

        // CameraMode = ONの場合、背景をカメラに
        if (data.getCameraMode() == data.ON) {
            CameraPreview cameraPreview = new CameraPreview(this);
            frameLayout.addView(cameraPreview);
        }

        // "tap to start" Textの表示
        linearLayout = new LinearLayout(this);
        startText = new TextView(this);
        startText.setTextColor(Color.BLACK);
        startText.setText("Tap to start");
        linearLayout.addView(startText, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        startText.setGravity(Gravity.CENTER);

        // Score表示
        Rectangle rect = new Rectangle(this, 0, 0,
                screenWidth * 0.05f, screenHeight * 0.06f);
        scoreText = new TextView(this);
        scoreText.setTextColor(Color.BLACK);
        scoreText.setText(score + "M");
        scoreText.setX(screenWidth * 0.01f);
        scoreText.setY(screenHeight * 0.01f);



        //プレイヤーボールの生成
        playerBall = new Ball(this, screenWidth*0.01f, data.getBallColor());
        playerBall.x = screenWidth / 2 - playerBall.radius;
        playerBall.y = (3 * screenHeight) / 4;

        frameLayout.addView(playerBall);
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        frameLayout.addView(rect);
        frameLayout.addView(scoreText);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //横向きのため逆にしている
        gx = event.values[1];
        gy = event.values[0];
    }


    //当たり判定関数
    private boolean isHit(Ball ball) {
        double dx = ball.x - playerBall.x;
        double dy = ball.y - playerBall.y;
        float d = (float)Math.sqrt(dx * dx + dy * dy);
        if (d < ball.radius + playerBall.radius) {
            return true;
        } else {
            return false;
        }
    }
    private void changePosition() {
        speedY += (float)(gy * 20 / 1000);

        //全体のy軸speed、総移動距離、スコアを計算
        if (speedY <= minSpeedY) speedY = minSpeedY;
        if (speedY >= maxSpeedY) speedY = maxSpeedY;
        totDistance += speedY;
        score = totDistance / 100;

        //各ボールに関する処理
        for (int i=0; i < enemyBalls.size(); i++) {
            Ball ball = enemyBalls.get(i);
            ball.y += ball.vy;


            //hit時
            if (isHit(ball)) {
                if (ball.isEnemy) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        // soundplayer
                    }
                    if (timer2 != null) {
                        timer2.cancel();
                        ;
                        timer2 = null;
                    }

                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("SCORE", score);
                    startActivity(intent);
                } else {
                    totDistance += ball.hasScore * 100;
                    enemyBalls.remove(ball);
                    frameLayout.removeView(ball);
                    ball = null;
                    break;
                }


            }
            // 一番下に到達した障害物の削除
            if (ball.y >= screenHeight) {
                enemyBalls.remove(ball);
                frameLayout.removeView(ball);
                ball = null;
                break;
            }

            ball.invalidate();
        }

        //player's ballの挙動
        playerBall.vx += (float) (gx * 0.03f);
        playerBall.x += playerBall.vx * screenWidth / 300.0f;
        if (playerBall.x > screenWidth - playerBall.radius) {
            playerBall.x = screenWidth - playerBall.radius;
            playerBall.vx = -playerBall.vx / 10;
        }
        if (playerBall.x < playerBall.radius) {
            playerBall.x = playerBall.radius;
            playerBall.vx = -playerBall.vx / 10;
        }
        playerBall.invalidate();

        scoreText.setText(score + "M");
    }


    //敵の生成関数
    private void generateEnemy() {
        /*
        *どの難易度でも生成する障害物
        *スピードは生成時のspeedYから乱数で設定 50%~150%
        *稀にスコアアップの障害物の発生
        *2種類あり、速度、大きさ、スコアがそれぞれ異なる
         */
        int r = rnd.nextInt(100);
        Ball ball;
        if (r < 85) {
            ball = new Ball(this, (float) (screenWidth * 0.015), Color.BLACK);
            ball.y = -100;
            ball.vy = speedY * 0.5f + speedY * rnd.nextFloat();
            ball.x = rnd.nextInt((int) (screenWidth - 2 * ball.radius)) + ball.radius;
            enemyBalls.add(ball);
            frameLayout.addView(ball);
        } else if (r < 95) {
            ball = new Ball(this, (float) (screenWidth * 0.015), -256);
            ball.y = -300;
            ball.vy = speedY * 0.7f;
            ball.isEnemy = false;
            ball.hasScore = 10;
            ball.x = rnd.nextInt((int) (screenWidth - 2 * ball.radius)) + ball.radius;
            enemyBalls.add(ball);
            frameLayout.addView(ball);
        } else {
            ball = new Ball(this, (float) (screenWidth * 0.01), -10496);
            ball.y = -500;
            ball.vy = speedY * 0.8f;
            ball.isEnemy = false;
            ball.hasScore = 50;
            ball.x = rnd.nextInt((int) (screenWidth - 2 * ball.radius)) + ball.radius;
            enemyBalls.add(ball);
            frameLayout.addView(ball);
        }

        //難易度NORMAL or HARD or EXPERTのとき、生成する障害物
        if (data.getGameDifficulty() == data.NORMAL || data.getGameDifficulty() == data.HARD
        || data.getGameDifficulty() == data.EXPERT) {
            ball = new Ball(this, (float)(screenWidth*0.015), Color.BLACK);
            ball.y = -20;
            ball.vy = speedY * 0.5f + speedY * rnd.nextFloat();
            ball.x = rnd.nextInt((int)(screenWidth - 2 * ball.radius)) + ball.radius;
            enemyBalls.add(ball);
            frameLayout.addView(ball);

            //EXPERT限定巨大障害物の生成
            if (data.getGameDifficulty() == data.EXPERT) {
                ball = new Ball(this, (float)(screenWidth*0.025), Color.BLACK);
                ball.y = -1000;
                ball.vy = speedY * 0.3f + speedY * rnd.nextFloat() * 0.5f;
                ball.x = (int)(rnd.nextInt((int)(screenWidth * 0.7 - 2 * ball.radius)) + ball.radius
                + screenWidth * 0.15);
                enemyBalls.add(ball);
                frameLayout.addView(ball);
            }
        }

        //スコアの上昇とともに生成する障害物
        for (int i=0; i<score/levelUpScore; i++) {
            ball = new Ball(this, (float)(screenWidth*0.015), Color.BLACK);
            ball.y = -200;
            ball.vy = speedY * 0.5f + speedY * rnd.nextFloat();
            ball.x = rnd.nextInt((int)(screenWidth - 2 * ball.radius)) + ball.radius;
            enemyBalls.add(ball);
            frameLayout.addView(ball);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!start_flg) {
            start_flg = true;
            linearLayout.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePosition();
                        }
                    });
                }
            }, 0, 20);

            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            generateEnemy();
                        }
                    });
                }
            }, 0, timerRate);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        start_flg = false;
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}