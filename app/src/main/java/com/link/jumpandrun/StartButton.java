package com.link.jumpandrun;

import android.content.Context;
import android.media.SoundPool;

public class StartButton extends GameButton{
    public StartButton(Context context, int gameWidth, int gameHeight, SoundPool soundPool) {
        super(context, gameWidth, gameHeight, soundPool);

        drawableResources = R.drawable.start_button;

        init();
    }
}
