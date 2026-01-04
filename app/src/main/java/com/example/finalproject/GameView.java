package com.example.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {
    // --- State Constants ---
    private static final int STATE_MENU = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_INSTRUCTIONS = 2;
    private static final int STATE_GAME_OVER = 3;
    private int currentState = STATE_MENU;

    private Thread gameThread;
    private boolean isPlaying = false;
    private final SurfaceHolder holder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Tower> towers = new ArrayList<>();
    private final List<BuildSpot> buildSpots = new ArrayList<>();
    private final List<PathLine> paths = new ArrayList<>();
    private final WaveManager waveManager = new WaveManager();

    private int coins;
    private int castleHp;
    private long lastFrameTime;
    private float shakeTimer = 0f;
    private int selectedTowerType = GameConfig.TOWER_REGULAR;
    private String playerName = "Defender";

    public GameView(Context context) {
        super(context);
        this.holder = getHolder();
        // initGame is called inside the onTouchEvent when "PLAY" is pressed
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    private void initGame() {
        enemies.clear();
        towers.clear();
        paths.clear();
        this.coins = GameConfig.START_COINS;
        this.castleHp = GameConfig.CASTLE_HP;
        this.shakeTimer = 0f;

        float w = getWidth();
        float h = getHeight();

        PointF merge = new PointF(w * 0.75f, h * 0.5f);
        PointF castle = new PointF(w - 50, h * 0.5f);

        // Define Paths
        List<PointF> p1 = new ArrayList<>();
        p1.add(new PointF(0, h * 0.2f)); p1.add(new PointF(w * 0.3f, h * 0.2f));
        p1.add(new PointF(w * 0.4f, h * 0.4f)); p1.add(merge); p1.add(castle);
        paths.add(new PathLine(p1));

        List<PointF> p2 = new ArrayList<>();
        p2.add(new PointF(0, h * 0.5f)); p2.add(merge); p2.add(castle);
        paths.add(new PathLine(p2));

        List<PointF> p3 = new ArrayList<>();
        p3.add(new PointF(0, h * 0.8f)); p3.add(new PointF(w * 0.3f, h * 0.8f));
        p3.add(new PointF(w * 0.4f, h * 0.6f)); p3.add(merge); p3.add(castle);
        paths.add(new PathLine(p3));

        // Define Build Spots
        buildSpots.clear();
        buildSpots.add(new BuildSpot(w * 0.35f, h * 0.12f));
        buildSpots.add(new BuildSpot(w * 0.35f, h * 0.88f));
        buildSpots.add(new BuildSpot(w * 0.55f, h * 0.5f));
        buildSpots.add(new BuildSpot(w * 0.85f, h * 0.42f));
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!holder.getSurface().isValid()) continue;
            long now = System.currentTimeMillis();
            float dt = (lastFrameTime == 0) ? 0.016f : (now - lastFrameTime) / 1000f;
            lastFrameTime = now;

            if (currentState == STATE_PLAYING) update(dt);
            draw();
        }
    }

    private void update(float dt) {
        // Inside your GameView.java update() method
        if (castleHp <= 0) {
            castleHp = 0;
            currentState = STATE_GAME_OVER;

            // Save the wave reached to Firebase!
            FBsingleton.getInstance().saveHighScore(waveManager.getWaveNumber());
        }
        // Inside GameView.java update() method:
        if (currentState == STATE_GAME_OVER) {
            // Trigger Firebase save when player loses
            FBsingleton.getInstance().saveHighScore(waveManager.getWaveNumber());
        }
        if (shakeTimer > 0) shakeTimer -= dt;
        waveManager.update(dt, enemies, paths);
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i); e.update(dt);
            if (e.reachedCastle()) {
                castleHp -= e.damageToCastle;
                shakeTimer = (e.typeId == GameConfig.ENEMY_BOSS) ? 0.8f : 0.2f;
                enemies.remove(i);
            } else if (e.isDead()) { coins += e.getCoinValue(); enemies.remove(i); }
        }
        for (Tower t : towers) t.update(dt, enemies);

        // Transition to Game Over state if health hits 0
        if (castleHp <= 0) {
            castleHp = 0;
            currentState = STATE_GAME_OVER;
        }
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        if (currentState == STATE_MENU) {
            drawMainMenu(canvas);
        } else if (currentState == STATE_INSTRUCTIONS) {
            drawInstructions(canvas);
        } else {
            // This draws the gameplay for both STATE_PLAYING and STATE_GAME_OVER
            drawGameplay(canvas);
            if (currentState == STATE_GAME_OVER) drawGameOverScreen(canvas);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    private void drawGameplay(Canvas canvas) {
        if (shakeTimer > 0) canvas.translate((float)(Math.random()*20-10), (float)(Math.random()*20-10));
        canvas.drawColor(0xFF2E3B23);
        drawTiles(canvas);

        paint.setStyle(Paint.Style.STROKE);
        for (PathLine pl : paths) {
            paint.setColor(0xFF1A150E); paint.setStrokeWidth(65f); pl.draw(canvas, paint);
            paint.setColor(0xFF505050); paint.setStrokeWidth(50f); pl.draw(canvas, paint);
            paint.setColor(0xFF666666); paint.setStrokeWidth(35f);
            paint.setPathEffect(new DashPathEffect(new float[]{20, 15}, 0));
            pl.draw(canvas, paint);
            paint.setPathEffect(null);
        }

        for (BuildSpot s : buildSpots) s.draw(canvas, paint);
        for (Tower t : towers) t.draw(canvas, paint);
        for (Enemy e : enemies) e.draw(canvas, paint);
        drawHUD(canvas);
        if (waveManager.waveTitleTimer > 0) drawWaveTransition(canvas);
    }

    private void drawTiles(Canvas canvas) {
        int ts = 120; paint.setStyle(Paint.Style.FILL);
        for (int x = 0; x < getWidth(); x += ts) {
            for (int y = 0; y < getHeight(); y += ts) {
                paint.setColor(((x/ts + y/ts) % 2 == 0) ? 0xFF3B4A30 : 0xFF2E3B23);
                canvas.drawRect(x + 2, y + 2, x + ts - 2, y + ts - 2, paint);
            }
        }
    }

    private void drawMainMenu(Canvas canvas) {
        drawTiles(canvas);
        paint.setColor(0xCC000000);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(Color.WHITE); paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100f);
        canvas.drawText("STONE DEFENSE", getWidth()/2f, getHeight()/3f, paint);
        paint.setTextSize(40f);
        canvas.drawText("Welcome, " + playerName, getWidth()/2f, getHeight()/3f + 60, paint);

        drawButton(canvas, getWidth()/2f, getHeight()/2f, "PLAY", Color.GREEN);
        drawButton(canvas, getWidth()/2f, getHeight()/2f + 160, "INFO", Color.LTGRAY);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawInstructions(Canvas canvas) {
        drawTiles(canvas);
        paint.setColor(0xEE000000);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(Color.WHITE); paint.setTextSize(60f);
        canvas.drawText("HOW TO PLAY", 50, 100, paint);
        paint.setTextSize(35f);
        canvas.drawText("- Select tower type at bottom.", 50, 200, paint);
        canvas.drawText("- Tap cyan circles to build.", 50, 260, paint);
        canvas.drawText("- Tap towers to upgrade.", 50, 320, paint);
        drawButton(canvas, 150, getHeight() - 100, "BACK", Color.RED);
    }

    private void drawButton(Canvas canvas, float cx, float cy, String txt, int color) {
        float w = 300, h = 100;
        paint.setColor(color);
        canvas.drawRect(cx - w/2, cy - h/2, cx + w/2, cy + h/2, paint);
        paint.setColor(Color.BLACK); paint.setTextSize(50f); paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(txt, cx, cy + 18, paint);
    }

    private void drawHUD(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xCC000000);
        canvas.drawRect(0, 0, getWidth(), 100, paint);
        paint.setColor(Color.YELLOW); paint.setTextSize(50f);
        canvas.drawText("💰 " + coins, 40, 70, paint);

        float hpRatio = (float) castleHp / GameConfig.CASTLE_HP;
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(getWidth() - 440, 40, getWidth() - 40, 75, paint);
        paint.setColor(hpRatio > 0.3 ? Color.GREEN : Color.RED);
        canvas.drawRect(getWidth() - 440, 40, getWidth() - 440 + (400 * hpRatio), 75, paint);

        // Tower Selector
        float mY = getHeight() - 150;
        drawMenuBtn(canvas, 50, mY, "REG", GameConfig.TOWER_REGULAR);
        drawMenuBtn(canvas, 200, mY, "ICE", GameConfig.TOWER_ICE);
        drawMenuBtn(canvas, 350, mY, "FIRE", GameConfig.TOWER_FIRE);
    }

    private void drawMenuBtn(Canvas canvas, float x, float y, String t, int type) {
        if (selectedTowerType == type) {
            paint.setColor(Color.YELLOW); paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(8f);
            canvas.drawRect(x-5, y-5, x+135, y+135, paint);
        }
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xAA000000);
        canvas.drawRect(x, y, x+130, y+130, paint);
        paint.setColor(Color.WHITE); paint.setTextSize(35f); paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(t, x+65, y+85, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawWaveTransition(Canvas canvas) {
        paint.setColor(Color.WHITE); paint.setTextSize(100f); paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("WAVE " + waveManager.getWaveNumber() + " START", getWidth()/2f, getHeight()/2f, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawGameOverScreen(Canvas canvas) {
        paint.setColor(0xEE000000); canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        paint.setColor(Color.RED); paint.setTextSize(120f); paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("GAME OVER", getWidth()/2f, getHeight()/2f - 50, paint);
        drawButton(canvas, getWidth()/2f, getHeight()/2f + 100, "RESTART", Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float tx = event.getX(), ty = event.getY();

            if (currentState == STATE_MENU) {
                if (Math.abs(tx - getWidth()/2f) < 150 && Math.abs(ty - getHeight()/2f) < 50) {
                    initGame(); waveManager.reset(); currentState = STATE_PLAYING;
                } else if (Math.abs(tx - getWidth()/2f) < 150 && Math.abs(ty - (getHeight()/2f + 160)) < 50) {
                    currentState = STATE_INSTRUCTIONS;
                }
                return true;
            }

            if (currentState == STATE_INSTRUCTIONS) {
                if (tx > 0 && tx < 300 && ty > getHeight() - 200) currentState = STATE_MENU;
                return true;
            }

            if (currentState == STATE_GAME_OVER) {
                if (Math.abs(tx - getWidth()/2f) < 150 && Math.abs(ty - (getHeight()/2f + 100)) < 50) {
                    waveManager.reset(); initGame(); currentState = STATE_PLAYING;
                }
                return true;
            }

            if (currentState == STATE_PLAYING) {
                // Tower Selection Logic
                float mY = getHeight() - 150;
                if (ty > mY && ty < mY + 130) {
                    if (tx > 50 && tx < 180) selectedTowerType = GameConfig.TOWER_REGULAR;
                    else if (tx > 200 && tx < 330) selectedTowerType = GameConfig.TOWER_ICE;
                    else if (tx > 350 && tx < 480) selectedTowerType = GameConfig.TOWER_FIRE;
                    return true;
                }
                // Build/Upgrade Logic
                for (BuildSpot s : buildSpots) {
                    if (s.contains(tx, ty)) {
                        if (s.occupied) handleUpgrade(s);
                        else {
                            int cost = GameConfig.getTowerCost(selectedTowerType);
                            if (coins >= cost) { coins -= cost; s.occupied = true; towers.add(new Tower(s.x, s.y, selectedTowerType)); }
                        }
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void handleUpgrade(BuildSpot spot) {
        for (Tower t : towers) {
            if (t.x == spot.x && t.y == spot.y) {
                int cost = t.getUpgradeCost();
                if (coins >= cost && t.level < GameConfig.MAX_TOWER_LEVEL) { coins -= cost; t.upgrade(); }
                break;
            }
        }
    }

    public void resume() { isPlaying = true; gameThread = new Thread(this); gameThread.start(); }
    public void pause() { isPlaying = false; try { if(gameThread != null) gameThread.join(); } catch (Exception e) {} }
}