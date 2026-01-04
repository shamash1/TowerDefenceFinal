package com.example.finalproject;

import java.util.List;
import java.util.Random;

public class WaveManager {
    private int wave = 0;
    private float spawnTimer = 0f;
    private int enemiesSpawnedInWave = 0;
    private int totalEnemiesToSpawn = 0;

    // NEW: Timer for showing the wave title text
    public float waveTitleTimer = 0f;
    private final Random random = new Random();

    // Slower spawning for tactical play
    private static final float SPAWN_INTERVAL = 2.5f;


    // Inside WaveManager.java
    public void startNextWave() {
        wave++;
        enemiesSpawnedInWave = 0;
        float scale = (float) Math.pow(GameConfig.WAVE_ENEMY_INCREASE, wave - 1);
        totalEnemiesToSpawn = Math.max(5, (int)(GameConfig.BASE_ENEMIES_PER_WAVE * scale));
        spawnTimer = 0;

        // Trigger the visual text for 2 seconds
        waveTitleTimer = 2.0f;
    }

    public void update(float dt, List<Enemy> enemies, List<PathLine> paths) {
        if (waveTitleTimer > 0) waveTitleTimer -= dt;

        if (enemies.isEmpty() && enemiesSpawnedInWave >= totalEnemiesToSpawn) {
            startNextWave();
        }

        if (enemiesSpawnedInWave < totalEnemiesToSpawn) {
            spawnTimer += dt;
            if (spawnTimer >= 2.5f) { // Spawn interval
                spawnTimer = 0;
                spawnEnemy(enemies, paths);
            }
        }
    }

    private void spawnEnemy(List<Enemy> enemies, List<PathLine> paths) {
        if (paths.isEmpty()) return;

        int typeId = GameConfig.ENEMY_REGULAR; // Default

        // 1. BOSS LOGIC: Spawn a boss every 5th wave as the very first enemy
        if (wave % 5 == 0 && enemiesSpawnedInWave == 0) {
            typeId = GameConfig.ENEMY_BOSS;
        }
        // 2. SPECIAL TYPES LOGIC: Start spawning others after wave 1
        else if (wave > 1) {
            int roll = random.nextInt(100); // 0 to 99

            // Adjust these chances as you like:
            if (wave >= 3 && roll < 20) {
                typeId = GameConfig.ENEMY_FIRE; // 20% chance after wave 3
            } else if (wave >= 5 && roll < 40) {
                typeId = GameConfig.ENEMY_ICE;  // 20% chance after wave 5
            }
            // Otherwise stays REGULAR
        }

        // Randomly pick one of your 3 horizontal lanes
        PathLine selectedPath = paths.get(random.nextInt(paths.size()));

        // Add the enemy to the game
        enemies.add(new Enemy(typeId, selectedPath.getPoints()));
        enemiesSpawnedInWave++;
    }
    public void reset() {
        this.wave = 0;
        this.enemiesSpawnedInWave = 0;
        this.totalEnemiesToSpawn = 0;
        this.spawnTimer = 0;
        this.waveTitleTimer = 0;
        startNextWave(); // Starts Wave 1 immediately
    }

    public int getWaveNumber() { return wave; }
}