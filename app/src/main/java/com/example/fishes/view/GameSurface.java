// GameSurface.java
package com.example.fishes.view;

import static com.example.fishes.model.PlayerFish.PLAYER_SCALE;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fishes.R;
import com.example.fishes.manager.SoundManager;
import com.example.fishes.model.EnemyFish;
import com.example.fishes.model.PlayerFish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    public interface GameOverListener {
        void onGameOver(int finalScore);
    }

    // --- 新增 listener 字段 ---
    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private final List<EnemyFish> enemies = new ArrayList<>();
    private final List<EnemyFish> drawEnemies = new ArrayList<>();
    private final Object enemyLock = new Object();

    private PlayerFish player;
    private SoundManager soundManager;
    private JoystickView joystickView;

    private GameThread gameThread;
    private int score = 0;
    private int playerLevel = 0;
    private long lastSpawnTime = 0;
    private int limitOfEnemies = 30;

    private static final int[] LEVEL_EXP = {0, 400, 800, 1600, 3200, 6400};

    public GameSurface(Context context) {
        this(context, null);
    }

    public GameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);
        SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        SCREEN_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;
        player = new PlayerFish(context, R.drawable.player, R.drawable.player2);
        soundManager = new SoundManager(context);
    }

    public void setJoystick(JoystickView joystick) {
        this.joystickView = joystick;
        player.setJoystick(joystick);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    public void startThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    public void stopThread() {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join();
                Log.d("GameSurface", "stopThread: GameThread joined");
            } catch (InterruptedException e) {
                Log.e("GameSurface", "Thread stop error", e);
            }
            gameThread = null;
        }
    }

    public void update() {
        player.update();
        synchronized (enemyLock) {
            Iterator<EnemyFish> iter = enemies.iterator();
            while (iter.hasNext()) {
                EnemyFish e = iter.next();
                e.update();
                if (e.isOutOfScreen()) iter.remove();
            }
            checkCollisions();
            long now = System.currentTimeMillis();
            if (now - lastSpawnTime > 500 && enemies.size() < limitOfEnemies) {
                spawnEnemy();
                lastSpawnTime = now;
            }
            synchronized (drawEnemies) {
                drawEnemies.clear();
                drawEnemies.addAll(enemies);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;
        canvas.drawColor(0xFF87CEFA);
        player.draw(canvas);
        synchronized (drawEnemies) {
            for (EnemyFish e : drawEnemies) {
                e.draw(canvas);
            }
        }
    }

    private void checkCollisions() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            EnemyFish e = enemies.get(i);
            if (player.collidesWith(e)) {
                if (player.isLargerThan(e)) {
                    enemies.remove(i);
                    score += e.getEnemyFishType().experience;
                    soundManager.playEatSound();
                    int newLvl = getLevel(score);  // 0…5
                    if (newLvl > playerLevel) {
                        float oldScale = PLAYER_SCALE[playerLevel];
                        float newScale = PLAYER_SCALE[newLvl];
                        player.grow(newScale / oldScale);
                        playerLevel = newLvl;
                        soundManager.playLevelUpSound();
                    }
                } else {
                    // 玩家死亡，触发 GameOver
                    soundManager.playCrashSound();
                    if (gameOverListener != null) {
                        gameOverListener.onGameOver(score);
                    }
                    break;
                }
            }
        }
    }

    private void spawnEnemy() {
        int lvl = getLevel(score);               // 当前玩家等级
        float r = (float) Math.random();         // [0,1) 的随机数
        EnemyFish.EnemyFishType type;

        if (lvl <= 2) {                         // Lv2：10% 中鱼
            type = (r < 0.10f) ? EnemyFish.EnemyFishType.MEDIUM_FISH
                    : EnemyFish.EnemyFishType.SMALL_FISH;

        } else if (lvl == 3) {                   // Lv3：20% 中鱼，10% 大鱼
            if      (r < 0.10f) type = EnemyFish.EnemyFishType.LARGE_FISH;
            else if (r < 0.30f) type = EnemyFish.EnemyFishType.MEDIUM_FISH;
            else                type = EnemyFish.EnemyFishType.SMALL_FISH;

        } else if (lvl == 4) {                   // Lv4：10% 鲨鱼，20% 大鱼
            if      (r < 0.10f) type = EnemyFish.EnemyFishType.SHARK;
            else if (r < 0.30f) type = EnemyFish.EnemyFishType.LARGE_FISH;
            else if (r < 0.60f) type = EnemyFish.EnemyFishType.MEDIUM_FISH;
            else                type = EnemyFish.EnemyFishType.SMALL_FISH;

        } else {                                 // Lv5 及以上：加入 Boss
            if      (r < 0.15f) type = EnemyFish.EnemyFishType.BOSS_FISH;
            else if (r < 0.35f) type = EnemyFish.EnemyFishType.SHARK;
            else if (r < 0.60f) type = EnemyFish.EnemyFishType.LARGE_FISH;
            else if (r < 0.80f) type = EnemyFish.EnemyFishType.MEDIUM_FISH;
            else                type = EnemyFish.EnemyFishType.SMALL_FISH;
        }
        EnemyFish e = new EnemyFish(getContext(), type);
        synchronized (enemyLock) {
            enemies.add(e);
        }
    }

    private static int getLevel(int exp) {
        for (int i = LEVEL_EXP.length - 1; i >= 0; i--) {
            if (exp >= LEVEL_EXP[i]) return i;
        }
        return 0;
    }

    public PlayerFish getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    private static class GameThread extends Thread {
        private final SurfaceHolder holder;
        private final GameSurface surface;
        private volatile boolean running;
        private static final int TARGET_FPS = 60;

        GameThread(SurfaceHolder h, GameSurface s) {
            holder = h;
            surface = s;
        }

        void setRunning(boolean run) {
            running = run;
        }

        @Override
        public void run() {
            final long targetTimeNs = 1_000_000_000L / TARGET_FPS;
            while (running) {
                long startNs = System.nanoTime();
                surface.update();
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) surface.draw(canvas);
                } catch (Exception e) {
                    Log.e("GameThread", "Render error", e);
                } finally {
                    if (canvas != null) {
                        try {
                            holder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            Log.e("GameThread", "unlock error", e);
                        }
                    }
                }
                long elapsedNs = System.nanoTime() - startNs;
                long sleepNs = targetTimeNs - elapsedNs;
                if (sleepNs > 0) {
                    try {
                        Thread.sleep(sleepNs / 1_000_000L, (int) (sleepNs % 1_000_000L));
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
}
