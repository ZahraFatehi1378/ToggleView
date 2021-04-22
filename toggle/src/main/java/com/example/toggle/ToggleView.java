package com.example.toggle;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ToggleView extends View {

    private Path path;
    private Paint paint;
    private int skyColor, skyDayColor = Color.parseColor("#F78F72"), skyNightColor = Color.parseColor("#6D58A7");
    private int bigWaveColor, bigWaveDayColor = Color.parseColor("#E16B7B"), bigWaveNightColor = Color.parseColor("#682F7C");
    private int smallWaveDayColor = Color.parseColor("#8E2C4F"), smallWaveNightColor = Color.parseColor("#5C296C");
    private int smallWaveColor, mainCircleColor = Color.parseColor("#FFE1BB");
    private int sunColor = Color.parseColor("#FFCF93");
    private int moonColor = Color.parseColor("#FFFFFF");
    private int flag = 0;
    private final int maxRandomCircleRadius = 3;
    private final Long circleLifeTime = 5000L;
    private Long TimeToGetNewRandomCircle = 500L;
    private Float w = 0f;
    private Float h = 0f;
    private final Float maxCircleRadius = 30f;
    private int spacesBetweenCircles = 50;
    private int flag2 = 0;
    private float coils = 4f;
    private float spiralRadius = 500f;
    private float spiralCenterY = -1000f;
    private float spiralCenterX = -1000f;
    private Random random;
    private float spiralRotation = 50f;
    private float leftCircleX, leftCircleY, rightCircleX, rightCircleY;
    private float circleRadius;
    private float sunRadius = 30;
    private Path path2;
    private Path path3;
    private RectF rectF1, rectF2, rectF3, rectF4, rectF;
    private RectF rectFF;
    private float mainCirclePadding = 20;
    private ArrayList<SunCircle> sunCircles;
    private float sunUpperMargin;
    private boolean isDay = true;
    private ArrayList<Circle> randomCircles;
    private float moonRadius = 40;
    private Long MIN_RANDOM_CIRCLE_LIFE_TIME = 7000L;
    private Long MAX_RANDOM_CIRCLE_LIFE_TIME = 10000L;
    int sign = R.drawable.night;
    private Bitmap bitmap;


    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sunCircles = new ArrayList<>();
        randomCircles = new ArrayList<>();
        random = new Random();
    }

    private Bitmap getBitmap(int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (flag == 0) { // just run for first time
            init();

            if (isDay)
                sign = R.drawable.day;
            else sign = R.drawable.night;

            bitmap = getBitmap(sign);
            flag = 1;
        }

        setColors();

        drawSky(canvas);
        if (!isDay) {

        }
        drawGround(canvas);
        drawSmallGround(canvas);

        if (isDay) {
            drawSun(canvas);
            updateCircles();
        } else {
            drawStars(canvas);
            drawMoon(canvas);
        }
        drawMainCircle(canvas);

    }


    private void init() {
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
        path3 = new Path();
        sunUpperMargin = w / 32;

        addSunCircle();
        //for waves drawing
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

    private void drawStars(Canvas canvas) {
        paint.setColor(moonColor);
        addRandomCircles();
        for (Circle circle : randomCircles) {
            paint.setAlpha(circle.getAlpha());
            canvas.drawCircle(circle.getX(), circle.getY(), circle.getR(), paint);
        }
    }

    private void addRandomCircles() {
        if (randomCircles.size() == 0 || System.currentTimeMillis() - randomCircles.get(randomCircles.size() - 1).getCreatedTime() > TimeToGetNewRandomCircle) {
            randomCircles.add(new Circle(leftCircleX + random.nextInt((int) (2 * circleRadius))
                    , leftCircleY - circleRadius + random.nextInt((int) (circleRadius)), random.nextInt(maxRandomCircleRadius + 2), 255, 0f, System.currentTimeMillis(),
                    MIN_RANDOM_CIRCLE_LIFE_TIME + (long) (random.nextDouble() * (MAX_RANDOM_CIRCLE_LIFE_TIME - MIN_RANDOM_CIRCLE_LIFE_TIME)))
            );
        }

        Iterator<Circle> iterator = randomCircles.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            circle.setAlpha(255 - (int) ((float) (System.currentTimeMillis() - circle.getCreatedTime()) * 255 / circle.getLifeTime()));
            //remove circles
            if (circle.getAlpha() <= 5 || circle.getR() < 1) {
                iterator.remove();
            }

        }
        invalidate();
    }

    private void drawMoon(Canvas canvas) {
        paint.setColor(moonColor);
        canvas.drawCircle(rightCircleX, rightCircleY - w / 20, moonRadius, paint);
        paint.setColor(skyColor);
        canvas.drawCircle(rightCircleX - w / 32, rightCircleY - w / 16, moonRadius, paint);
    }

    private void setColors() {
        if (isDay) {
            skyColor = skyDayColor;
            bigWaveColor = bigWaveDayColor;
            smallWaveColor = smallWaveDayColor;
        } else {
            skyColor = skyNightColor;
            bigWaveColor = bigWaveNightColor;
            smallWaveColor = smallWaveNightColor;
        }
    }

    private void drawMainCircle(Canvas canvas) {
        if (isDay){
            canvas.drawBitmap(bitmap, null, new RectF(rightCircleX - (circleRadius - mainCirclePadding),
                    rightCircleY - (circleRadius - mainCirclePadding),
                    rightCircleX + (circleRadius - mainCirclePadding),
                    rightCircleY + (circleRadius - mainCirclePadding)), null);
        }else {
            canvas.drawBitmap(bitmap, null, new RectF(leftCircleX - (circleRadius - mainCirclePadding),
                    leftCircleY - (circleRadius - mainCirclePadding),
                    leftCircleX + (circleRadius - mainCirclePadding),
                    leftCircleY + (circleRadius - mainCirclePadding)), null);
        }

    }

    private void drawSun(Canvas canvas) {
        paint.setColor(sunColor);
        for (SunCircle sunCircle : sunCircles) {
            paint.setAlpha(sunCircle.getAlpha());
            canvas.drawCircle(sunCircle.getX(), sunCircle.getY(), sunCircle.getR(), paint);
        }
        paint.setAlpha(255);
        canvas.drawCircle(leftCircleX, leftCircleY - sunUpperMargin, sunRadius, paint);

    }

    private void addSunCircle() {
        SunCircle sunCircle = new SunCircle(leftCircleX, leftCircleY - sunUpperMargin, sunRadius, 255, System.currentTimeMillis());
        sunCircles.add(sunCircle);
    }

    private void updateCircles() {
        // for moving
        Iterator<SunCircle> iterator = sunCircles.iterator();
        while (iterator.hasNext()) {
            SunCircle circle = iterator.next();
            circle.setAlpha(255 - (int) ((float) (System.currentTimeMillis() - circle.getCreatedTime()) * 255 / circleLifeTime));
            circle.setR(sunRadius + ((float) (System.currentTimeMillis() - circle.getCreatedTime()) * circleRadius / (2 * circleLifeTime)));
            if (circle.getAlpha() <= 5 || circle.getR() >= circleRadius) {
                iterator.remove();
            }
        }
        if (System.currentTimeMillis() - sunCircles.get(sunCircles.size() - 1).getCreatedTime() > circleLifeTime / 3) {
            addSunCircle();
        }
        invalidate();
    }

    private void drawSmallGround(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bigWaveColor);
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
        paint.setColor(skyColor);
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

        paint.setColor(skyColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(leftCircleX, leftCircleY - w / (8), rightCircleX, leftCircleY, paint);
        canvas.drawCircle(leftCircleX, h / 2, w / 8, paint);
        canvas.drawCircle(rightCircleX, h / 2, w / 8, paint);

        paint.setColor(bigWaveColor);

        canvas.drawRect(leftCircleX, leftCircleY, rightCircleX, leftCircleY + w / (8), paint);
        canvas.drawArc(rectF1
                , 0, 180 + 30, true, paint);
        canvas.drawArc(rectF2
                , 270 + 60, 180 + 30, true, paint);

        paint.setColor(smallWaveColor);

        canvas.drawRect(leftCircleX, leftCircleY + w / 16, rightCircleX, leftCircleY + w / (8), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w / 16);
        canvas.drawArc(rectF3
                , 60, 120, false, paint);
        canvas.drawArc(rectF4
                , 0, 120, false, paint);


    }


}


