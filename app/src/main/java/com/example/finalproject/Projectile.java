package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Projectile {
    public float x, y;
    private Enemy target;
    private int damage;
    private int type;
    public boolean hit = false;

    public Projectile(float x, float y, Enemy target, int damage, int type) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.type = type;
    }

    public void update(float dt) {
        if (target == null || target.isDead() || target.reachedCastle()) {
            hit = true;
            return;
        }

        float dx = target.x - x;
        float dy = target.y - y;
        float dist = (float) Math.hypot(dx, dy);
        float speed = GameConfig.PROJECTILE_SPEED;

        if (dist < speed * dt) {
            target.damage(damage);
            // Apply slow effect if it's an Ice Tower projectile
            if (type == GameConfig.TOWER_ICE) {
                target.slowTimer = 2.0f; // 2 seconds slow
            }
            hit = true;
        } else {
            x += (dx / dist) * speed * dt;
            y += (dy / dist) * speed * dt;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF); // White projectiles
        canvas.drawCircle(x, y, 10, paint);
    }
}