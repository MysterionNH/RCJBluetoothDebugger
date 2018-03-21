package com.niklashalle;

public class Main {
    public static void main(String[] args) {
        (new Thread(() -> {
            Visualizer visualizer;
            FifoAdapter adapter = new FifoAdapter();
            if (!adapter.isConnected()) return;
            adapter.start();

            visualizer = new Visualizer();
            while (!visualizer.isEnded()) {
                visualizer.drawBackground();
                for (int i = 0; i < 10; ++i) {
                    FifoAdapter.JsonData data = adapter.getJsonData(i);
                    if (data == null || !data.isReal()) continue;

                    visualizer.drawBallLine(data.robotPositionX, data.robotPositionY, data.ballPositionX, data.ballPositionY);
                    visualizer.drawRobot(data.robotPositionX, data.robotPositionY, data.robotRotation,
                            data.robotNumber == 1 ? "Blue" : data.robotNumber == 2 ? "Orange" : String.valueOf(data.robotNumber));
                    visualizer.drawBall(data.ballPositionX, data.ballPositionY);
                }
                visualizer.drawField();
                visualizer.updateScreen();
            }
        })).start();
    }
}
