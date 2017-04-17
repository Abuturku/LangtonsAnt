package de.andreasschick.langton.application;

public class Application {
    private int id;
    private byte numberOfAnts;
    private String movementRule;
    private int tableauSize;

    public Application(byte numberOfAnts, String movementRule, int tableauSize, int applicationId) {
        this.id = applicationId;
        this.numberOfAnts = numberOfAnts;
        this.movementRule = movementRule;
        this.tableauSize = tableauSize;
    }

    public byte getNumberOfAnts() {
        return numberOfAnts;
    }

    public String getMovementRule() {
        return movementRule;
    }

    public int getTableauSize() {
        return tableauSize;
    }

    public int getId() {
        return id;
    }
}
