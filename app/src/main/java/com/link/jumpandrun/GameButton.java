package com.link.jumpandrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.SoundPool;
import android.view.MotionEvent;

public abstract class GameButton extends GameObject{

    private Bitmap bitmap;
    private Rect bitmapPosition;

    private boolean pressed;

    int drawableResources;

    SoundPool soundPool;
    private int clickSoundId;

    public GameButton(Context context, int gameWidth , int gameHeight, SoundPool soundPool) {

        super(context, gameHeight, gameWidth);
        this.soundPool = soundPool;
    }

    private int initialWidth;
    private int initialHeight;

    void init(){
        bitmap = decodeSampledBitmapFromResource(context.getResources(),drawableResources, (int) (gameWidth*0.3), -1);

        //odredjuje poziciju dugmeta na ekranu
        bitmapPosition = new Rect();
        bitmapPosition.left = gameWidth / 2 - bitmap.getWidth() / 2;
        bitmapPosition.right = bitmapPosition.left + bitmap.getWidth();
        bitmapPosition.top = (int) (gameHeight / 1.6) - bitmap.getHeight() / 2;
        bitmapPosition.bottom = bitmapPosition.top + bitmap.getHeight();

        //inicijalne vrijednosti visine i sirine
        initialWidth = bitmap.getWidth();
        initialHeight = bitmap.getHeight();

        clickSoundId = soundPool.load(context, R.raw.click, 1);
    }

    public void update(){

        if(pressed){

            //dugme se umanji kad se stisne radi bolje interakcije
            bitmapPosition.left = (int) ((gameWidth / 2 - (initialWidth * 0.9) / 2));
            bitmapPosition.right = bitmapPosition.left + (int) (initialWidth * 0.9);
            bitmapPosition.top = (int) (((int) (gameHeight /1.6) - (int) (initialHeight * 0.9) / 2));
            bitmapPosition.bottom = bitmapPosition.top + (int)(initialHeight * 0.9);
        }
        else {

            //inicijalna pozicija dugmeta
            bitmapPosition.left = gameWidth / 2 - bitmap.getWidth() / 2;
            bitmapPosition.right = bitmapPosition.left + bitmap.getWidth();
            bitmapPosition.top = (int) (gameHeight / 1.6) - bitmap.getHeight() / 2;
            bitmapPosition.bottom = bitmapPosition.top + bitmap.getHeight();
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, bitmapPosition, null);
    }

    //
    public boolean delegateTouch(float x, float y, int action){

        boolean inBounds = false;

        //metoda contains provjerava da li je korisnik kliknuo na dugme
        if(bitmapPosition.contains((int) x, (int) y) && action == MotionEvent.ACTION_DOWN){
            inBounds = true;
            pressed = true;
        }
        else if(bitmapPosition.contains((int) x, (int) y ) && action == MotionEvent.ACTION_UP){
            inBounds = true;
            pressed = false;

            soundPool.play(clickSoundId, 1, 1, 1, 0, 1.0f);
        }
        else{
            pressed = false;
        }
        return  inBounds;
    }
}
