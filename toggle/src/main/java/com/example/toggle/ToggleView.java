package com.example.toggle;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.toggle.wave.WaveDrawable;
import com.example.toggle.wave.WaveState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ToggleView extends View {

    private Path path;
    private Paint paint;
    private int skyColor, skyDayColor = Color.parseColor("#F78F72"), skyNightColor = Color.parseColor("#6D58A7");
    private int bigWaveColor, bigWaveDayColor = Color.parseColor("#E16B7B"), bigWaveNightColor = Color.parseColor("#3d1e6d");
    private int smallWaveDayColor = Color.parseColor("#8E2C4F"), smallWaveNightColor = Color.parseColor("#2e003e");
    private int smallWaveColor;
    private int sunColor = Color.parseColor("#FFCF93");
    private int moonColor = Color.parseColor("#FFFFFF");
    private int flag = 0;
    private final int maxRandomCircleRadius = 3;
    private final Long circleLifeTime = 5000L;
    private Long TimeToGetNewRandomCircle = 500L;
    private Float w = 0f;
    private Float h = 0f;
    private Random random;
    private float leftCircleX, leftCircleY, rightCircleX, rightCircleY;
    private float circleRadius;
    private float sunRadius = 30;
    private Path path2;
    private RectF rectF3, rectF4;
    private float mainCirclePadding = 20;
    private ArrayList<SunCircle> sunCircles;
    private float sunUpperMargin;
    private boolean isDay = true;
    private ArrayList<Circle> randomCircles;
    private float moonRadius = 40;
    private Long MIN_RANDOM_CIRCLE_LIFE_TIME = 7000L, MAX_RANDOM_CIRCLE_LIFE_TIME = 10000L;
    int sign = R.drawable.night;
    private Bitmap bitmap;
    private Matrix rotator;
    private Listener listener;

    //&&&&&&&&&&&
    float amplitude;
    float animateToAmplitude;
    float animateAmplitudeDiff;
    float amplitudeSpeed = 0.33f;
    private boolean stub;
    private long lastStubUpdateAmplitude;
    private WaveState currentState;
    private WaveState previousState;
    private float progressToState = 1f;
    private int maxAlpha = 20;
    private int shaderColor1;
    private int shaderColor2;
    private List<WaveDrawable> waveDrawables = new ArrayList<>();
    private Map<Integer, WaveState> states = new HashMap<>();
    WaveDrawable mainWave;
    boolean mainWaveEnabled = true;
    float mainWaveHeight = -1;
    private Paint paint2 = new Paint();
    private float move = 0;
    private int flag2 = 0;
    private float goUp, goDown;


    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sunCircles = new ArrayList<>();
        randomCircles = new ArrayList<>();
        random = new Random();
        rotator = new Matrix();
        initWave();
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
            move = 2 * circleRadius;
            flag = 1;
            bitmap = getBitmap(sign);
        }
        if (flag2 == 0) {
            if (isDay) {
                sign = R.drawable.day;
                addSunCircle();
            } else {
                sign = R.drawable.night;
            }
            bitmap = getBitmap(sign);
            flag2 = 1;
        }
        setColors();
        drawSky(canvas);
        drawGround(canvas);
        drawSmallGround(canvas);
        if (isDay) {
            makeSunBigger(canvas);
            makeMoonSmaller(canvas);
            updateCircles();
        } else {
            makeSunSmaller(canvas);
            drawStars(canvas);
            makeMoonBigger(canvas);
        }
        drawMainCircle(canvas);
    }

    private void makeMoonBigger(Canvas canvas) {
        paint.setColor(moonColor);
        canvas.drawCircle(rightCircleX, rightCircleY - circleRadius / 2, goUp, paint);
        paint.setAlpha(255);
        paint.setColor(skyColor);
        canvas.drawCircle(rightCircleX - circleRadius / 8, rightCircleY - 2 * circleRadius / 3, moonRadius, paint);
    }

    private void makeMoonSmaller(Canvas canvas) {
        paint.setColor(moonColor);
        canvas.drawCircle(rightCircleX, rightCircleY - circleRadius / 2, goDown, paint);
        paint.setAlpha(255);
        paint.setColor(skyColor);
        canvas.drawCircle(rightCircleX - circleRadius / 8, rightCircleY - 2 * circleRadius / 3, moonRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        } else if ((event.getAction() == MotionEvent.ACTION_MOVE)) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            changeState();
            listener.changeStateClicked(isDay);
            this.invalidate();
            return false;
        } else {
            return false;
        }
    }

    private void changeState() {
        isDay = !isDay;
        startDailyAnimation();
        flag2 = 0;
    }

    private void startDailyAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 2 * circleRadius);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(animation -> {
            move = (float) animation.getAnimatedValue();
        });
        valueAnimator.start();
        invalidate();
        animateSunAndMoon();

    }

    private void animateSunAndMoon() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, sunRadius);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(animation -> {
            goUp = (float) animation.getAnimatedValue();
            goDown = sunRadius - (float) animation.getAnimatedValue();
        });
        valueAnimator.start();
        invalidate();
    }

    private void makeSunBigger(Canvas canvas) {
        paint.setColor(sunColor);
        for (SunCircle sunCircle : sunCircles) {
            paint.setAlpha(sunCircle.getAlpha());
            canvas.drawCircle(sunCircle.getX(), sunCircle.getY(), sunCircle.getR() * goUp / sunRadius, paint);
        }
        paint.setAlpha(255);
        canvas.drawCircle(leftCircleX, leftCircleY - sunUpperMargin, goUp, paint);
    }

    private void makeSunSmaller(Canvas canvas) {
        paint.setColor(sunColor);
        for (SunCircle sunCircle : sunCircles) {
            paint.setAlpha(sunCircle.getAlpha());
            canvas.drawCircle(sunCircle.getX(), sunCircle.getY(), sunCircle.getR() * goDown / sunRadius, paint);
        }
        paint.setAlpha(255);
        canvas.drawCircle(leftCircleX, leftCircleY - sunUpperMargin, goDown, paint);
    }

    private void init() {
        w = (float) getWidth();
        h = (float) getHeight();
        leftCircleX = w / 4;
        leftCircleY = h / 2;
        rightCircleX = 3 * w / 4;
        rightCircleY = h / 2;
        circleRadius = w / 4;
        paint = new Paint();
        path = new Path();
        path2 = new Path();
        sunUpperMargin = w / 9;

        addSunCircle();
        //for waves drawing
        rectF3 = new RectF(leftCircleX - circleRadius, leftCircleY - circleRadius,
                leftCircleX + circleRadius, leftCircleY + circleRadius);
        rectF4 = new RectF(rightCircleX - circleRadius, rightCircleY - circleRadius,
                rightCircleX + circleRadius, rightCircleY + circleRadius);
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
            circle.setAlpha(255 - (int) ((float) (System.currentTimeMillis() - circle.getCreatedTime()) * 255F / circle.getLifeTime()));
            //remove circles
            if (circle.getAlpha() <= 5 || circle.getR() < 1) {
                iterator.remove();
            }

        }
        invalidate();
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
        shaderColor1 = bigWaveColor;
        shaderColor2 = smallWaveColor;
    }

    private void drawMainCircle(Canvas canvas) {
        if (!isDay) {
            canvas.drawBitmap(bitmap, null, new RectF(rightCircleX - (circleRadius - mainCirclePadding) - move,
                    rightCircleY - (circleRadius - mainCirclePadding),
                    rightCircleX + (circleRadius - mainCirclePadding) - move,
                    rightCircleY + (circleRadius - mainCirclePadding)), null);
            rotator.postRotate(move * 90 / (2 * circleRadius), rightCircleX - move, rightCircleY);
        } else {
            canvas.drawBitmap(bitmap, null, new RectF(leftCircleX - (circleRadius - mainCirclePadding) + move,
                    leftCircleY - (circleRadius - mainCirclePadding),
                    leftCircleX + (circleRadius - mainCirclePadding) + move,
                    leftCircleY + (circleRadius - mainCirclePadding)), null);
            rotator.postRotate(move * 90 / (2 * circleRadius), rightCircleX + move, rightCircleY);
        }
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
        if (sunCircles.size() != 0)
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

        path2.cubicTo((float) (leftCircleX + circleRadius * 5 / 7 * Math.cos(Math.toRadians(90 + 30)))
                , (float) (leftCircleY + circleRadius * 4 / 7 * Math.sin(Math.toRadians(90 + 30)))
                , (float) (leftCircleX + circleRadius * 4 / 7 * Math.cos(Math.toRadians(90 + 30)))
                , (float) (leftCircleY + circleRadius * 4 / 7 * Math.sin(Math.toRadians(90 + 30)))
                , leftCircleX + circleRadius / 2, leftCircleY + circleRadius / 3
        );

        path2.moveTo(leftCircleX + circleRadius / 2, leftCircleY + circleRadius / 3);
        path2.cubicTo(
                (float) (rightCircleX + 2 * circleRadius / 5 * Math.cos(Math.toRadians(60)))
                , (float) (rightCircleY + 2 * circleRadius / 5 * Math.sin(Math.toRadians(60)))
                , (float) (rightCircleX + circleRadius * Math.cos(Math.toRadians(60)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(60)))
                , (float) (rightCircleX + circleRadius * Math.cos(Math.toRadians(0)))
                , (float) (rightCircleY + circleRadius * Math.sin(Math.toRadians(0))));
        path2.close();

        canvas.drawPath(path2, paint);
    }

    private void drawGround(Canvas canvas) {
        paint.setColor(bigWaveColor);
        path.moveTo(0, h / 2);
        path.lineTo(w, h / 2);
        path.lineTo(leftCircleX + circleRadius / 2, leftCircleY + circleRadius / 3);
        path.lineTo(0, h / 2);

        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawSky(Canvas canvas) {


        paint.setColor(skyColor);

        canvas.drawRect(leftCircleX, leftCircleY - circleRadius, rightCircleX, leftCircleY, paint);
        canvas.drawCircle(leftCircleX, h / 2, circleRadius, paint);
        canvas.drawCircle(rightCircleX, h / 2, circleRadius, paint);
        paint.setColor(bigWaveColor);
        canvas.drawRect(leftCircleX, leftCircleY, rightCircleX, leftCircleY + circleRadius, paint);
        paint.setColor(smallWaveColor);
        canvas.drawRect(leftCircleX, leftCircleY, rightCircleX, leftCircleY + circleRadius, paint);
        canvas.drawArc(rectF3
                , 0, 180, false, paint);
        canvas.drawArc(rectF4
                , 0, 180, false, paint);

        setAmplitude(1000 * 2);
        drawWaves(leftCircleX - circleRadius, 0, rightCircleX + circleRadius, getHeight() / 2, canvas);

    }

    //waves

    private void initWave() {

        shaderColor2 = Color.parseColor("#ffffff");
        shaderColor1 = Color.parseColor("#ffffff");
        setAmplitude(-1f);
        mainWaveHeight = -1;

        addWaveDrawable(new WaveDrawable(7, 0.7f) {

            @Override
            public void draw(float left, float top, float right, float bottom, Canvas canvas, Paint paint, float amplitude) {
                deltaTop = dp(10) * amplitude;
                super.draw(left, top, right, bottom, canvas, paint, amplitude);
            }

            @Override
            public void updateLine(float amplitude, float maxHeight) {
                super.updateLine(amplitude, maxHeight);
                setMinRadius(0);
                setMaxRadius(Math.max(maxHeight * amplitude, dp(3)));
            }

        });

        addWaveDrawable(new WaveDrawable(8, 0.7f) {

            @Override
            public void updateLine(float amplitude, float maxHeight) {
                super.updateLine(amplitude, maxHeight);
                setMinRadius(0);
                setMaxRadius(Math.max(maxHeight * amplitude, dp(3)));
            }

            @Override
            public void draw(float left, float top, float right, float bottom, Canvas canvas, Paint paint, float amplitude) {
                deltaTop = dp(6) * amplitude;
                super.draw(left, top, right, bottom, canvas, paint, amplitude);
            }
        });

        mainWave = new WaveDrawable(5, 0.3f) {

            @Override
            public void updateLine(float amplitude, float maxHeight) {
                super.updateLine(amplitude, maxHeight);
                setMinRadius(0);
                setMaxRadius(dp(2) + dp(2) * amplitude);
            }
        };
    }

    protected WaveState createDefaultState() {
        return new WaveState(-1, createLinearShader(getWidth(), shaderColor1, shaderColor2, shaderColor1)) {
        };
    }

    public Shader createLinearShader(float size, int color1, int color2, int color3) {
        return new LinearGradient(0, 0, size, 0,
                new int[]{color1, color2, color3},
                new float[]{0, 0.4f, 1f}, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (currentState == null && previousState == null && isEnabled()) {
            setState2(createDefaultState());
        }

        if (mainWaveHeight == -1) mainWaveHeight = h * 0.75f;
    }

    protected void drawWaves(float left, float top, float right, float bottom, Canvas canvas) {
        if (stub) loadStubWaves();

        if (animateToAmplitude != amplitude) {
            amplitude += animateAmplitudeDiff * 16;
            if (animateAmplitudeDiff > 0) {
                if (amplitude > animateToAmplitude) {
                    amplitude = animateToAmplitude;
                }
            } else {
                if (amplitude < animateToAmplitude) {
                    amplitude = animateToAmplitude;
                }
            }
        }

        if (previousState != null) {
            progressToState += 16 / 250f;
            if (progressToState > 1f) {
                progressToState = 1f;
                previousState = null;
            }
        }

        for (int i = 0; i < 2; i++) {
            float alpha;
            if (i == 0 && previousState == null) {
                continue;
            }

            if (i == 0) {
                alpha = 1f - progressToState;
                previousState.setSize(left, top, right, bottom);
                previousState.update(16, amplitude);
                previousState.loadMatrix();
                previousState.setToPaint(paint2);
            } else {
                if (currentState == null) {
                    return;
                }
                alpha = previousState != null ? progressToState : 1f;
                currentState.setSize(left, top, right, bottom);
                currentState.update(16, amplitude);
                currentState.loadMatrix();
                currentState.setToPaint(paint2);
            }

            paint2.setAlpha((int) (maxAlpha * alpha));

            float wavesHeight = bottom - top;
            if (mainWaveEnabled) wavesHeight -= mainWaveHeight;
            wavesHeight = wavesHeight + 400;

            for (int index = 0; index < waveDrawables.size(); index++) {
                WaveDrawable waveDrawable = waveDrawables.get(index);
                waveDrawable.updateLine(amplitude, wavesHeight - dp(6));
                waveDrawable.update(amplitude, waveDrawable.lineSpeedScale);

                waveDrawable.draw(left, top, right, bottom, canvas, paint2, amplitude);
            }

            if (mainWaveEnabled) {
                mainWave.updateLine(amplitude, wavesHeight);
                mainWave.update(amplitude, mainWave.lineSpeedScale);

                if (i == 1) {
                    paint2.setAlpha((int) (255 * alpha));
                } else {
                    paint2.setAlpha(255);
                }
            }
        }

        invalidate();
    }

    protected void loadStubWaves() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStubUpdateAmplitude > 1000) {
            lastStubUpdateAmplitude = currentTime;
            animateToAmplitude = 0.5f + 0.5f * Math.abs(random.nextInt() % 100) / 100f;
            animateAmplitudeDiff = (animateToAmplitude - amplitude) / (100 + 1500.0f * getAmplitudeSpeed());
        }
    }

    public void addWaveDrawable(@NonNull WaveDrawable blobDrawable) {
        if (waveDrawables.contains(blobDrawable)) return;
        waveDrawables.add(blobDrawable);
        blobDrawable.generateBlob();
        invalidate();
    }

    public final static float MAX_AMPLITUDE = 8_500f;

    public void setAmplitude(float value) {
        if (value < 0) {
            stub = true;
            return;
        }
        stub = false;
        animateToAmplitude = Math.min(MAX_AMPLITUDE, value) / MAX_AMPLITUDE;
        animateAmplitudeDiff = (animateToAmplitude - amplitude) / (100 + 500.0f * getAmplitudeSpeed());
    }

    public void setState2(WaveState state) {
        if (currentState != null && currentState == state) {
            return;
        }

        previousState = currentState;
        currentState = state;
        if (previousState != null) {
            progressToState = 0;
        } else {
            progressToState = 1;
        }
        invalidate();
    }

    public float getAmplitudeSpeed() {
        return amplitudeSpeed;
    }

    private int dp(int value) {
        return (int) (getContext().getResources().getDisplayMetrics().density * value);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}


