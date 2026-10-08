package com.example.Cortex.components.core;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.core.content.ContextCompat;


/**
 * Draws TWO moving glowing segments along the view's edges.
 * They travel clockwise, always opposite each other (180° apart).
 */
public class EdgeLightningView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int glowColor = Color.parseColor("#00D9FF");
    private float intensity = 1f;

    /** 0..1 — position of the first segment. The second is +0.5 ahead. */
    private float position = 0f;

    private ValueAnimator moveAnimator;

    public EdgeLightningView(Context context) {
        super(context);
        init();
    }

    public EdgeLightningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EdgeLightningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        setWillNotDraw(false);
        startMoving();
    }

    public void setGlowColor(int color) {
        this.glowColor = color;
        invalidate();
    }

    public void setIntensity(float intensity) {
        this.intensity = Math.max(0f, Math.min(1f, intensity));
        invalidate();
    }

    private void startMoving() {
        if (moveAnimator != null) moveAnimator.cancel();

        moveAnimator = ValueAnimator.ofFloat(0f, 1f);
        moveAnimator.setDuration(5000);
        moveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        moveAnimator.setInterpolator(new LinearInterpolator());
        moveAnimator.addUpdateListener(a -> {
            position = (float) a.getAnimatedValue();
            invalidate();
        });
        moveAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (moveAnimator != null) {
            moveAnimator.cancel();
            moveAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Draw two opposite segments
        drawSegment(canvas, position, w, h);
        drawSegment(canvas, (position + 0.5f) % 1f, w, h);

        // Faint baseline so the whole rectangle is subtly visible
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.argb(
            (int) (255 * intensity * 0.15f),
            Color.red(glowColor), Color.green(glowColor), Color.blue(glowColor)));
        canvas.drawRect(0, 0, w, h, paint);
    }

    private void drawSegment(Canvas canvas, float startP, float w, float h) {
        float perimeter = 2 * (w + h);
        float segmentLength = perimeter * 0.25f;   // length of the line
        int steps = 80;

        paint.setStrokeWidth(dp(2));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(glowColor);
        paint.setAlpha((int) (255 * intensity));

        float prevX = -1, prevY = -1;

        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            float p = (startP + t * (segmentLength / perimeter)) % 1f;

            float[] xy = pointOnPerimeter(p, w, h);
            float cx = xy[0];
            float cy = xy[1];

            if (i > 0) {
                canvas.drawLine(prevX, prevY, cx, cy, paint);
            }
            prevX = cx;
            prevY = cy;
        }
    }

    private float[] pointOnPerimeter(float p, float w, float h) {
        float perimeter = 2 * (w + h);
        float d = p * perimeter;

        if (d < w)             return new float[]{ d, 0 };
        d -= w;
        if (d < h)             return new float[]{ w, d };
        d -= h;
        if (d < w)             return new float[]{ w - d, h };
        d -= w;
        return new float[]{ 0, h - d };
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}