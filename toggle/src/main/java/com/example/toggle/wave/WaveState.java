package com.example.toggle.wave;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.animation.Interpolator;

import java.util.Random;

public class WaveState {
    public float targetX = -1f,targetY = -1f;
    public float startX , startY , duration;
    public float time;
    public float interpolation;
    public float x , y;
    private final int state;
    public Shader shader;
    public final Matrix matrix = new Matrix();
    protected Random random = new Random();

    public int width = 0;
    public int height = 0;
    public float fixedScale = 1.5f;
    public float scale;
    public int durationBound = 1000;

    public float speedMin = 0.5f;
    public float speedMax = 0.01f;
    public Interpolator interpolator;
    private final Shader wavingShader;

    public WaveState(int state, final Shader wavingShader){
        this.state = state;
        init();
        this.wavingShader = wavingShader;
    }

    public void setSize (float left,float top,float right,float bottom){
        this.width = (int) (bottom - top);
        this.height = (int) (right - left);
    }

    protected void init(){
        interpolator = new CubicBezierInterpolator(0, 0, .58, 1);

    }

    /**
     * create Paint shader
     */
    public  Shader createShader(){
        return wavingShader;
    }


    /**
     * update shader
     */
    public void update(long dt, float amplitude) {
        if (shader==null) shader = createShader();
        if (shader == null) return;

        if (duration == 0 || time >= duration) {
            duration = random.nextInt(durationBound) + 500;
            time = 0;
            if (targetX == -1f) {
                updateTargets();
            }
            startX = targetX;
            startY = targetY;
            updateTargets();
        }

        time += dt * (0.5f + speedMin) + dt * (speedMax * 2) * amplitude;
        if (time > duration) {
            time = duration;
        }

        if (interpolator!=null) {
            interpolation = interpolator.getInterpolation(time / duration);
        } else {
            interpolation = 1f;
        }

        updateScale();
        updateTranslate();
    }

    /**
     * calculate targets
     */
    protected void updateTargets() {
        targetX = 0.8f + 0.2f * (random.nextInt(100) / 100f);
        targetY = random.nextInt(100) / 100f;
    }

    /**
     * calculate scale
     */
    protected void updateScale(){
        scale = width / 400.0f * this.fixedScale;
    }

    /**
     * calculate x,y using targets and start points
     */
    protected void updateTranslate(){
        x = width * (startX + (targetX - startX) * interpolation) - 200;
        y = height * (startY + (targetY - startY) * interpolation) - 200;
    }

    /**
     * apply changes and updated values
     */
    public void loadMatrix(){
        matrix.reset();
        matrix.postTranslate(x, y);
        matrix.postScale(scale, scale, x + 200, y + 200);

        if (shader!=null) shader.setLocalMatrix(matrix);
    }

    /**
     * apply shader into the target paint
     */
    public void setToPaint(Paint paint) {
        paint.setShader(shader);
    }

}