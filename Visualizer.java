package com.niklashalle;

import edu.princeton.cs.introcs.Draw;
import edu.princeton.cs.introcs.DrawListener;

import java.awt.Color;

@SuppressWarnings("FieldCanBeLocal")
public class Visualizer {
    // all lengths in mm
    private final double CANVAS_BORDER = 2000;

    private final Color BLACK  = new Color(0x000000);
    private final Color WHITE  = new Color(0xFFFFFF);
    private final Color BLUE   = new Color(0x0000FF);
    private final Color YELLOW = new Color(0xFFFF00);

    // field
    private final Color WEDGE_GREEN = new Color(0x008844);
    private final Color FIELD_GREEN = new Color(0x328700);

    private final double FIELD_WIDTH_HALF  = 24300/2d;
    private final double FIELD_HEIGHT_HALF = 18200/2d;
    private final double WIDTH_HEIGHT_RATIO = FIELD_WIDTH_HALF / FIELD_HEIGHT_HALF;

    private final double WEDGE_WIDTH_HALF  = FIELD_WIDTH_HALF  - 1000;
    private final double WEDGE_HEIGHT_HALF = FIELD_HEIGHT_HALF - 1000;

    private final double INNER_WIDTH_HALF  = FIELD_WIDTH_HALF  - 3000;
    private final double INNER_HEIGHT_HALF = FIELD_HEIGHT_HALF - 3000;

    private final double PENALTY_WIDTH_HALF  = 3000/2d;
    private final double PENALTY_HEIGHT_HALF = 9000/2d;
    private final double PENALTY_X = FIELD_HEIGHT_HALF - PENALTY_WIDTH_HALF;

    private final double GOAL_WIDTH_HALF  =  740/2d;
    private final double GOAL_HEIGHT_HALF = 6000/2d;
    private final double GOAL_X = INNER_WIDTH_HALF + GOAL_WIDTH_HALF;

    private final double NEUTRAL_X = INNER_WIDTH_HALF - 4500;
    private final double NEUTRAL_Y = GOAL_HEIGHT_HALF;

    private final double MIDDLE_CIRCLE_RADIUS = 6000/2d;

    // robot
    private final double ROBOT_RADIUS = 1100;
    private final double ROBOT_CAPZONE_DEPTH = 250;

    // ball
    private final Color ORANGE = new Color(0xFF8000);
    private final double BALL_RADIUS = 650/2d;

    // canvas
    private final int CANVAS_HEIGHT = 800;
    private final int CANVAS_WIDTH = (int) Math.ceil(CANVAS_HEIGHT * WIDTH_HEIGHT_RATIO);

    private Draw mCanvas;
    private double defaultPenRadius;

    private boolean ended = false;

    Visualizer() {
        initializeCanvas();
    }

    private void initializeCanvas() {
        mCanvas = new Draw();

        mCanvas.addListener(new DrawListener() {
            @Override
            public void mousePressed(double x, double y) {

            }

            @Override
            public void mouseDragged(double x, double y) {

            }

            @Override
            public void mouseReleased(double x, double y) {

            }

            @Override
            public void keyTyped(char c) {

            }

            @Override
            public void keyPressed(int keycode) {
                switch (keycode) {
                    case 'q':
                    case 'Q':
                    {
                        ended = true;
                        break;
                    }
                    default: break;
                }
            }

            @Override
            public void keyReleased(int keycode) {

            }
        });

        mCanvas.enableDoubleBuffering();
        //mCanvas.setLocationOnScreen(1040,960);
        mCanvas.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        mCanvas.setXscale(-FIELD_WIDTH_HALF - CANVAS_BORDER, FIELD_WIDTH_HALF + CANVAS_BORDER);
        mCanvas.setYscale(-FIELD_HEIGHT_HALF - CANVAS_BORDER, FIELD_HEIGHT_HALF + CANVAS_BORDER);
        defaultPenRadius = mCanvas.getPenRadius() * 3 / 512;
        mCanvas.setPenRadius(defaultPenRadius);
    }

    void drawBackground() {
        // background
        mCanvas.setPenColor(BLACK);
        mCanvas.filledRectangle(0, 0,
                FIELD_WIDTH_HALF + CANVAS_BORDER, FIELD_HEIGHT_HALF + CANVAS_BORDER);

        // outer
        mCanvas.setPenColor(FIELD_GREEN);
        mCanvas.filledRectangle(0, 0, FIELD_WIDTH_HALF, FIELD_HEIGHT_HALF);

        // wedge
        mCanvas.setPenColor(WEDGE_GREEN);
        mCanvas.filledRectangle(0, 0, WEDGE_WIDTH_HALF, WEDGE_HEIGHT_HALF);

        // inner
        mCanvas.setPenColor(FIELD_GREEN);
        mCanvas.filledRectangle(0, 0, WEDGE_WIDTH_HALF - 500, WEDGE_HEIGHT_HALF - 500);
    }

    void drawRobot(double x, double y, double t, String name) {
        // body
        mCanvas.setPenColor(Color.BLACK);
        mCanvas.filledCircle(x, y, ROBOT_RADIUS);

        // "cap zone"
        mCanvas.setPenColor(FIELD_GREEN);
        mCanvas.setPenRadius(defaultPenRadius*1.65);

        double angle = Math.toRadians(t - 45);

        double x1 = x + ROBOT_RADIUS - BALL_RADIUS - x;
        double y1 = y + ROBOT_RADIUS - BALL_RADIUS - y;

        double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
        double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);

        double newX = x2 + x;
        double newY = y2 + y;

        for (int i = 250; i >= 0; --i) {
            mCanvas.arc(newX, newY, i, t + 112, t - 112);
        }

        // marker
        mCanvas.setPenColor(WHITE);
        mCanvas.text(x, y, name);
    }

    void drawBallLine(double x0, double y0, double x1, double y1) {
        mCanvas.setPenRadius(defaultPenRadius/3);
        mCanvas.setPenColor(ORANGE);
        mCanvas.line(x0, y0, x1, y1);
    }

    void drawBall(double x, double y) {
        mCanvas.setPenColor(ORANGE);
        mCanvas.filledCircle(x, y, BALL_RADIUS);
    }

    void drawField() {
        // penalty area
        mCanvas.setPenRadius(defaultPenRadius);
        mCanvas.setPenColor(BLACK);
        mCanvas.rectangle(-PENALTY_X, 0, PENALTY_WIDTH_HALF, PENALTY_HEIGHT_HALF);
        mCanvas.rectangle(+PENALTY_X, 0, PENALTY_WIDTH_HALF, PENALTY_HEIGHT_HALF);
        mCanvas.show();

        // bound lines
        mCanvas.setPenRadius(defaultPenRadius * 1.5);
        mCanvas.setPenColor(WHITE);
        mCanvas.rectangle(0, 0, INNER_WIDTH_HALF, INNER_HEIGHT_HALF);
        mCanvas.show();

        // goals
        mCanvas.setPenRadius(defaultPenRadius * 0.75);
        mCanvas.setPenColor(BLACK);
        mCanvas.rectangle(+GOAL_X, 0, GOAL_WIDTH_HALF, GOAL_HEIGHT_HALF);
        mCanvas.rectangle(-GOAL_X, 0, GOAL_WIDTH_HALF, GOAL_HEIGHT_HALF);

        // goal colors
        mCanvas.setPenRadius(defaultPenRadius * 1.75);
        mCanvas.setPenColor(BLUE);
        mCanvas.line(-GOAL_X + GOAL_WIDTH_HALF, GOAL_HEIGHT_HALF, -GOAL_X + GOAL_WIDTH_HALF, -GOAL_HEIGHT_HALF);
        mCanvas.setPenColor(YELLOW);
        mCanvas.line(GOAL_X - GOAL_WIDTH_HALF, GOAL_HEIGHT_HALF, GOAL_X - GOAL_WIDTH_HALF, -GOAL_HEIGHT_HALF);
        mCanvas.show();

        // neutral spots
        mCanvas.setPenRadius(defaultPenRadius * 2);
        mCanvas.setPenColor(BLACK);
        mCanvas.point(0, 0);
        mCanvas.point(+NEUTRAL_X, +NEUTRAL_Y);
        mCanvas.point(-NEUTRAL_X, +NEUTRAL_Y);
        mCanvas.point(+NEUTRAL_X, -NEUTRAL_Y);
        mCanvas.point(-NEUTRAL_X, -NEUTRAL_Y);
        mCanvas.show();

        // middle circle
        mCanvas.setPenRadius(defaultPenRadius);
        mCanvas.setPenColor(BLACK);
        mCanvas.circle(0,0, MIDDLE_CIRCLE_RADIUS);
        mCanvas.show();
    }

    void updateScreen() {
        mCanvas.show();
    }

    boolean isEnded() {
        return ended;
    }
}
