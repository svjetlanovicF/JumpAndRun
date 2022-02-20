package com.link.jumpandrun;

import android.content.Context;

public class Ground extends InfiniteScrollingBackground{

    public Ground(Context context, int gameWidth, int gameHeight) {
        super(context, gameWidth, gameHeight);

        this.VELOCITY = 0.6;
        this.drawableResource = R.drawable.ground;
        this.bitmapVerticalAlligment = BitmapVerticalAlligment.BOTTOM;
        this.relativeHeight = 0.3;
        this.relativeVerticalOffset = 0;

        init();
    }
}
