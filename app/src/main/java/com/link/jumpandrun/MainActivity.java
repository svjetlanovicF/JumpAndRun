package com.link.jumpandrun;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    FrameLayout mainContainer;
    GameView gameView;

    private static final float GAME_WIDTH = 800;
    private float scaleFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Display display = getWindowManager().getDefaultDisplay();       //uzima sirinu i visinu ekrana odnosno displeja
        Point size = new Point();
        display.getSize(size);          //uzima velicinu ekrana iz display objekta i upisuje u size tipa Point

        double screenRatio = (size.x * 1.0) / (size.y * 1.0);       //odnos stranica

        //posto je nas koordinantni sistem razlicit od k.r. sistema displeja, sluzi za odredjivanje gdje je korisnik kliknuo na ekranu u app
        scaleFactor = GAME_WIDTH / size.x;

        ViewGroup.LayoutParams layoutParams  = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //postavljanje sirine i visine gameview objekta
        gameView = new GameView(this, (int)GAME_WIDTH, (int)(GAME_WIDTH/screenRatio), scaleFactor);      //prosljedivanje sirine i visine

        mainContainer = (FrameLayout) findViewById(R.id.main_container);
        mainContainer.addView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}