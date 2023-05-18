package com.example.task1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Rectangle extends View {
    private Paint paint;
    private float x1, y1, x2, y2;

    public Rectangle(Context context, float x1, float y1, float x2, float y2) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(200);
        paint.setStyle(Paint.Style.FILL);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(x1, y1, x2, y2, paint);
    }
}
