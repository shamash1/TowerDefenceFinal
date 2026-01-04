package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.List;

public class Enemy {
    public float x, y;
    public int typeId;
    public int hp, maxHp;
    public float speed;
    public int coinValue;
    public int damageToCastle;

    // --- Slow Effect Variables ---
    public float slowTimer = 0f;

    public List<PointF> path;
    public int pathIdx = 0;
    public boolean reached = false;

    public Enemy(int typeId, List<PointF> path) {
        // Inside Enemy.java constructor
        if (typeId == GameConfig.ENEMY_BOSS) {
            this.maxHp = GameConfig.BOSS_HP;
            this.speed = GameConfig.BOSS_SPEED;
            this.coinValue = GameConfig.BOSS_COINS;
            this.damageToCastle = GameConfig.BOSS_DAMAGE_TO_CASTLE; // Now 75
        } else if (typeId == GameConfig.ENEMY_FIRE) {
            this.damageToCastle = GameConfig.FIRE_DAMAGE_TO_CASTLE;
            // ...
        } else {
            this.damageToCastle = GameConfig.REGULAR_DAMAGE_TO_CASTLE;
            // ...
        }
        this.typeId = typeId;
        this.path = path;
        if (path != null && !path.isEmpty()) {
            this.x = path.get(0).x;
            this.y = path.get(0).y;
        }

        if (typeId == GameConfig.ENEMY_ICE) {
            maxHp = GameConfig.ICE_HP; speed = GameConfig.ICE_SPEED; coinValue = GameConfig.ICE_COINS;
        } else if (typeId == GameConfig.ENEMY_FIRE) {
            maxHp = GameConfig.FIRE_HP; speed = GameConfig.FIRE_SPEED; coinValue = GameConfig.FIRE_COINS;
        } else if (typeId == GameConfig.ENEMY_BOSS) {
            maxHp = GameConfig.BOSS_HP; speed = GameConfig.BOSS_SPEED; coinValue = GameConfig.BOSS_COINS;
        } else {
            maxHp = GameConfig.REGULAR_HP; speed = GameConfig.REGULAR_SPEED; coinValue = GameConfig.REGULAR_COINS;
        }
        this.hp = maxHp;
        this.damageToCastle = 10;
    }

    public void update(float dt) {
        if (reached || isDead()) return;
        if (pathIdx >= path.size() - 1) { reached = true; return; }

        // Update Slow Timer
        if (slowTimer > 0) slowTimer -= dt;

        // Apply Speed Penalty if slowed
        float currentSpeed = (slowTimer > 0) ? speed * 0.5f : speed;

        PointF target = path.get(pathIdx + 1);
        float dx = target.x - x;
        float dy = target.y - y;
        float dist = (float) Math.hypot(dx, dy);
        float moveStep = currentSpeed * dt;

        if (dist < moveStep) {
            pathIdx++;
            x = target.x; y = target.y;
        } else {
            x += (dx / dist) * moveStep;
            y += (dy / dist) * moveStep;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);

        // Change color to Cyan if slowed
        if (slowTimer > 0) paint.setColor(0xFF00E5FF);
        else if (typeId == GameConfig.ENEMY_ICE) paint.setColor(0xFF88FFFF);
        else if (typeId == GameConfig.ENEMY_FIRE) paint.setColor(0xFFFF6666);
        else if (typeId == GameConfig.ENEMY_BOSS) paint.setColor(0xFFAA33FF);
        else paint.setColor(0xFF00FF00);

        canvas.drawCircle(x, y, (typeId == GameConfig.ENEMY_BOSS) ? 40f : 25f, paint);
    }


    public boolean isDead() { return hp <= 0; }
    public void damage(int amount) {
        this.hp -= amount;
        // flash the enemy white for 1 frame or log it
        if (this.hp < 0) this.hp = 0;
    }    public int getCoinValue() { return coinValue; }
    public boolean reachedCastle() { return reached; }
    public int getPathProgress() { return pathIdx; }
}