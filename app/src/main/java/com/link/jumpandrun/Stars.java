package com.link.jumpandrun;

import android.content.Context;

public class Stars extends InfiniteScrollingBackground{

    public Stars(Context context, int gameWidth, int gameHeight) {
        super(context, gameWidth, gameHeight);

        this.VELOCITY = 0.02;
        this.drawableResource = R.drawable.stars;
        this.bitmapVerticalAlligment = BitmapVerticalAlligment.TOP;
        this.relativeHeight = 0.7;
        this.relativeVerticalOffset = 0;

        init();
    }
}
