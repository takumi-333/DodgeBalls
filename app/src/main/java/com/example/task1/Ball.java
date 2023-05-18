package com.example.task1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Ball extends View {
    float radius;
    float vx, vy, x, y;
    private Paint paint;
    private int color;

    //障害物か否か、獲得スコア
    public boolean isEnemy = true;
    public int hasScore = 0;

    public Ball(Context context, float size, int color)  {
        super(context);
        radius =  size;
        this.color = color;
        x = y = vx = vy = 0;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, radius, paint);
    }


}
