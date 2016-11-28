package de.andreasschick.langton.hsqldb;

public class MovementRow {

    private int antId;
    private int xPosition;
    private int yPosition;
    private int movementIndex;

    public MovementRow(int antId, int xPosition, int yPosition, int movementIndex) {
        this.antId = antId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.movementIndex = movementIndex;
    }

    public int getAntId() {
        return antId;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getMovementIndex() {
        return movementIndex;
    }
}
