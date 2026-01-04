package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class Tower {
    public float x, y;
    public int typeId;
    public int level = 1;
    public float range = GameConfig.BASE_TOWER_RANGE;
    public float cooldownTimer = 0f;
    public final List<Projectile> projectiles = new ArrayList<>();

    public Tower(float x, float y, int typeId) {
        this.x = x;
        this.y = y;
        this.typeId = typeId;
    }

    public void update(float dt, List<Enemy> enemies) {
        if (cooldownTimer > 0) cooldownTimer -= dt;

        // Update Projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(dt);
            if (p.hit) projectiles.remove(i);
        }

        // Fire Logic
        if (cooldownTimer <= 0f) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                fire(target);
                cooldownTimer = GameConfig.BASE_FIRE_RATE;
            }
        }
    }

    private void fire(Enemy target) {
        float damage = GameConfig.BASE_TOWER_DAMAGE * (float)Math.pow(GameConfig.DAMAGE_PER_LEVEL, level - 1);
        projectiles.add(new Projectile(x, y, target, (int)damage, typeId));
    }

    private Enemy findTarget(List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (e.isDead() || e.reachedCastle()) continue;
            float dist = (float) Math.hypot(e.x - x, e.y - y);
            if (dist <= range) return e;
        }
        return null;
    }

    public void draw(Canvas canvas, Paint paint) {
        // 1. Draw Range Circle (Subtle)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x22FFFFFF);
        canvas.drawCircle(x, y, range, paint);

        // 2. Draw Tower Base
        paint.setStyle(Paint.Style.FILL);
        if (typeId == GameConfig.TOWER_ICE) paint.setColor(0xFF00E5FF);
        else if (typeId == GameConfig.TOWER_FIRE) paint.setColor(0xFFFF5722);
        else paint.setColor(0xFF9E9E9E);

        // Tower size grows slightly with level
        float radius = 40f + (level * 5);
        canvas.drawCircle(x, y, radius, paint);

        // 3. Level Indicator (Small dots)
        paint.setColor(Color.WHITE);
        for (int i = 0; i < level; i++) {
            canvas.drawCircle(x - 20 + (i * 20), y + radius + 15, 5, paint);
        }

        // 4. Draw Projectiles
        for (Projectile p : projectiles) p.draw(canvas, paint);
    }

    public int getUpgradeCost() {
        return GameConfig.getUpgradeCost(typeId, level);
    }

    public void upgrade() {
        if (level < GameConfig.MAX_TOWER_LEVEL) {
            level++;
            range += GameConfig.RANGE_PER_LEVEL;
        }
    }
}