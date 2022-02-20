package com.link.jumpandrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class InfiniteScrollingBackground extends GameObject{

    double VELOCITY;    //brzina pomjeranja podloge
    int drawableResource;   //slika podloge
    double relativeHeight;
    double relativeVerticalOffset;
    BitmapVerticalAlligment bitmapVerticalAlligment;

    //mjerenje ofseta u odnosu na dole ili gore
    public enum BitmapVerticalAlligment{
        TOP, BOTTOM
    }

    private Bitmap[] bitmaps;
    private Rect[] bitmapPositions;

    private double offsetInternal = 0;

    public InfiniteScrollingBackground(Context context, int gameWidth, int gameHeight) {
        super(context,gameHeight,gameWidth);
    }

    void init(){
        bitmaps = new Bitmap[2];
        bitmapPositions = new Rect[2];

        for (int i = 0; i < bitmaps.length; i++) {

            Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), drawableResource, -1, (int)(gameHeight*relativeHeight));
            bitmaps[i] = bitmap;

            Rect bitmapPosition = new Rect();   //koristi se za definisanje pozicije bitmap slike koja je upravo nacrtana unutar nase igre

            //pomjera ekran srazmjerno sa velicinom verticaloffset na prema gore
            if(bitmapVerticalAlligment == BitmapVerticalAlligment.BOTTOM){
                bitmapPosition.bottom = (int)(gameHeight - gameHeight*relativeVerticalOffset);
                bitmapPosition.top = bitmapPosition.bottom - bitmaps[i].getHeight();
                //pomjera ekran srazmjerno sa velicinom verticaloffset na prema dole
            } else if(bitmapVerticalAlligment == BitmapVerticalAlligment.TOP){
                bitmapPosition.top = (int)(relativeVerticalOffset*gameHeight);
                bitmapPosition.bottom = bitmapPosition.top + bitmaps[i].getHeight();
            }

            //smjenjivanje dve slike
            bitmapPosition.left = (i) * bitmaps[i].getWidth();
            bitmapPosition.right = bitmapPosition.left + bitmaps[i].getWidth();

            //upisuje sve 4 pozicije za obe slike
            bitmapPositions[i] = bitmapPosition;
        }

    }

    public void update(long delta, float gameSpeed){

        offsetInternal = offsetInternal - (VELOCITY * delta * gameSpeed);

        //pretvoreno je u int zato sto velocity*delta daje rezultat sa pomicnim zarezom pa je zato tek poslije pretvoreno u int
        int offset = (int) (offsetInternal);

        if(Math.abs(offset) > (bitmaps[0].getWidth())){

            offset = ((bitmaps[0].getWidth()) - Math.abs(offset));
            offsetInternal = offset;

            List<Rect> list = Arrays.asList(bitmapPositions);       //pretvara niz u listu
            Collections.reverse(list);          //obrce clanove liste
            bitmapPositions = (Rect[]) list.toArray();      //pretvara listu u niz
        }

        //pomjeranje slike kadar po kadar ulijevo i namjestanje pozicija obe slike
        //namjesta pozicije samo za lijevo i desno jer se top i bottom ne pomjeraju
        for (int i = 0; i < bitmaps.length; i++) {
            bitmapPositions[i].left = offset + ((i) * (bitmaps[i].getWidth()));
            bitmapPositions[i].right = bitmapPositions[i].left + bitmaps[i].getWidth();
        }
    }

    public void draw(Canvas canvas){
        for (int i = 0; i < bitmaps.length; i++) {
            canvas.drawBitmap(bitmaps[i], null, bitmapPositions[i], null);
        }
    }
}
