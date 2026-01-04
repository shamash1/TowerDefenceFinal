package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Projectile {
    public float x, y;
    public Enemy target;
    public int damage;
    public int sourceTowerType; // Added this
    public boolean hit = false;

    public Projectile(float x, float y, Enemy target, int damage, int sourceTowerType) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.sourceTowerType = sourceTowerType;
    }

    public void update(float dt) {
        if (hit || target == null || target.isDead()) { hit = true; return; }

        float dx = target.x - x;
        float dy = target.y - y;
        float dist = (float) Math.hypot(dx, dy);
        float step = GameConfig.PROJECTILE_SPEED * dt;

        if (dist < step) {
            target.damage(damage);
            // Apply slow if it's an Ice Tower
            if (sourceTowerType == GameConfig.TOWER_ICE) {
                target.slowTimer = 2.0f; // Slow for 2 seconds
            }
            hit = true;
        } else {
            x += (dx / dist) * step;
            y += (dy / dist) * step;
        }
    }

    public void draw(Canvas c, Paint p) {
        p.setColor(sourceTowerType == GameConfig.TOWER_ICE ? 0xFF00E5FF : 0xFFFFFF00);
        c.drawCircle(x, y, 10f, p);
    }
}