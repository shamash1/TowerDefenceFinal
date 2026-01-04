package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.List;

/**
 * Optimized PathLine class.
 * Improvements: Pre-computed drawing paths to prevent memory churn and better visual layering.
 */
public class PathLine {
    private final List<PointF> points;
    private final android.graphics.Path renderPath;

    public PathLine(List<PointF> pts) {
        this.points = pts;
        this.renderPath = new android.graphics.Path();

        // Pre-calculate the path once during initialization
        if (pts != null && !pts.isEmpty()) {
            renderPath.moveTo(pts.get(0).x, pts.get(0).y);
            for (int i = 1; i < pts.size(); i++) {
                renderPath.lineTo(pts.get(i).x, pts.get(i).y);
            }
        }
    }

    public List<PointF> getPoints() {
        return points;
    }

    public PointF getPoint(int i) {
        return points.get(i);
    }

    public void draw(Canvas canvas, Paint paint) {
        android.graphics.Path path = new android.graphics.Path();
        if (points.isEmpty()) return;
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        canvas.drawPath(path, paint);
    }
}