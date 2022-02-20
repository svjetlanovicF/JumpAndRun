package com.link.jumpandrun;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;

public abstract class GameObject {

    Context context;
    int gameWidth;
    int gameHeight;

    int bitmapWidth;
    int bitmapHeight;


    public GameObject(Context context, int gameHeight, int gameWidth) {
        this.context = context;
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
    }

    //Zaduzena je za citanje odredjenog grafickog resursa iz drawable foldera
    //i za isporuku objekta tipa Bitmap, koji je moguce koristiti za crtanje
    //na Canvas elementu
    Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);          //vraca velicinu slike u options

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        float ratio = (float) imageWidth / (float) imageHeight;

        if (reqHeight == -1){
            reqHeight = (int)(reqWidth / ratio);
        }
        else if(reqWidth == -1){
            reqWidth = (int)(reqHeight * ratio);
        }

        bitmapWidth = reqWidth;
        bitmapHeight = reqHeight;

        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        //vraca bitmap sa zeljenom visinom i sirinom
        return Bitmap.createScaledBitmap(bitmap,reqWidth, reqHeight, false);
    }

}
