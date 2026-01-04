package com.example.finalproject;

public class GameConfig {
    // --- Orientation & Screen ---
    public static final int CASTLE_HP = 100;
    public static final int START_COINS = 50; //  starting cash
    public static final int REGULAR_COINS = 10;
    public static final int ICE_COINS = 15;
    public static final int FIRE_COINS = 25;
    public static final int BOSS_COINS = 100;
    public static final int BOSS_DAMAGE_TO_CASTLE = 50; // Deals 50% damage

    // Regular enemy damage for comparison
    public static final int REGULAR_DAMAGE_TO_CASTLE = 10;
    public static final int FIRE_DAMAGE_TO_CASTLE = 20;

    // --- High Lethality Combat (Instant Kills) ---
    public static final float BASE_TOWER_DAMAGE = 7.0f;
    public static final float DAMAGE_PER_LEVEL = 1.5f;
    public static final float BASE_FIRE_RATE = 0.6f;
    public static final float PROJECTILE_SPEED = 1400f;
    public static final float BASE_TOWER_RANGE = 320f;
    public static final float FIRE_RATE_PER_LEVEL = 0.1f;

    // --- Weakened Enemies (Die Faster) ---
    public static final int REGULAR_HP = 20;
    public static final int ICE_HP = 45;
    public static final int FIRE_HP = 75;
    public static final int BOSS_HP = 400;

    public static final float REGULAR_SPEED = 180f;
    public static final float ICE_SPEED = 200f;
    public static final float FIRE_SPEED = 150f;
    public static final float BOSS_SPEED = 100f;

    // --- Type IDs ---
    public static final int ENEMY_REGULAR = 0;
    public static final int ENEMY_ICE = 1;
    public static final int ENEMY_FIRE = 2;
    public static final int ENEMY_BOSS = 3;

    public static final int TOWER_REGULAR = 0;
    public static final int TOWER_ICE = 1;
    public static final int TOWER_FIRE = 2;

    public static final int MAX_TOWER_LEVEL = 3;
    public static final float RANGE_PER_LEVEL = 40f;


    public static int getTowerCost(int type) {
        switch (type) {
            case TOWER_ICE: return 75;
            case TOWER_FIRE: return 100;
            default: return 50;
        }
    }    public static int getUpgradeCost(int type, int level) { return 35 * level; }

    public static float calculateDamage(int towerType, int enemyType, float baseDamage) {
        return baseDamage;
    }

    public static final int BASE_ENEMIES_PER_WAVE = 10; // Slightly fewer enemies per wave
    public static final float WAVE_ENEMY_INCREASE = 2;
}