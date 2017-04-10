package de.andreasschick.langton.application;

public enum DriftDirection {
    UPRIGHT (1, -1), DOWNRIGHT (1,1), UPLEFT(-1, -1), DOWNLEFT(-1, 1);

    public int getxDirection() {
        return xDirection;
    }

    public int getyDirection() {
        return yDirection;
    }

    private int xDirection;
    private int yDirection;

    DriftDirection(int xDirection, int yDirection) {
        this.xDirection=xDirection;
        this.yDirection=yDirection;
    }
}
