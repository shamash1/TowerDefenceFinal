package com.example.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    public float x, y;
    public int typeId;
    public int level = 1;
    public float range = GameConfig.BASE_TOWER_RANGE;
    private float cooldownTimer = 0f;
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

        // Firing Logic
        if (cooldownTimer <= 0f) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                float damage = GameConfig.BASE_TOWER_DAMAGE * (float)Math.pow(GameConfig.DAMAGE_PER_LEVEL, level - 1);
                projectiles.add(new Projectile(x, y, target, (int)damage, typeId));
                cooldownTimer = GameConfig.BASE_FIRE_RATE;
            }
        }
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
        paint.setStyle(Paint.Style.FILL);
        if (typeId == GameConfig.TOWER_ICE) paint.setColor(0xFF00E5FF);
        else if (typeId == GameConfig.TOWER_FIRE) paint.setColor(0xFFFF5722);
        else paint.setColor(0xFF9E9E9E);

        canvas.drawCircle(x, y, 40, paint);
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