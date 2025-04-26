package com.example.fishes.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.fishes.R;
import com.example.fishes.manager.SoundManager;
import com.example.fishes.model.EnemyFish;
import com.example.fishes.model.PlayerFish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private static final int[] LEVEL_EXP = {0, 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200, 102400};
    private int playerLevel = 0;
    private GameThread gameThread;
    private JoystickView joystickView;
    private PlayerFish player;
    private List<EnemyFish> enemies;
    private int LimitOfEnemies = 30;
    private int score = 0;
    private long lastSpawnTime = 0;
    private SoundManager soundManager;

    public GameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(false);

        SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        SCREEN_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;

        player = new PlayerFish(context, R.drawable.player);
        enemies = new ArrayList<>();
        soundManager = new SoundManager(context);
    }

    public PlayerFish getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        pause();
    }

    public void pause() {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join(); // 等待线程完全退出
            } catch (InterruptedException e) {
                Log.e("GameSurface.stop", Objects.requireNonNull(e.getMessage()));
            }
            gameThread = null;
        }
    }

    public void resume() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    public void update() {
        synchronized (enemies) {
            player.update();
            Iterator<EnemyFish> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                EnemyFish enemy = iterator.next();
                enemy.update();
                if (enemy.isOutOfScreen()) {
                    iterator.remove();
                }
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSpawnTime > 500 && enemies.size() < LimitOfEnemies) {
                spawnEnemy();
                lastSpawnTime = currentTime;
            }
        }
        checkCollisions();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // 绘制背景（如果有背景图可绘制此处，此处假设使用纯色背景）
            canvas.drawColor(0xFF87CEFA); // 天空蓝色

            // 绘制玩家和敌人
            player.draw(canvas);
            synchronized (enemies) {
                for (EnemyFish enemy : enemies) {
                    enemy.draw(canvas);
                }
            }
        }
    }

    private void spawnEnemy() {
        int enemyResId = R.drawable.enemy_small;
        EnemyFish enemy = new EnemyFish(getContext(), EnemyFish.EnemyFishType.SMALL_FISH, enemyResId);
        enemies.add(enemy);
    }

    private void checkCollisions() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            EnemyFish enemy = enemies.get(i);
            if (player.collidesWith(enemy)) {
                if (player.isLargerThan(enemy)) {
                    // 玩家吃掉敌鱼
                    enemies.remove(i);
                    score += enemy.getEnemyFishType().experience;
                    soundManager.playEatSound();

                    int newLevel = getLevel(score);
                    if (newLevel > playerLevel) {
                        playerLevel = newLevel;
                        player.grow(1.1f);
                        soundManager.playLevelUpSound();
                    }
                } else {
                    // 撞到更大的敌鱼，游戏结束
                    soundManager.playCrashSound();
                    gameOver();
                    break;
                }
            }
        }
    }

    private static int getLevel(int score) {
        for (int i = LEVEL_EXP.length - 1; i >= 0; --i) {
            if (score >= LEVEL_EXP[i]) {
                return i;
            }
        }
        return 0;
    }

    public void setJoystick(JoystickView joystickView) {
        this.joystickView = joystickView;
        if (player != null) {
            player.setJoystick(joystickView);
        }
    }

    public interface GameOverListener {
        void onGameOver(int finalScore);
    }

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }


    private void gameOver() {
        // 通过Activity跳转是在Activity层完成，此处可由GameActivity在检测到分数后跳转
        // 暂停线程
        pause();
        if (gameOverListener != null) {
            gameOverListener.onGameOver(score);
        }
    }

    private static class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private final GameSurface gameSurface;
        private boolean running = false;
        private final int TARGET_FPS = 60;
        private final long FRAME_TIME = 1000 / TARGET_FPS;

        public GameThread(SurfaceHolder holder, GameSurface surface) {
            surfaceHolder = holder;
            gameSurface = surface;
        }

        public void setRunning(boolean run) {
            running = run;
        }

        @Override
        public void run() {
            long startTime, elapsed, waitTime;
            while (running) {
                startTime = System.currentTimeMillis();
                gameSurface.update();

                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameSurface.draw(canvas);
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

                elapsed = System.currentTimeMillis() - startTime;
                waitTime = FRAME_TIME - elapsed;
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Log.e("GameThread.run", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
        }
    }
}
