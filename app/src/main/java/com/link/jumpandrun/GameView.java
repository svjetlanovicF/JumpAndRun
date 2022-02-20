package com.link.jumpandrun;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

public class GameView extends SurfaceView implements Runnable{

    Context context;

    Thread gameThread;
    SurfaceHolder surfaceHolder;
    int gameWidth;
    int gameHeight;

    boolean running;
    Drawable nightSky;

    private float scaleFactor;

    Canvas canvas;
    Paint paint;
    Stars stars;
    MountainsHigh mountainsHigh;
    MountainsLow mountainsLow;
    Ground ground;
    Runner runner;
    Enemies enemies;
    StartButton startButton;
    RestartButton restartButton;

    //font
    Typeface customFont;

    private float gameSpeed = 1.0f;

    float score = 0;
    int highScore;

    public enum GameState{
        PRE_START, STARTED, GAME_OVER, COMPLETED
    }

    private GameState gameState;
    SoundPool soundPool;
    private int jumpSoundId;
    int failSoundId;
    int completedSoundId;

    private static final int BASE_WIDTH = 800;


    public GameView(Context context, int gameWidth, int gameHeight, float scaleFactor) {
        super(context);

        this.context = context;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.scaleFactor = scaleFactor;

        paint = new Paint();

        surfaceHolder = getHolder();        //omotac objekta surface

        surfaceHolder.setFixedSize(gameWidth, gameHeight);

        nightSky = getResources().getDrawable(R.drawable.sky);
        nightSky.setBounds(0, 0, gameWidth, gameHeight);    //namjesta granice gdje ce se iscrtati nebo
        nightSky.setDither(true);      //ublazava prelaz boja u gradientu na slici neba

        //ubacivanje zvuka
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)   //paralelno koriscenje 4 zvuka
                .build();

        failSoundId = soundPool.load(context, R.raw.fail, 1);
        completedSoundId = soundPool.load(context, R.raw.win, 1);

        stars = new Stars(context, gameWidth, gameHeight);
        mountainsHigh = new MountainsHigh(context, gameWidth, gameHeight);
        mountainsLow = new MountainsLow(context, gameWidth, gameHeight);
        ground = new Ground(context, gameWidth, gameHeight);
        runner = new Runner(context, gameWidth, gameHeight, soundPool);
        enemies = new Enemies(context, gameWidth, gameHeight);
        startButton = new StartButton(context, gameWidth, gameHeight, soundPool);
        restartButton = new RestartButton(context, gameWidth, gameHeight, soundPool);

        //izvlacenje iz foldera font
        customFont = ResourcesCompat.getFont(context, R.font.luckiest_guy);

        highScore = loadHighScore();

        gameState = GameState.PRE_START;


    }



    long startTime;
    long endTime;
    long delta;
    int fps = 0;

    @Override
    public void run() {

        while(running) {

            startTime = System.currentTimeMillis();

            update(delta);

            draw();

            endTime = System.currentTimeMillis();

            delta = endTime - startTime;
            if(delta != 0){
                fps = (int)(1000 / delta);
            }
        }

    }

    private void update(long delta){
        stars.update(delta, gameSpeed);
        mountainsHigh.update(delta, gameSpeed);
        mountainsLow.update(delta, gameSpeed);
        ground.update(delta, gameSpeed);

        switch (gameState){
            case PRE_START:
                gameSpeed = 0;
                startButton.update();
                break;
            case STARTED:

                gameSpeed = gameSpeed + (0.0004f * ((float) delta / 16f));

                //kada igrac zavrsi igru
                if(gameSpeed >= 3){
                    gameSpeed = 0;
                    gameState = GameState.COMPLETED;
                    soundPool.play(completedSoundId, 1, 1, 1, 0, 1.0f);
                    runner.stopRunning();
                }

                //upisuje prepreke u obstacle
                for (int i = 0; i < enemies.getEnemies().size(); i++) {
                    Enemies.Enemy obstacle = enemies.getEnemies().get(i);

                    //prolazi kroz prepreke i gleda koja je aktivna
                    if(obstacle.active){
                        //kad dodje do sudara igrac staje
                        if(Collision.detectCollision(runner, obstacle)){
                            runner.stopRunning();
                            gameState = GameState.GAME_OVER;        //proglasava igru zavrsenom ako dodje do sudara
                            soundPool.play(failSoundId, 1, 1, 1, 0, 1.0f);

                            if(score > highScore){
                                highScore = (int) score;
                                saveHighScore(highScore);
                            }
                        }
                    }
                }
                enemies.update(delta, gameSpeed);

                break;
            case GAME_OVER:
                gameSpeed = 0f;
                break;
            case COMPLETED:
                break;
        }

        runner.update(delta, gameSpeed);

    }

    private void draw(){
        if(surfaceHolder.getSurface().isValid()){
            canvas = surfaceHolder.lockCanvas();           //priprema grafike za crtanje

            nightSky.draw(canvas);          //crtanje neba
            stars.draw(canvas);             //crtanje zvijezda
            mountainsHigh.draw(canvas);     //crtanje visokih planina
            mountainsLow.draw(canvas);      //crtanje niskih planina
            ground.draw(canvas);            //crtanje zemljista

            drawHighScore(highScore);

            switch (gameState){
                case PRE_START:
                    canvas.drawColor(Color.argb(130, 16, 16, 16));
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(80);
                    drawVerticalCenteredText(canvas, paint, "JUMP & RUN", 0);
                    startButton.draw(canvas);
                    break;
                case STARTED:
                    enemies.draw(canvas);

                    score = ((gameSpeed - 1) * 5000) - 1;
                    drawScore((int) score);

                    break;
                case GAME_OVER:

                    enemies.draw(canvas);

                    canvas.drawColor(Color.argb(130, 16, 16, 16));
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    drawVerticalCenteredText(canvas, paint, "GAME OVER", 0);
                    restartButton.draw(canvas);
                    break;
                case COMPLETED:
                    canvas.drawColor(Color.argb(130, 16, 16, 16));
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    drawVerticalCenteredText(canvas, paint, "GAME COMPLETED", 0);
                    drawVerticalCenteredText(canvas, paint, "CONGRATULATION!!!", 50);
                    break;
            }

            runner.draw(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);      //prikaz grafike nakon crtanja
        }
    }

    private void drawFps(){
        paint.setTextSize((int)(gameWidth * 0.025));
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        canvas.drawText("FPS:" + fps, 20, 40, paint);
    }

    public void pause(){
        try{
            running = false;
            gameThread.join();          //glavna nit nece poceti sa izvrsavanjem dok pozadinska ne zavrsi svoju logiku
        }
        catch (InterruptedException exc){
            Log.e("game loop error", exc.getMessage());
        }

    }

    public void resume(){
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

   @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_UP:

                //kad korisnik klikne na start igra se pokrece
                if(gameState == GameState.PRE_START){
                    boolean pressed = startButton.delegateTouch(event.getX() * scaleFactor, event.getY() * scaleFactor, action);

                    if(pressed){
                        gameState = GameState.STARTED;
                        gameSpeed = 1;
                        runner.startRunning();
                    }
                }
                else if(gameState == GameState.GAME_OVER){
                    boolean pressed = restartButton.delegateTouch(event.getX() * scaleFactor, event.getY() * scaleFactor, action);

                    if(pressed){
                        //ako igra zavrsi i korisnik klikne na restart game igra pocinje ponovo
                        restartGame();
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:

                switch (gameState){
                    case PRE_START:
                        startButton.delegateTouch(event.getX() * scaleFactor, event.getY() * scaleFactor, action);
                        break;
                    case STARTED:
                        runner.jump();
                        break;
                    case GAME_OVER:
                        restartButton.delegateTouch(event.getX() * scaleFactor, event.getY() * scaleFactor, action);
                        break;
                }

        }

        return true;
    }

    public void drawVerticalCenteredText(Canvas canvas, Paint paint, String text, int verticalOffset){

        paint.setTypeface(customFont);
// ubacuje u bounds
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        int y = (canvas.getHeight() / 2) - (bounds.height() / 2 - verticalOffset);
//poboljsava izgled okvira dugmeta
        paint.setAntiAlias(true);
        canvas.drawText(text, x, y, paint);
    }

    private void restartGame(){

        enemies.resetAllEnemies();
        gameSpeed = 1f;
        gameState = GameState.STARTED;
        runner.startRunning();
    }

    /****************************SAVE AND LOAD HIGH SCORE**************/

    private void saveHighScore(int highScore){
        SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("high_score", highScore);

        editor.apply();
    }

    private int loadHighScore(){

        SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
        return settings.getInt("high_score", 0);
    }

    private void drawHighScore(int highScore){

        String highScoreText = "High Score: " + highScore;

        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(highScoreText, 0, highScoreText.length(), bounds);

        canvas.drawText(highScoreText, gameWidth - bounds.width() - (int) (gameWidth * 0.02), 40, paint);
    }

    private void drawScore(int score){
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawText("Score: " + score + "/9999", 20, 40, paint);

    }

}
