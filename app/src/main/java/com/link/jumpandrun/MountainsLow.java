package com.link.jumpandrun;

import android.content.Context;

public class MountainsLow extends InfiniteScrollingBackground{

    public MountainsLow(Context context, int gameWidth, int gameHeight) {
        super(context, gameWidth, gameHeight);

        this.VELOCITY = 0.1;
        this.drawableResource = R.drawable.mountains_low;
        this.bitmapVerticalAlligment = BitmapVerticalAlligment.BOTTOM;
        this.relativeHeight = 0.3;
        this.relativeVerticalOffset = 0.1;

        init();
    }
}
