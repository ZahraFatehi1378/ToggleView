package com.example.toggle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Random;

public class ToggleView extends View {

    private Path path;
    private Paint paint;
    private Paint paint2;
    private int skyDayColor = Color.parseColor("#F78F72");
    private int bigWaveDayColor = Color.parseColor("#E16B7B");
    private int smallWaveDayColor = Color.parseColor("#8E2C4F");
    private int flag = 0;
    private final Long TimeToGetNewCircle = 1000L;
    private final Long circleLifeTime = 30000L;
    private Long TimeToGetNewRandomCircle = 2000L;
    private Long MIN_RANDOM_CIRCLE_LIFE_TIME = 7000L;
    private Long MAX_RANDOM_CIRCLE_LIFE_TIME = 10000L;
    private final int maxRandomCircleRadius = 10;
    private Float w = 0f;
    private Float h = 0f;
    private final Float maxCircleRadius = 30f;
    private int spacesBetweenCircles = 50;
    private int flag2 = 0;
    private Shader shader;
    private float coils = 4f;
    private float spiralRadius = 500f;
    private float spiralCenterY = -1000f;
    private float spiralCenterX = -1000f;
    private Random random;
    private float spiralRotation = 50f;
    private float leftCircleX;
    private float leftCircleY;
    private float rightCircleX;
    private float rightCircleY;
    private float circleRadius;
    private Path path2;
    private Path path3;
    private RectF rectF1, rectF2, rectF3, rectF4, rectF;
    private RectF rectFF;

    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (flag == 0) { // just run for first time
            //    startAnimation();
            flag = 1;
            w = (float) getWidth();
            h = (float) getHeight();
            leftCircleX = (w / 2 - w / (8));
            leftCircleY = h / 2;
            rightCircleX = w / 2 + w / (8);
            rightCircleY = h / 2;
            circleRadius = w / 8;
            paint = new Paint();
            path = new Path();
            path2 = new Path();
            paint2 = new Paint();
            path3 = new Path();

            rectF1 = new RectF(leftCircleX - circleRadius, leftCircleY - circleRadius,
                    leftCircleX + circleRadius, leftCircleY + circleRadius);
            rectF2 = new RectF(rightCircleX - circleRadius, rightCircleY - circleRadius,
                    rightCircleX + circleRadius, rightCircleY + circleRadius);
            rectF3 = new RectF(leftCircleX - circleRadius + w / 32, leftCircleY - circleRadius + w / 32,
                    leftCircleX + circleRadius - w / 32, leftCircleY + circleRadius - w / 32);
            rectF4 = new RectF(rightCircleX - circleRadius + w / 32, rightCircleY - circleRadius + w / 32,
                    rightCircleX + circleRadius - w / 32, rightCircleY + circleRadius - w / 32);
            rectF = new RectF(leftCircleX - circleRadius - w / 14, leftCircleY - w / 14
                    , leftCircleX - circleRadius + w / 14, leftCircleY + w / 14);

            rectFF = new RectF(rightCircleX + circleRadius - w / 14, rightCircleY - w / 14
                    , rightCircleX + circleRadius + w / 14, rightCircleY + w / 14);
        }

        drawSky(canvas);
        drawGround(canvas);
        drawSmallGround(canvas);

    }

    private void drawSmallGround(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bigWaveDayColor);
        path2.moveTo((float) (leftCircleX + circleRadius * Math.cos(Math.toRadians(180)))
                , (float) (leftCircleY + circleRadius * Math.sin(Math.toRadians(180))));//cx + r cos(alpha) , cy + r sin(alpha)

        path2.cubicTo((float) (leftCircleX + circleRadius * 2 / 3 * Math.cos(Math.toRadians(90 + 30)))
                , (float) (leftCircleY + circleRadius * 2 / 3 * Math.sin(Math.toRadians(90 + 30)))
                , (float) (leftCircleX + circleRadius * 2 / 3 * Math.cos(Math.toRadians(90 + 30)))
                , (float) (leftCircleY + circleRadius * 2 / 3 * Math.sin(Math.toRadians(90 + 30)))
                , leftCircleX + circleRadius / 2, leftCircleY + circleRadius / 2
        );

        path2.moveTo(leftCircleX + circleRadius / 2, leftCircleY + circleRadius / 2);
        path2.cubicTo(
                (float) (rightCircleX + circleRadius / 2 * Math.cos(Math.toRadians(60)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(60)))
                , (float) (rightCircleX + circleRadius / 2 * Math.cos(Math.toRadians(60)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(60)))
                , (float) (rightCircleX + circleRadius * Math.cos(Math.toRadians(0)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(0))));
        path2.close();

        canvas.drawPath(path2, paint);


        canvas.drawArc(rectF, 0, 20, true, paint);

        canvas.drawArc(rectFF, 160, 20, true, paint);
    }

    private void drawGround(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(skyDayColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        path.moveTo((float) (leftCircleX + circleRadius * Math.cos(Math.toRadians(180 + 30)))
                , (float) (leftCircleY + circleRadius * Math.sin(Math.toRadians(180 + 30))));//cx + r cos(alpha) , cy + r sin(alpha)

        path.cubicTo((float) (leftCircleX + circleRadius / 3 * Math.cos(Math.toRadians(90 + 60)))
                , (float) (leftCircleY + circleRadius / 3 * Math.sin(Math.toRadians(90 + 60)))
                , (float) (leftCircleX + circleRadius / 3 * Math.cos(Math.toRadians(90 + 60)))
                , (float) (leftCircleY + circleRadius / 3 * Math.sin(Math.toRadians(90 + 60)))
                , leftCircleX + circleRadius / 2, leftCircleY
        );

        path.moveTo(leftCircleX + circleRadius / 2, leftCircleY);


        path.cubicTo(
                (float) (rightCircleX + circleRadius / 2 * Math.cos(Math.toRadians(180)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(180)))
                , (float) (rightCircleX + circleRadius / 2 * Math.cos(Math.toRadians(180)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(90 + 45)))
                , (float) (rightCircleX + circleRadius * Math.cos(Math.toRadians(270 + 60)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(270 + 60))));
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawSky(Canvas canvas) {

        paint.setColor(skyDayColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(leftCircleX, leftCircleY - w / (8), rightCircleX, leftCircleY, paint);
        canvas.drawCircle(leftCircleX, h / 2, w / 8, paint);
        canvas.drawCircle(rightCircleX, h / 2, w / 8, paint);

        paint.setColor(bigWaveDayColor);

        canvas.drawRect(leftCircleX, leftCircleY, rightCircleX, leftCircleY + w / (8), paint);
        canvas.drawArc(rectF1
                , 0, 180 + 30, true, paint);
        canvas.drawArc(rectF2
                , 270 + 60, 180 + 30, true, paint);

        paint.setColor(smallWaveDayColor);

        canvas.drawRect(leftCircleX, leftCircleY + w / 16, rightCircleX, leftCircleY + w / (8), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w / 16);
        canvas.drawArc(rectF3
                , 60, 120, false, paint);
        canvas.drawArc(rectF4
                , 0, 120, false, paint);


    }


}


