package com.example.game2;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.database.sqlite.SQLiteDatabase;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private volatile boolean playing;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private int screenX;
    private int screenY;
    private float characterX;
    private float characterY;
    private float characterRadius;
    private float characterSpeed;
    private boolean isMovingLeft = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private List<Obstacle> obstacles;
    private Random random;
    private int score;
    private Paint scorePaint;
    private Bitmap selectedCharacterBitmap;
    private int selectedCharacterWidth;
    private int selectedCharacterHeight;


    private boolean gameOver;
    private Bitmap restartButton;
    private Bitmap playerBitmap;
    private Bitmap obstacleBitmap;
    private int playerWidth;
    private int playerHeight;
    private int obstacleWidth;
    private int obstacleHeight;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Bitmap statisticsButton;
    private int buttonWidth;
    private int buttonHeight;private CharacterSkin currentSkin;






    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;

        surfaceHolder = getHolder();
        paint = new Paint();
        characterX = screenX / 2f;
        characterY = screenY / 2f;
        characterRadius = screenX / 30f;
        characterSpeed = screenX / 100f;
        obstacles = new ArrayList<>();
        random = new Random();
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        score = 0;
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(50);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setTextAlign(Paint.Align.RIGHT);


        gameOver = false;
        restartButton = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);
        obstacleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_2);
        playerWidth = playerBitmap.getWidth();
        playerHeight = playerBitmap.getHeight();
        obstacleWidth = obstacleBitmap.getWidth();
        obstacleHeight = obstacleBitmap.getHeight();




        // Создаем начальные препятствия
        createObstacles();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        // Check if the game is over
        if (gameOver) {
            return; // Don't update anything if the game is over
        }

        // Update the position of the character based on its movement
        if (isMovingLeft && characterX > characterRadius) {
            characterX -= characterSpeed;
        } else if (characterX < screenX - characterRadius) {
            characterX += characterSpeed;
        }

        if (isMovingUp && characterY > characterRadius) {
            characterY -= characterSpeed;
        } else if (isMovingDown && characterY < screenY - characterRadius) {
            characterY += characterSpeed;
        }

        // Check collision between character and obstacles
        for (Obstacle obstacle : obstacles) {
            if (RectF.intersects(obstacle.getCollisionRect(), getCharacterCollisionRect())) {
                // If collision occurred, end the game
                gameOver = true;
                return;
            }
        }

        // Move obstacles and remove those that go off the screen
        for (Obstacle obstacle : obstacles) {
            obstacle.move();
            if (obstacle.getRect().right < 0) {
                obstacles.remove(obstacle);
                break;
            }
        }


        if (obstacles.size() < 5) {
            createObstacle();
            score++;
        }
    }
    public void setCharacterSkin(CharacterSkin characterSkin) {
        this.currentSkin = characterSkin;
        playerBitmap = characterSkin.getSkinImage();
        playerWidth = playerBitmap.getWidth();
        playerHeight = playerBitmap.getHeight();
    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            // Рисуем персонажа
            canvas.drawBitmap(playerBitmap, characterX - playerWidth / 2f, characterY - playerHeight / 2f, paint);

            // Рисуем препятствия
            for (Obstacle obstacle : obstacles) {
                canvas.drawBitmap(obstacleBitmap, obstacle.getRect().left, obstacle.getRect().top, paint);
            }

            // Рисуем текст с очками
            canvas.drawText("Score: " + score, screenX - 50, 50, scorePaint);

            // Если игра окончена, рисуем кнопку перезапуска
            if (gameOver) {
                int buttonWidth = restartButton.getWidth();
                int buttonHeight = restartButton.getHeight();
                int buttonLeft = (screenX - buttonWidth) / 2;
                int buttonTop = (screenY - buttonHeight) / 2;
                canvas.drawBitmap(restartButton, buttonLeft, buttonTop, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Проверяем, было ли нажатие на кнопку "Статистика"
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            int buttonWidth = restartButton.getWidth();
            int buttonHeight = restartButton.getHeight();
            int buttonLeft = (screenX - buttonWidth) / 2;
            int buttonTop = (screenY - buttonHeight) / 2;
            if (touchX >= buttonLeft && touchX <= buttonLeft + buttonWidth &&
                    touchY >= buttonTop && touchY <= buttonTop + buttonHeight) {
                showStatistics();
            }
        }
        if (gameOver) {
            // Если игра окончена, обработываем касание кнопки перезапуска
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();

                int buttonWidth = restartButton.getWidth();
                int buttonHeight = restartButton.getHeight();
                int buttonLeft = (screenX - buttonWidth) / 2;
                int buttonTop = (screenY - buttonHeight) / 2;

                if (touchX >= buttonLeft && touchX <= buttonLeft + buttonWidth &&
                        touchY >= buttonTop && touchY <= buttonTop + buttonHeight) {
                    restartGame();
                }
            }
            return true;
        } else {
            // Если игра не окончена, обработываем движения персонажа
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                // Переключаем направление движения персонажа
                isMovingLeft = touchX < characterX;
                isMovingUp = touchY < characterY;
                isMovingDown = touchY > characterY;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // Останавливаем движение персонажа при прекращении касания
                isMovingLeft = false;
                isMovingUp = false;
                isMovingDown = false;
            }
        }

        return true;
    }
    private void showStatistics() {
        long startTime = System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        int score = this.score; // Здесь необходимо получить реальное количество набранных очков

        ContentValues values = new ContentValues();
        values.put("start_time", startTime);
        values.put("duration", duration);
        values.put("score", score);

        database.insert("game_statistics", null, values);

    }
    private RectF getCharacterCollisionRect() {
        return new RectF(
                characterX - playerWidth / 2f,
                characterY - playerHeight / 2f,
                characterX + playerWidth / 2f,
                characterY + playerHeight / 2f
        );
    }

    private void createObstacles() {
        int numObstacles = 5;
        int obstacleWidth = screenX / 15;
        int obstacleHeight = screenY / 10;
        int gap = screenY / 4;
        int minTop = screenY / 10;
        int maxTop = screenY - minTop - obstacleHeight;

        int left = screenX;
        for (int i = 0; i < numObstacles; i++) {
            int top = minTop + random.nextInt(maxTop - minTop);
            int right = left + obstacleWidth;
            int bottom = top + obstacleHeight;
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            obstacles.add(new Obstacle(left, top, right, bottom, color));
            left += obstacleWidth + gap;
        }
    }

    private void createObstacle() {
        int obstacleWidth = screenX / 15;
        int obstacleHeight = screenY / 10;
        int minTop = screenY / 10;
        int maxTop = screenY - minTop - obstacleHeight;

        int left = screenX;
        int top = minTop + random.nextInt(maxTop - minTop);
        int right = left + obstacleWidth;
        int bottom = top + obstacleHeight;
        int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        obstacles.add(new Obstacle(left, top, right, bottom, color));
    }

    private void restartGame() {
        // Сбрасываем состояние игры
        characterX = screenX / 2f;
        characterY = screenY / 2f;
        obstacles.clear();
        createObstacles();
        score = 0;
        gameOver = false;
        resume(); // Запускаем игру заново
    }
    private class Obstacle {
        private RectF rect;
        private int color;
        private float speed;

        public Obstacle(float left, float top, float right, float bottom, int color) {
            rect = new RectF(left, top, right, bottom);
            this.color = color;
            speed = characterSpeed * 2;
        }

        public RectF getRect() {
            return rect;
        }

        public int getColor() {
            return color;
        }

        public RectF getCollisionRect() {
            // Уменьшаем размер прямоугольника столкновения, чтобы учесть пространство между персонажем и препятствием
            float padding = characterRadius * 0.9f;
            return new RectF(
                    rect.left + padding,
                    rect.top + padding,
                    rect.right - padding,
                    rect.bottom - padding
            );
        }

        public void move() {
            rect.left -= speed;
            rect.right -= speed;
        }
    }
}