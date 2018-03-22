package com.niklashalle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FifoAdapter {
    private BufferedReader mBufferedReader;
    private boolean connected = false;

    private JsonData[] robots = new JsonData[10];

    FifoAdapter() {
        initialize();
        if (connected) {
            System.out.println("Named pipe open, ready for parsing incoming data");
        } else {
            System.err.println("Failed to open named pipe");
        }
    }

    private void initialize() {
        try {
            System.out.println("Waiting for pipe...");
            mBufferedReader = new BufferedReader(new FileReader("/tmp/fifoBlueJava"));
        } catch (IOException e) {
            return;
        }

        connected = true;
    }

    public void start() {
        if (connected) {
            (new Thread(() -> {
                while (true) {
                    try {
                        parseJson(mBufferedReader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            })).start();
        }
    }

    private void parseJson(String msg) {
        //System.out.println(msg);
        JSONObject jsonRoot = null;
        JSONParser parser = new JSONParser();
        try {
            jsonRoot = (JSONObject) parser.parse(msg);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JsonData temp = new JsonData(true);

        temp.header = (String) jsonRoot.get("header");
        temp.version = (String) jsonRoot.get("version");

        JSONObject teamRoot = (JSONObject) ((JSONObject) jsonRoot.get("data")).get("team");
        temp.teamId = (Long) teamRoot.get("id");
        temp.teamNumber = (Long) teamRoot.get("number");

        JSONObject robotRoot = (JSONObject) ((JSONObject) jsonRoot.get("data")).get("robot");
        temp.robotNumber = (Long) robotRoot.get("number");

        JSONObject robotPositionRoot = (JSONObject) robotRoot.get("position");
        temp.robotPositionX = (Double) robotPositionRoot.get("x");
        temp.robotPositionY = (Double) robotPositionRoot.get("y");
        temp.robotRotation  = (Double) robotPositionRoot.get("t");

        JSONObject robotVelocityRoot = (JSONObject) robotRoot.get("velocity");
        temp.robotVelocityX = (Double) robotPositionRoot.get("x");
        temp.robotVelocityY = (Double) robotPositionRoot.get("y");

        temp.robotStatus = (Long) robotRoot.get("status");
        temp.robotRole = (Long) robotRoot.get("role");

        JSONObject ballRoot = (JSONObject) ((JSONObject) jsonRoot.get("data")).get("ball");
        temp.ballStatus = (Long) ballRoot.get("status");
        temp.ballAge = (Long) ballRoot.get("age");

        // yes, switched, don't ask
        temp.ballPositionX = -(Double) ((JSONObject) ballRoot.get("position")).get("y");
        temp.ballPositionY = -(Double) ((JSONObject) ballRoot.get("position")).get("x");

        temp.ballVelocityX = (Double) ((JSONObject) ballRoot.get("velocity")).get("x");
        temp.ballVelocityY = (Double) ((JSONObject) ballRoot.get("velocity")).get("y");

        temp.timestamp = (Long) jsonRoot.get("timestamp");

        temp.ballAngle          = (Double)  ((JSONObject) jsonRoot.get("customData")).get("ballAngle");
        temp.ballDistance       = (Double)  ((JSONObject) jsonRoot.get("customData")).get("ballDistance");
        temp.macauStatus        = (Boolean) ((JSONObject) jsonRoot.get("customData")).get("macauStatus");
        temp.macauEnabled       = (Boolean) ((JSONObject) jsonRoot.get("customData")).get("macauEnabled");
        temp.lightbarrierStatus = (Boolean) ((JSONObject) jsonRoot.get("customData")).get("lightbarrierStatus");
        temp.opponentGoal       = (Long)    ((JSONObject) jsonRoot.get("customData")).get("opponentGoal");

        robots[(int)temp.robotNumber] = temp;
    }

    public boolean isConnected() {
        return connected;
    }

    public JsonData getJsonData(int id) {
        return robots[id];
    }

    public class JsonData {
        String header;
        String version;
        long teamId;
        long teamNumber;
        long robotNumber;
        double robotPositionX;
        double robotPositionY;
        double robotRotation;
        double robotVelocityX;
        double robotVelocityY;
        long robotStatus;
        long robotRole;
        long ballStatus;
        long ballAge;
        double ballPositionX;
        double ballPositionY;
        double ballVelocityX;
        double ballVelocityY;
        long timestamp;

        // custom data
        double ballAngle;
        double ballDistance;
        boolean macauStatus;
        boolean macauEnabled;
        boolean lightbarrierStatus;
        long opponentGoal;

        // not part of the protocol
        private boolean real = false;

        JsonData() {}

        JsonData(boolean real) {
            this.real = real;
        }

        public boolean isReal() {
            return real;
        }
    }
}
