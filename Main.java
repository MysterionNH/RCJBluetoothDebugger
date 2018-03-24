package com.niklashalle;

// TODO: switch to event driven structure

public class Main {
    public static void main(String[] args) {
        Visualizer visualizer;
        FifoAdapter adapter = new FifoAdapter();
        if (!adapter.isConnected()) return;
        adapter.start();

        visualizer = new Visualizer();
        while (!visualizer.isEnded()) {
            long frameTime = System.currentTimeMillis();
            visualizer.drawBackground();
            for (int i = 0; i < 10; ++i) {
                FifoAdapter.JsonData data = adapter.getJsonData(i);
                if (data == null || !data.isReal()) continue;

                // robot position is currently always at 0,0, ball relative to that - no absolute positions
                visualizer.drawBallLine(data.robotPositionX, data.robotPositionY, data.ballPositionY * 10, -data.ballPositionX * 10);
                visualizer.drawRobot(data.robotPositionX, data.robotPositionY,
                        data.opponentGoal == 1 ? data.robotRotation + 180 : -data.robotRotation,
                        data.robotNumber == 1 ? "Blue" : data.robotNumber == 2 ? "Orange" : String.valueOf(data.robotNumber));
                visualizer.drawBall(data.ballPositionY * 10, -data.ballPositionX * 10);
            }
            visualizer.drawField();
            visualizer.updateScreen();
            while (System.currentTimeMillis() - frameTime < 20);
        }

        System.exit(0);
    }
}
