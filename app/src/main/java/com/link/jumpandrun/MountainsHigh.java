package com.link.jumpandrun;

import android.content.Context;

public class MountainsHigh extends InfiniteScrollingBackground{

    public MountainsHigh(Context context, int gameWidth, int gameHeight) {
        super(context, gameWidth, gameHeight);

        this.VELOCITY = 0.05;
        this.drawableResource = R.drawable.mountains_high;
        this.bitmapVerticalAlligment = BitmapVerticalAlligment.BOTTOM;
        this.relativeHeight = 0.5;
        this.relativeVerticalOffset = 0.1;

        init();
    }
}
