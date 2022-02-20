package com.link.jumpandrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemies {

    public static class Enemy extends GameObject {

        private static final double VELOCITY = 0.6;         //da bi se kretalo brzinom kao zemlja
        private Bitmap bitmap;
        private Rect bitmapPosition;

        public Rect getBitmapPosition(){
            return bitmapPosition;
        }

        private Enemy(Context context, int gameWidth, int gameHeight, int bitmapResourceId, int desiredHeight){
            super(context, gameHeight, gameWidth);

            this.bitmap = decodeSampledBitmapFromResource(context.getResources(), bitmapResourceId, -1, desiredHeight);

            bitmapPosition = new Rect(gameWidth, (int) (gameHeight * 0.85) - bitmap.getHeight(), gameWidth + bitmap.getWidth(), (int) (gameHeight * 0.85));
        }

        private int offset = 0;
        private double offsetInternal = 0;
        boolean outOfBounds = false;        //definise da li je protivnik izvan granica displeja
        boolean active = false;

        //pomjeranje neprijetalja s desna na lijevo
        void update(double delta, float gameSpeed){

            if(Math.abs(offset) <= gameWidth + bitmap.getWidth()){
                offsetInternal = offsetInternal - (VELOCITY * delta * gameSpeed);
                offset = (int) (offsetInternal);

                bitmapPosition.left = gameWidth + offset;       //stavlja se plus jer je offset negativan
                bitmapPosition.right = bitmap.getWidth() + (gameWidth + offset);
            }
            else{
                reset();
            }
        }

        void draw(Canvas canvas){
            canvas.drawBitmap(bitmap, null, bitmapPosition, null);
        }

        //vraca neprijatelja izvan desne strane ekrana i vraca vrijednosti na nulu
        void reset(){

            offset = 0;
            offsetInternal = 0;
            outOfBounds = false;
            active = false;

            bitmapPosition.left = gameWidth;
            bitmapPosition.right = gameWidth + bitmap.getWidth();
        }

    }

    public static class Factory{

        Context context;
        int gameWidth;
        int gameHeight;

        private static final int TOTAL_ENEMIES = 5;     //5 vrsta neprijatelja

        private static final double[] ENEMIES_RELATIVE_HEIGHT = {0.12, 0.12, 0.11, 0.14, 0.16};

        private int ENEMY_INDEX;

        private List<Enemy> enemyPool;

        private List<Enemy> getEnemyPool(){
            return enemyPool;
        }

        public Factory(Context context, int gameWidth, int gameHeight){

            this.context = context;
            this.gameWidth = gameWidth;
            this.gameHeight = gameHeight;

            enemyPool = new ArrayList<>();

            //ubacivanje slika u listu neprijatelja
            for (int i = 0; i < TOTAL_ENEMIES; i++) {

                int bitmapResourceId = context.getResources().getIdentifier("obstacle" + (i + 1), "drawable", context.getPackageName());

                enemyPool.add(new Enemy(context, gameWidth, gameHeight, bitmapResourceId, (int) (gameHeight * ENEMIES_RELATIVE_HEIGHT[i])));
            }
        }

        //nasumicno biranje jednog od protivnika
        public Enemy getRandomObstacle(){
            Random rnd = new Random();
            ENEMY_INDEX = rnd.nextInt(enemyPool.size());

            return enemyPool.get(ENEMY_INDEX);
        }
    }

    /**********************ENEMIES MAIN CLASS LOGIC************************/

    private Context context;
    private int gameHeight;
    private int gameWidth;
    private Enemies.Factory enemiesFactory;

    private List<Enemy> enemies;

    public List<Enemy> getEnemies(){
        return enemies;
    }

    public void resetAllEnemies(){
        for(Enemy obstacle : enemies){
            obstacle.reset();
        }
    }

    public Enemies(Context context, int gameWidth, int gameHeight){

        this.context = context;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        enemiesFactory = new Factory(context, gameWidth, gameHeight);
        enemies = enemiesFactory.getEnemyPool();
    }

    private int control = 0;            //definisanje novog neprijatelja u update metodi
    private long deltaCounter = 0;
    private Random random = new Random();

    public void update(long delta, float gameSpeed){

        //suma vremana kadrova
        deltaCounter += delta;

        //kontrolisanje vremena za izlazak protivnika
        if(control == 0){
            control = random.nextInt((int) (1000 / gameSpeed)) + (int) (800 / gameSpeed);
        }

        //kada suma deltaCounter(16) predje ili bude jednaka controlu
        if(deltaCounter >= control){

            Enemy enemy = enemiesFactory.getRandomObstacle();
            enemy.active = true;

            control = 0;
            deltaCounter = 0;
        }

        //update svaku prepreku
        //loop koji iterira kroz listu prepreka i update
        for(Enemy enemy : enemies){
            //
            if(enemy.active){
                enemy.update(delta, gameSpeed);
            }
        }
    }

    public void draw(Canvas canvas){
        for(Enemy enemy : enemies){
            if(enemy.active){
                enemy.draw(canvas);
            }
        }
    }

}
