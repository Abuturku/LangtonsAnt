package de.andreasschick.langton.application;

import de.andreasschick.langton.hsqldb.HSQLDB;
import de.andreasschick.langton.hsqldb.MovementRow;

import java.sql.SQLException;
import java.util.ArrayList;

public class Ant {
    private int antId;
    private int applicationId;
	private  ArrayList<MovementRow> movements;
    private int startX;
    private int startY;

    public Ant(int antId, int applicationId, int maxStep) throws SQLException {
        this.antId = antId;
        HSQLDB hsqldb = HSQLDB.getInstance();
        movements = hsqldb.getAntMovements(antId, applicationId, maxStep);
    }

    public int getAntId() {
		return antId;
	}
    
    public ArrayList<MovementRow> getMovements() {
        return movements;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }
}
