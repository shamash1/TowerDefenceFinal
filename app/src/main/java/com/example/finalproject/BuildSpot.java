package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class BuildSpot {
    public float x, y;
    public boolean occupied = false;

    // Tracks which tower is built here (using Type-ID system)
    // -1 = Empty, 0 = Archer, 1 = Cannon, etc.
    public int towerTypeId = -1;

    // Dimensions moved to constants for easier global balancing
    private static final float SPOT_SIZE = 64f;
    private static final float CLICK_RADIUS = 52f;
    private static final float CORNER_RADIUS = 12f;

    private final RectF rect;

    public BuildSpot(float x, float y) {
        this.x = x;
        this.y = y;
        // Pre-calculate RectF to avoid object allocation in the draw() loop
        this.rect = new RectF(x - SPOT_SIZE/2, y - SPOT_SIZE/2, x + SPOT_SIZE/2, y + SPOT_SIZE/2);
    }


    public boolean contains(float tx, float ty) {
        float dx = tx - x;
        float dy = ty - y;
        return (dx * dx + dy * dy) < (CLICK_RADIUS * CLICK_RADIUS);
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!occupied) {
            // Draw a dark base platform
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0x44000000);
            canvas.drawCircle(x, y, 45, paint);

            // Draw the glowing selection circle
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5f);
            paint.setColor(Color.CYAN);
            canvas.drawCircle(x, y, 45, paint);

            // Corner brackets for a "Tactical" look
            paint.setStrokeWidth(8f);
            canvas.drawLine(x-30, y-30, x-15, y-30, paint); // Top Left
            canvas.drawLine(x-30, y-30, x-30, y-15, paint);

            canvas.drawLine(x+30, y+30, x+15, y+30, paint); // Bottom Right
            canvas.drawLine(x+30, y+30, x+30, y+15, paint);
        }
    }
}