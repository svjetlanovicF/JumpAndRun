package com.link.jumpandrun;

import android.graphics.Rect;

public class Collision {

    public static boolean detectCollision(Runner runner, Enemies.Enemy obstacle){

        Rect runnerPosition = runner.getSpritePosition();
        Rect originalObstaclePosition = obstacle.getBitmapPosition();

        Rect obstaclePosition = new Rect();
        obstaclePosition.top = (int)(originalObstaclePosition.top*1.1);
        obstaclePosition.left = (int)(originalObstaclePosition.left*1.25);
        obstaclePosition.right = (int) (originalObstaclePosition.right*0.75);
        obstaclePosition.bottom = originalObstaclePosition.bottom;

        //detektuje preklapanje dva pravougaonika(igraca i neprijatelja)
        return Rect.intersects(runnerPosition, obstaclePosition);
    }


}
