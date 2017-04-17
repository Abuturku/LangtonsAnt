package de.andreasschick.langton.application;

public class Position {

    public short getxPosition() {
        return xPosition;
    }

    public short getyPosition() {
        return yPosition;
    }

    public short getAmountOfVisits() {
        return amountOfVisits;
    }

    private short xPosition;
    private short yPosition;
    private short amountOfVisits;

    public Position(short xPosition, short yPosition, short amountOfVisits) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.amountOfVisits = amountOfVisits;
    }

	public Position(int xPosition, int yPosition, int amountOfVisits) {
		this.xPosition = (short)xPosition;
        this.yPosition = (short)yPosition;
        this.amountOfVisits = (short)amountOfVisits;
	}
}
