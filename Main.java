package com.niklashalle;

public class Main {
    private  static long frameTime;

    public static void main(String[] args) {
        Visualizer visualizer;
        FifoAdapter adapter = new FifoAdapter();
        if (!adapter.isConnected()) return;
        adapter.start();

        visualizer = new Visualizer();
        while (!visualizer.isEnded()) {
            frameTime = System.currentTimeMillis();
            visualizer.drawBackground();
            for (int i = 0; i < 10; ++i) {
                FifoAdapter.JsonData data = adapter.getJsonData(i);
                if (data == null || !data.isReal()) continue;

                // robot position is currently always at 0,0, ball relative to that - no absolute positions
                visualizer.drawBallLine(data.robotPositionX, data.robotPositionY, data.ballPositionX, data.ballPositionY);
                visualizer.drawRobot(data.robotPositionX, data.robotPositionY,
                        data.opponentGoal == 1 ? data.robotRotation + 180 : data.robotRotation,
                        data.robotNumber == 1 ? "Blue" : data.robotNumber == 2 ? "Orange" : String.valueOf(data.robotNumber));
                visualizer.drawBall(data.ballPositionX, data.ballPositionY);
            }
            visualizer.drawField();
            visualizer.updateScreen();
            while (System.currentTimeMillis() - frameTime < 16);
        }

        System.exit(0);
    }
}
