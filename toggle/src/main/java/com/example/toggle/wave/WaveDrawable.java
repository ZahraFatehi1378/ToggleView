package com.example.toggle.wave;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

public class WaveDrawable {
    protected float maxSpeed = 8.2f;
    protected float minSpeed = 0.8f;

    protected float minRadius = -1f;
    protected float maxRadius = -1f;
    protected boolean autoMin = true;
    protected boolean autoMax = true;
    protected Path path = new Path();

    protected float[] radius;
    protected float[] radiusNext;
    protected float[] progress;
    protected float[] speed;

    final Random random = new Random();
    protected int N;
    public float lineSpeedScale;
    protected float deltaLeft;
    protected float deltaTop;
    protected float deltaRight;
    protected float deltaBottom;

    public WaveDrawable(int n, float lineSpeedScale) {
        super();
        this.lineSpeedScale = lineSpeedScale;
        init(n);
    }

    void init(int n) {
        N = n;
        radius = new float[n + 1];

        radiusNext = new float[n + 1];
        progress = new float[n + 1];
        speed = new float[n + 1];

        for (int i = 0; i <= N; i++) {
            generateBlob(radius, i);
            generateBlob(radiusNext, i);
            progress[i] = 0;
        }
    }

    protected void generateBlob(float[] radius, int i) {
        float radDif = maxRadius - minRadius;
        radius[i] = minRadius + Math.abs(((random.nextInt() % 100f) / 100f)) * radDif;
        speed[i] = (float) (0.017 + 0.003 * (Math.abs(random.nextInt() % 100f) / 100f));
    }

    public void update(float amplitude, float speedScale) {
        final float s = speedScale == -1 ? lineSpeedScale : speedScale;
        for (int i = 0; i <= N; i++) {
            progress[i] += (speed[i] * minSpeed) + amplitude * speed[i] * maxSpeed * s;
            if (progress[i] >= 1f) {
                progress[i] = 0;
                radius[i] = radiusNext[i];
                generateBlob(radiusNext, i);
            }
        }
    }


    public void draw(float left, float top, float right, float bottom, Canvas canvas, Paint paint, float amplitude) {
        realDraw(left + deltaLeft, bottom - top - deltaTop, right + deltaRight, bottom - deltaBottom, canvas, paint);
    }

    private void realDraw(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
        path.reset();

        path.moveTo(right, bottom);
        path.lineTo(left, bottom);

        for (int i = 0; i <= N; i++) {
            if (i == 0) {
                float progress = this.progress[i];
                float r1 = radius[i] * (1f - progress) + radiusNext[i] * progress;
                float y = (top - r1) * (float) 1.0 + (float) 0 * (1f - (float) 1.0);
                path.lineTo(left, y);
            } else {
                float progress = this.progress[i - 1];
                float r1 = radius[i - 1] * (1f - progress) + radiusNext[i - 1] * progress;
                float progressNext = this.progress[i];
                float r2 = radius[i] * (1f - progressNext) + radiusNext[i] * progressNext;
                float x1 = (right - left) / N * (i - 1);
                float x2 = (right - left) / N * i;
                float cx = x1 + (x2 - x1) / 2;

                float y1 = (top - r1) * (float) 1.0 + (float) 0 * (1f - (float) 1.0);
                float y2 = (top - r2) * (float) 1.0 + (float) 0 * (1f - (float) 1.0);
                path.cubicTo(
                        cx, y1,
                        cx, y2,
                        x2, y2
                );
                if (i == N) {
                    path.lineTo(right, bottom);
                }
            }
        }

        canvas.drawPath(path, paint);
    }

    public void generateBlob() {
        for (int i = 0; i < N; i++) {
            generateBlob(radius, i);
            generateBlob(radiusNext, i);
            progress[i] = 0;
        }
    }

    public void updateLine(float amplitude, float maxHeight) {
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
        autoMax = maxRadius < 0;
    }


    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
        autoMin = minRadius < 0;
    }

}