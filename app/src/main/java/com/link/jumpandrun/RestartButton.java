package com.link.jumpandrun;

import android.content.Context;
import android.media.SoundPool;

public class RestartButton extends GameButton{

    public RestartButton(Context context, int gameWidth, int gameHeight, SoundPool soundPool) {
        super(context, gameWidth, gameHeight, soundPool);

        drawableResources = R.drawable.restart_button;

        init();
    }
}
