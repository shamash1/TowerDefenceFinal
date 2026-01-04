package com.example.finalproject;

import java.util.List;
import java.util.Random;

public class WaveManager {
    private int wave = 0;
    private float spawnTimer = 0f;
    private int enemiesSpawnedInWave = 0;
    private int totalEnemiesToSpawn = 0;
    public float waveTitleTimer = 0f;
    private final Random random = new Random();

    public void startNextWave() {
        wave++;
        enemiesSpawnedInWave = 0;
        totalEnemiesToSpawn = GameConfig.BASE_ENEMIES_PER_WAVE + (int)(wave * GameConfig.WAVE_ENEMY_INCREASE);
        waveTitleTimer = 2.0f;
    }

    public void update(float dt, List<Enemy> enemies, List<PathLine> paths) {
        if (waveTitleTimer > 0) waveTitleTimer -= dt;

        if (enemies.isEmpty() && enemiesSpawnedInWave >= totalEnemiesToSpawn) {
            startNextWave();
        }

        if (enemiesSpawnedInWave < totalEnemiesToSpawn) {
            spawnTimer += dt;
            if (spawnTimer >= 1.5f) {
                spawnTimer = 0;
                if (!paths.isEmpty()) {
                    int pathIdx = random.nextInt(paths.size());
                    int type = random.nextInt(4); // 0-3 for various types
                    enemies.add(new Enemy(type, paths.get(pathIdx).getPoints()));
                    enemiesSpawnedInWave++;
                }
            }
        }
    }

    public void reset() {
        wave = 0;
        enemiesSpawnedInWave = 0;
        startNextWave();
    }

    public int getWaveNumber() { return wave; }
}