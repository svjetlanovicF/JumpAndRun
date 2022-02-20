package com.link.jumpandrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.SoundPool;
import android.util.Log;

public class Runner extends GameObject{

    //sprite je dio slike

    private Bitmap runnerSpritesheet;       //slicica
    private int currentSpriteIndex;         //trenutni indeks slicice

    private Rect currentSprite;     //trenutni sprajt
    private Rect spritePosition;    //trenutna pozicija sprajta na displeju


    public Rect getSpritePosition(){
        return spritePosition;
    }


    public enum RunnerState{
        STANDING, RUNNING, JUMPING
    }

    private RunnerState runnerState;

    private static final int BMP_COLUMNS = 9;       //9 sprajtova

    private int spriteHeight;       //visina sprajta
    private int spriteWidth;        //sirina sprajta

    private int jumpTopLimit;       //limit skoka
    private int initialTop;

//    int gameHeight, gameWidth;

    SoundPool soundPool;
    private int jumpSoundId;

    public Runner(Context context, int gameWidth, int gameHeight, SoundPool soundPool) {
        super(context, gameHeight, gameWidth);

        this.soundPool = soundPool;
        jumpSoundId = soundPool.load(context, R.raw.jump, 1);

        spriteWidth = (gameHeight / 8);

        runnerSpritesheet = decodeSampledBitmapFromResource(context.getResources(), R.drawable.runner, (spriteWidth * BMP_COLUMNS), -1);

        spriteHeight = bitmapHeight;

        currentSpriteIndex = 0;

        initialTop = gameHeight - spriteHeight - (int) (gameHeight * 0.15); //podesavanje trkaca da stoji na stazi
        jumpTopLimit = (gameHeight / 3);

        //uzimanje velicine prvog sprajta(lijevo je nula jer uzimamo prvi sprajt, a spritewidth je debljina tog sprajta)
        currentSprite = new Rect(0, 0, spriteWidth, spriteHeight);
        //odredjuje poziciju igraca na ekranu
        spritePosition = new Rect((int) (gameWidth * 0.1), initialTop, spriteWidth + (int) (gameWidth * 0.1), gameHeight - (int) (gameHeight*0.15));

        //pocetno stanje igraca
        runnerState = RunnerState.STANDING;

    }

    private float deltaCounter = 0;

    public void update(long delta, float gameSpeed){

        //suma svih delti
        deltaCounter += delta;

        float period = ((100) - (10 * gameSpeed));    //90 milisekundi

        switch (runnerState){
            case STANDING:

                //Smjenjivanje prvog i drugog sprajta nakon 90milisekundi(period)
                if(deltaCounter / period > 1){
                    if(currentSpriteIndex < 1){
                        currentSpriteIndex++;
                    }
                    else{
                        currentSpriteIndex = 0;
                    }
                    deltaCounter = deltaCounter - period;
                }

                //update trenutnog sprajta
                currentSprite.left = currentSpriteIndex * spriteWidth;
                currentSprite.right = currentSprite.left + spriteWidth;

                //update pozicije na ekranu
                spritePosition.top = gameHeight - spriteHeight - (int)(gameHeight * 0.15);
                spritePosition.bottom = gameHeight - (int)(gameHeight * 0.15);

                break;
            case RUNNING:

                //Smjenjivanje od drugog do petog sprajta nakon 90milisekundi(period)
                if(deltaCounter / period > 1){
                    if(currentSpriteIndex < 5){
                        currentSpriteIndex++;
                    }
                    else{
                        currentSpriteIndex = 2;
                    }
                    deltaCounter = deltaCounter - period;
                }

                //update trenutnog sprajta
                currentSprite.left = currentSpriteIndex * spriteWidth;
                currentSprite.right = currentSprite.left + spriteWidth;

                //update pozicije na ekranu
                spritePosition.top = gameHeight - spriteHeight - (int)(gameHeight * 0.15);
                spritePosition.bottom = gameHeight - (int)(gameHeight * 0.15);

                break;

            case JUMPING:

                if((deltaCounter / period) <= 2){

                    currentSpriteIndex = 7;

                    currentSprite.left = currentSpriteIndex * spriteWidth;
                    currentSprite.right = currentSprite.left + spriteWidth;
                    //Pozicija igraca na ekranu raste srazmjerno sa deltaCounter sve dok je deltaCounter < 180 (millisekundi)
                    //razmak pozicije glave igraca od zadnje pozicije na svakih 16 milliseconds
                    float newTop = ((initialTop - jumpTopLimit) * (deltaCounter / (period * 2)));

                    //update pozicije igraca prilikom skoka
                    spritePosition.left = (int) (gameWidth * 0.1);
                    spritePosition.top = initialTop - (int) newTop;  // Oduzimanje pozocije glave igraca od razmaka (iznad opisano)
                    spritePosition.right = spriteWidth + (int)(gameWidth * 0.1);
                    spritePosition.bottom = (spritePosition.top + spriteHeight);


                }
                //prelazi u stanje mirovanja od 180millisekundi do 270millisekundi.
                else if((deltaCounter / period) > 2 && (deltaCounter / period) <= 3) {

                    if(spritePosition.top < jumpTopLimit){
                        float newTop = ((initialTop - jumpTopLimit));
                        spritePosition.top = initialTop - (int) newTop;
                    }

                }
                else if((deltaCounter / period) > 3 && (deltaCounter / period) <= 5){
// namjesta sprite za spustanje
                    currentSpriteIndex = 8;

                    currentSprite.left = currentSpriteIndex * spriteWidth;
                    currentSprite.right = currentSprite.left + spriteWidth;

                    float newTop = ((initialTop - jumpTopLimit) * ((deltaCounter - (period * 3)) / (period * 2)));
//low down the runner
                    spritePosition.left = (int) (gameWidth * 0.1);
                    spritePosition.top = jumpTopLimit + (int) newTop;
                    spritePosition.right = spriteWidth + (int) (gameWidth * 0.1);
                    spritePosition.bottom = (spritePosition.top + spriteHeight);

                }
                //skok je zavrsen, vraca se igrac u fazu trcanja
                else{
                    runnerState = RunnerState.RUNNING;
                    currentSpriteIndex = 2;
                    deltaCounter = deltaCounter - (period * 5); // ???
                }

                break;
        }
    }

    public void draw(Canvas canvas) {
        Log.e("crtanje", "prije crtanja");
        canvas.drawBitmap(runnerSpritesheet, currentSprite, spritePosition, null);
        Log.e("crtanje", "poslije crtanja" + spritePosition.top + " " + spritePosition.bottom + " " + spritePosition.left + " " + spritePosition.right);
    }

    public void jump(){

        if(runnerState == RunnerState.RUNNING){
            deltaCounter = 0;       //kontrolise stanje skoka
            runnerState = RunnerState.JUMPING;
            //pustanje zvuka
            soundPool.play(jumpSoundId, 1, 1, 1, 0, 1.0f);
        }
    }

    public void startRunning(){
        Log.e("tag", "pocinje trcati");
        runnerState = RunnerState.RUNNING;

    }

    public void stopRunning(){
        runnerState = RunnerState.STANDING;
    }

}
